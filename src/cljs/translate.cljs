(ns translate
  (:require [pathetic.core :as pathetic])
  (:use [jayq.util :only [log]]))

(defn get-locale
  "Returns the current locale by parsing the current url."
  []
  (-> (aget js/window "location" "pathname")
      pathetic/parse-path
      second
      keyword))

(defn translate
  [m & keys]
  (let [locale (get-locale)]
    (get-in m (concat [locale] keys))))
