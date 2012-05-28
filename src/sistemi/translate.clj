(ns sistemi.translate
  (require [clojure.string :as str]
           [locale.core :as l]
           [locale.translate :as tr]
           [www.request :as req]
           [util.path :as path]
           [www.url :as url]
           [sistemi.handler :as handler]
           [sistemi.registry :as registry]))

(defn full-locale
  []
  (l/full-locale (:locale req/*req*)))

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

(defn qualifys
  "Localizes and qualifies a url as a sibling of the uri of the current request."
  [url & opts]
  (let [url (url/new-URL url)
        path (:path url)
        lurl (apply localize url opts)]
    (if (path/absolute? path)
      (url/qualify lurl req/*req*)
      (let [locale (:locale req/*req*)
            req-url (url/new-URL req/*req*)
            parent-path (path/parent (:path req-url))
            parent-url (assoc req-url :path (path/join locale parent-path))]
        (url/qualify lurl parent-url)))))
