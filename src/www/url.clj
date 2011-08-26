(ns www.url
  "URL related functions."
  (:import (java.net URLDecoder)))

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
