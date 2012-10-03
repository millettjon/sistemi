(ns paypal.nvp
  "Functions for working with the Paypal NVP API."
  (:use paypal.config)
  (:require [clj-http.client :as client]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [log.readably :as logr]
            [www.url :as url]))

;; TODO: ? Is the circuit breaker pattern useful (http://blog.higher-order.net/2010/05/05/circuitbreaker-clojure-1-2/)?
;; TODO: ? Is the http.agent-api appropriate (http://richhickey.github.com/clojure-contrib/http.agent-api.html)?
;; - limit concurrency to paypal
;; - allow request to return immediately to client (or after a timeout)
;; TODO: add tests

;; TODO: Factor this out.
;;    - recursively walk a data structure
;;    - redact values that have a specific metadata tag
;;    ? is there a better word that sanitize? redact?
(defn sanitize
  "Remove sensitive information from a map."
  [m]
  (let [redacted-map (select-keys m [:user :pwd :signature])
        redacted-map (zipmap (keys redacted-map) (repeat (count redacted-map) "-redacted-"))]
    (merge m redacted-map)))

(defn- call
  "Calls a paypal NVP (name-value-pair) method and returns the result."
  [conf method params]
  (let [site-conf ((keyword (:site conf)) site-conf)
        form-params (merge
                     (select-keys site-conf [:version])
                     (select-keys conf [:user :pwd :signature])
                     params
                     {:method method})
        request-params (hash-map :form-params form-params)]
    (logr/info "request" (sanitize form-params))
    (let [resp (client/post (:url site-conf) request-params)]
      (logr/info "response" resp)
      resp)))

(defn- parse
  "Parses a paypal NVP response and returns the result as a keywordized map."
  [resp]
  (reduce (fn [m kvpair]
            (let [[k v] (str/split kvpair #"=")]
              (assoc m (keyword (str/lower-case k)) (url/decode v))))
          {}
          (str/split (:body resp) #"&")))

(defn- check
  "Checks an NVP response map for errors. If an error is found, logs the error and throws an exception."
  [nvp]
  (logr/info "check: nvp:" nvp)
  (when (not= "Success" (:ack nvp))
    (let [message (str/join "|" (map (fn [e] ((keyword (str "l_" (name e) "0")) nvp)) [:severitycode :errorcode :shortmessage :longmessage]))]
      (log/error message)
      (throw (RuntimeException. message))))
  nvp)

(defn do-request
  [conf method params]
  (-> (call conf method params)
      parse
      check))
