(ns shipping.ups.xml.tools
  (:import [java.io ByteArrayInputStream])
  (:require [clojure.string :as str]))

(defn text-in-bytestream
  "Turn text into ByteArrayInputStream (trimmed text)."
  [text]
  (ByteArrayInputStream. (.getBytes (str/trim text))) )