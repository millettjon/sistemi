(ns www.event
  "Browser event tracking for analytics."
  (:require [util.base62 :as b62]
            [www.request :as req]
            [ring.util.response :as rsp]
            [datomic.api :as d]
            [sistemi.datomic :as sd]
            [clojure.tools.logging :as log]))

;; It should degrade gracefully if the transactor is offline.
;; - for writes, attempting to create new browser-id will fail with a 500 error
;;   - javascript just ignores the error
;;   - no cookie is set
;; - for reads, the query will just run against the working set in memory and will often just work
;;
;; ? what if cookie path is set to /event?
;;   - cookie sent only when necessary
;;   - lose tracking other requests (e.g., for debugging)
;;     - static resources will usually be cached and hence there is no tracking anyway


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
  (log/info "===== new browser-id: " id " =====")
  (d/transact-async conn [{:db/id (d/tempid sd/partition)
                           :browser/id id}]))

(def ^:private event-response
  "An empty json response for consumption by the client side javascript event sender."
  (-> "42" ; dummy content
      rsp/response
      (rsp/content-type "application/json")))

(defn- handle-event
  [req]
  ;; (log/info "EVENT" req)
  ;; TODO: Call any event handlers e.g., logging, mixpanel.
  ;; TODO: Should this filter out events from localhost?
  )

(defn- valid?
  "Returns true if the browser-id is valid."
  [bid]
  (re-matches #"[a-zA-Z0-9]{21}" bid))

(defn- exists?
  "Returns true if the browser-id exists in database."
  [conn bid]
  (->> conn
       d/db
       (d/q '[:find ?e
              :in ?bid $
              :where [?e :browser/id ?bid]]
            bid)
       empty?
       not))

(defn- handle-event-request
  ""
  [req conn]
  (let [bid         (get-in req [:cookies cookie-name :value])
        event-path  (:uri req)]
    (cond
     ;; no id was passed: generate one, call handlers, set a response cookie
     (not bid) (let [bid (gen-id)]
                 (new-browser-async conn bid)
                 (handle-event (assoc-in req [:cookies cookie-name] bid))
                 (-> event-response
                     (rsp/set-cookie cookie-name bid {:secure (not (req/local-request? req))
                                                      :http-only true
                                                      :path "/event"
                                                      :max-age one-year
                                                      })))

     ;; valid id was passed: call handlers
     (and (valid? bid) (exists? conn bid)) (do (handle-event req)
                                               event-response)

     ;; invalid id: log an error and ignore it
     :else (do (log/error "Invalid browser id:" bid)
               event-response))))

(defn wrap-event
  "Manages events. For normal request, inserts the event handler path
into the request. For event tracking requests, reads the browser id
from a cookie. If no cookie was set, generates a new id and sets the
cookie. The request is then passed to any event handlers and an empty
response is returned."
  [app path conn]
  (let [event-path (str "/" path)]
    (fn [req]
      (if (not= event-path (:uri req))
        (-> req
            (assoc :event-path event-path)
            app)
        (handle-event-request req conn)))))

(defn script
  "Returns html snippet with javascript that makes a request to the
event tracking handler. Include this in pages that should log events
when loaded."
  [req]
  (let [event-path (:event-path req)]
    [:script {:type "text/javascript"}
     (str "(function () { var r = new XMLHttpRequest(); r.open(\"get\", \""
          event-path
          "\", true); r.send();})();")]))
