(ns paypal.nvp
  "Functions for working with the Paypal NVP API."
  (:require [clj-http.client :as client]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [log.readably :as logr]
            [www.url :as url]))

;; TODO: ? Is the http.agent-api is more appropriate (http://richhickey.github.com/clojure-contrib/http.agent-api.html)?
;; - limit concurrency to paypal
;; - allow request to return immediately to client (or after a timeout)
;; - provide endpoint for client to poll for status
;; TODO: learn about agents

;; TODO: Factor out the configurable items.
(defn call
  "Calls a paypal NVP (name-value-pair) method and returns the result."
  [method params]
  (let [fparams (merge params {:USER "seller_1301328605_biz_api1.sistemimoderni.com"
                               :PWD "1301328617"
                               :SIGNATURE "A67JHhtiGB0VpBJwFhFIoh1sRh1jAcab39wpr0cwYobasj4xRoJtiV.S"
                               :VERSION "78.0"
                               :METHOD method})
        rparams (hash-map :form-params fparams)]
    (logr/info "request" (if (contains? fparams :PWD) (assoc fparams :PWD "-redacted-") fparams))
    (let [resp (client/post "https://api-3t.sandbox.paypal.com/nvp" rparams)]
      (logr/info "response" resp)
      resp)))

(defn parse
  "Parses a paypal NVP response and returns the result as a map."
  [resp]
  (let [v (str/split (:body resp) #"&")
        v1 (map (fn [kvpair] (str/split kvpair #"=")) v)
        v2 (apply hash-map (map url/decode (flatten v1)))
        ]
    v2))

(defn check
  "Checks an NVP response map for errors. If an error is found, logs the error and throws an exception."
  [nvp]
  (when (not (= "Success" (get nvp "ACK")))
    (let [message (str/join "|" (map (fn [e] (get nvp (str "L_" (name e) "0"))) [:SEVERITYCODE :ERRORCODE :SHORTMESSAGE :LONGMESSAGE  ]))]
      (prn message)                     ; TODO: disable printing
      (log/error message)
;      (throw (RuntimeException. message)) ; TODO: enable exceptions
      )))

(defn client
  [method params]
  (-> (call method params)
      parse
      check))
