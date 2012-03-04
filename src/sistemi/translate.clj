(ns sistemi.translate
  (require [clojure.string :as str]
           [locale.translate :as tr]
           [www.request :as req]
           [sistemi.handler :as handler]
           [sistemi.registry :as registry]))

(defn translate
  [& keys]
  (let [req req/*req*
        locale (keyword (:locale req))
        path (:uri req)]
    (apply tr/translate registry/strings locale path keys)))

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
  (apply tr/localize url registry/localized-paths req/*req* opts))

(defn canonicalize
  "Translates a localized path to a canonical one."
  [path]
  (tr/canonicalize path registry/canonicalized-paths req/*req*))
