(ns analytics.mixpanel
  "For interacting with Mixpanel."
  (:require [clojure.core.async :as as]
            [org.httpkit.client :as http]
            [app.config :as c]
            [www.url :as url]
            [taoensso.timbre :as log]
            [clojure.data.json :as json])
  (:import [org.apache.commons.codec.binary Base64]
           [java.util Date]))

(def ^:private url "https://api.mixpanel.com/")
(def ^:private track-url (str url "track"))

(defn- base64-encode
  "Base64 encode suitable for passing data to the Mixpanel API."
  ([^String s]
     (base64-encode s false false))
  ([^String s chunked url-safe]
     (String. (Base64/encodeBase64 (.getBytes s) chunked url-safe))))

(defn- now
  "Current time in seconds since 1/1/70 UTC."
  []
  (apply str (drop-last 3 (str (.getTime (Date.))))))

;; TODO: If needed for scaling and/or graceful degradation, send batch requests and use a sliding window buffer.
(defn post
  [data]
  (log/info {:event :mixpanel/track-request :data data})
  (let [c (as/chan)]
    ;; Post data (async).
    (http/post track-url
               {:query-params {:data (-> data json/json-str base64-encode)}}
               (fn [response]
                 (as/go (as/>! c response)
                        (as/close! c))))

    ;; Log result (async).
    (as/go (let [{:keys [body status] :as response} (as/<! c)
                 msg {:event :mixpanel/track-response :response response}]
             (if (and (= body "1") (= status 200))
               (log/info msg)
               (log/error msg))))))

(defn- make-event
  "Makes a mixpanel event from an event."
  [{:keys [event bid req] :as m}]
  {:event (str event)
   :properties (-> m
                   (dissoc :req :event :bid)
                   (assoc :token (c/conf :mixpanel :token)
                          :distinct_id bid
                          :time (now)
                          :ip (get-in req [:headers "x-forwarded-for"])))})

;; only add bid if present
;; only add ip if req is present

(defn send-event
  "Sends an event to mixpanel."
  [event]
  (-> event
      make-event
      post))
