(ns www.url
  "URL related functions."
  (:require [clojure.string :as str])
  (:import (java.net URLDecoder URI)))

(defn decode
  "Returns the UTF-8 URL decoded version of the given string."
  [encoded]
  (URLDecoder/decode encoded "UTF-8"))

(defn canonicalize
  "Makes a canonical URL by combining the scheme, server, and port from a request map with the
   desired URI."
  [req uri]
  (let [{server :server-name port :server-port} req]
    (case (:scheme req)
      :http (str "http://" server (case port 80 "" (str ":" port)) uri)
      :https (str "https://" server (case port 443 "" (str ":" port)) uri))))

;; Note: stolen from ring-mock...
(defn parse
  "Parses a URL into a minimal ring request map."
  [uri]
  (let [uri    (URI. uri)
        host   (.getHost uri)
        port   (if (not= (.getPort uri) -1) (.getPort uri))
        scheme (.getScheme uri)
        path   (.getRawPath uri)]
    {:server-port    (or port 80)
     :server-name    host
     :uri            (if (str/blank? path) "/" path)
     :scheme         (keyword scheme)
     }))

(defn self-referred?
  "Returns true if the request and it's referer header reference the same server."
  [req]
  (apply = (map #(select-keys %1 [:scheme :server-name :server-port])
                [req
                 (parse (get-in req [:headers "referer"]))])))

;; TODO: Add this as a test.
;; (self-referred?
;;  {:scheme :http :server-name "localhost" :server-port 5000
;;   :headers {"referer" "http://localhost:5000/foo"}})
