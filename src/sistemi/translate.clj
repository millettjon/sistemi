(ns sistemi.translate
  (require [clojure.string :as str]
           [locale.translate :as tr]
           [www.request :as req]))

(defn translate
  [& keys]
  (let [req req/*req*
        locale (keyword (:locale req))
        path (:uri req)]
    (apply tr/translate "sistemi.site" locale path keys)))

(defn mangle-text-to-kw
  "Converts a text string to a mangled keyword to use to lookup a string translation."
  [s]
  (-> s
      str/lower-case
      (str/replace \ \-)
      keyword))

(defn localize
  "Calls 'locale.translate/localize with the current request."
  [url & opts]
  (apply tr/localize url req/*req* opts))
