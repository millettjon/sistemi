(ns shipping.ups.util
  (:require [clojure.string :as s]) )

(defn strip-newlines
  "Strip newline characters from text -- "
  [& text]
  (s/replace (apply str text) "\n" ""))