(ns www.url
  "URL related functions."
  (:import (java.net URLDecoder)))

(defn decode
  "Returns the UTF-8 URL decoded version of the given string."
  [encoded]
  (URLDecoder/decode encoded "UTF-8"))
