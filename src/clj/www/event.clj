(ns www.event
  "Browser event tracking for analytics."
  (:require [util.base62 :as b62]
            [www.request :as req]
            [ring.util.response :as rsp]
            [datomic.api :as d]
            [sistemi.datomic :as sd]
            [taoensso.timbre :as log]
            [selmer.parser :as selmer]
            [analytics.mixpanel :as mixpanel]
            [www.url :as url]))

;; It should degrade gracefully if the transactor is offline.
;; - for writes, attempting to create new browser-id will fail with a 500 error
;;   - javascript just ignores the error
;;   - no cookie is set
;; - for reads, the query will just run against the working set in memory and will often just work

(def ^:private cookie-name "browser-id")

;; Used for cookie max-age in seconds.
(def ^:private one-year (* 365 24 60 60))

(defn- gen-id
  "Generates a new random browser id."
  []
  (util.base62/rand 21)) ;; 126 (21*6) bits of entropy.

;; Use async here since it is faster and we don't need to wait for the result.
(defn- new-browser-async
  "Creates a new browser entity with the specified id."
  [conn id]
  (log/info {:event :browser-id/new :browser-id id})
  (d/transact-async conn [{:db/id (d/tempid sd/partition)
                           :browser/id id}]))

(def ^:private event-response
  "An empty json response for consumption by the client side javascript event sender."
  (-> "42" ; dummy content
      rsp/response
      (rsp/content-type "application/json")))

(defn- handle-event
  [event]
  (log/info event)
  (if (contains? #{:page/load} (:event event))
    (mixpanel/send-event event)))

(defn- valid?
  "Returns true if the browser-id is valid."
  [bid]
  (re-matches #"[a-zA-Z0-9]{21}" bid))

(defn- exists?
  "Returns true if the browser-id exists in the database."
  [conn bid]
  (->> conn
       d/db
       (d/q '[:find ?e
              :in ?bid $
              :where [?e :browser/id ?bid]]
            bid)
       empty?
       not))

(defn- add-cookie
  "Adds the browser-id cookie to the response."
  [rsp bid]
  (rsp/set-cookie rsp cookie-name bid {:secure true
                                       :http-only true
                                       :path "/"
                                       :max-age one-year}))

(defn- browser-id
  "Returns the browser-id associated with a request."
  [req]
  (if-let [bid (get-in req [:cookies cookie-name :value])]
    (if (valid? bid)
      bid
      (log/error {:event :browser-id/invalid :browser-id bid}))))

(defn- new-browser-id
  "Returns a new browser-id."
  [conn]
  (let [bid (gen-id)]
    (new-browser-async conn bid)
    bid))

(defn- handle-event-request
  "Handles a general event logging request and returns a response."
  [{:keys [body] :as req}]
  (let [req (dissoc req :body)
           bid (browser-id req)
           event (if bid
                   (assoc body :bid bid)
                   body)]
    (handle-event (-> event
                      (assoc :url (get-in req [:headers "referer"]) ; assumes called via javascript and referer is containing page
                             :req req))))
  event-response)

(defn wrap-event
  "Manages events. For normal requests, inserts the event handler path
into the request. For event tracking requests, reads the browser id
from a cookie. If no cookie was set, generates a new id and sets the
cookie. The request is then passed to any event handlers and an empty
response is returned."
  [app path conn]
  (let [event-path (str "/" path)]
    (fn [{:keys [uri] :as req}]
      (cond
       ;; page load
       (re-matches #".*\.html?" uri) (let [bid (browser-id req)
                                           new-bid (and (not bid) (new-browser-id conn))
                                           rsp (app (assoc req :event-path event-path))]
                                       (handle-event {:event :page/load
                                                      :bid (or bid new-bid)
                                                      :url (-> req url/new-URL str)
                                                      :referrer (get-in req [:headers "referer"])
                                                      :req req})
                                       (if new-bid
                                         (add-cookie rsp new-bid)
                                         rsp))

       ;; event request
       (= uri event-path) (handle-event-request req)

       ;; delegate other requests to chain
       :else (app req)))))

(defn script
  "Returns html snippet with javascript that makes a request to the
event tracking handler. Include this in pages that should log events
when loaded."
  [req]
  (let [event-path (:event-path req)]
    [:script {:type "text/javascript"}
     (selmer/render
      "
var logEvent = function(data) {
  var r = new XMLHttpRequest();
  r.open('POST', '{{event-path}}');
  r.setRequestHeader('Content-Type', 'application/json');
  r.send(JSON.stringify(data));
};
"
      req)]))
