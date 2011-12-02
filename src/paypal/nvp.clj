(ns paypal.nvp
  "Functions for working with the Paypal NVP API."
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

(defn- sanitize
  "Remove sensitive information from a map."
  [m]
  (let [redacted-map (select-keys m [:USER :PWD :SIGNATURE])
        redacted-map (zipmap (keys redacted-map) (repeat (count redacted-map) "-redacted-"))]
    (merge m redacted-map)))

(defn- call
  "Calls a paypal NVP (name-value-pair) method and returns the result."
  [conf method params]
  (let [form-params (merge params (select-keys conf [:USER :PWD :SIGNATURE :VERSION]) {:METHOD method})
        request-params (hash-map :form-params form-params)]
    (logr/info "request" (sanitize form-params))
    (let [resp (client/post (:URL conf) request-params)]
      (logr/info "response" resp)
      resp)))

(defn- parse
  "Parses a paypal NVP response and returns the result as a keywordized map."
  [resp]
  (reduce (fn [m kvpair]
            (let [[k v] (str/split kvpair #"=")]
              (assoc m (keyword k) (url/decode v))))
          {}
          (str/split (:body resp) #"&")))

(defn- check
  "Checks an NVP response map for errors. If an error is found, logs the error and throws an exception."
  [nvp]
  (logr/info "check: nvp:" nvp)
  (when (not= "Success" (:ACK nvp))
    (let [message (str/join "|" (map (fn [e] ((keyword (str "L_" (name e) "0")) nvp)) [:SEVERITYCODE :ERRORCODE :SHORTMESSAGE :LONGMESSAGE]))]
      (log/error message)
      (throw (RuntimeException. message))))
  nvp)

(defn do-request
  [conf method params]
  (-> (call conf method params)
      parse
      check))
