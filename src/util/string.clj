(ns util.string
  (require [clojure.string :as str]))

(defn starts-with?
  "Returns true if string starts with substring."
  [string substring]
  (.startsWith string substring))

(defn triml-lines
  "Deletes leading whitespace from a multi-line string."
  [s]
  (str/join "\n" (map str/triml (str/split-lines s))))

(defn join-lines
  "Converts new lines to spaces and deletes leading whitespace from a multi-line string."
  [s]
  (str/join " " (map str/triml (str/split-lines s))))
