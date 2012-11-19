(ns locale.translate
  "Functions for translating paths and strings."
  (require [clojure.string :as str]
           [clojure.tools.logging :as log]
           [util.path :as path]
           [www.url :as url])
  (use [clojure.contrib.def :only (defnk)]
       [util.except :only (safely)]))

(defn- translate-path
  [path m key]
  (loop [m m
         keys (path/split path)
         result []]
    (if (empty? keys)
      (-> (apply path/join result)
          path/qualify)
      (if-let [m (get m (first keys))]
        (recur m (rest keys) (conj result (m key)))))))

(defn localize-path
  "Localizes a canonical path."
  [path m locale]
  (if-let [path (translate-path path m locale)]
    (path/joinq (name locale) path)))

(defn canonicalize-path
  "Canonicalizes a locale path."
  [path m locale]
  (if-let [m (m locale)]
    (translate-path path m :name)))

(defn localize
  "Translates a canonical url to a localized one. The locale to use
   is looked up in req (a ring request). Optional arguments are
   converted to a hash and merged in to the localized url which is
   then coerced to a string."
  [url m req & opts]
  (let [locale (keyword (:locale req))
        url (url/new-URL url)
        path (:path url)
        path (if (path/relative? path)
               (let [parent (path/parent (:uri req))
                     path (localize-path (path/join parent path) m locale)]
                 (if path
                   (path/unqualify path (localize-path parent m locale))
                   (do
                     (log/warn (str "Failed to localize path '" url "' (locale=" locale ", parent=" parent ")."))
                     (:path url))))
               (localize-path path m locale))
        url (if path
              (assoc url :path path)
              url)
        ;; Merge in url options if present.
        url (if (empty? opts)
              url
              (let [opts (if (= 1 (count opts))
                           (first opts)           ; map
                           (apply hash-map opts)) ; vector of keys and values
                    ;; Encode the query string if present.
                    opts (if-let [qs (:query opts)]
                           (assoc opts :query (url/encode-query qs))
                           opts)]
                (merge url opts)))]
    (str url)))

(defn canonicalize
  "Translates a localized path to a canonical one."
  [path m req]
  (let [locale (keyword (path/first path))
        path (canonicalize-path (path/rest path) m locale)]
    (if path
      (str path))))

(defn translate
  "Looks up a string translation in strings for the given locale, path, and keys.
   If no value is found for path, the parent path is searched recursively.
   If no value is found, a default string based on the key is returned."
  [strings locale path & keys]
  (let [s (loop [path path]
            (let [s (get-in strings (concat (path/split path) [locale] keys))]
              (or (and s (if (map? s)
                           (get s :_ s)
                           s))
                  (if-not (path/blank? path)
                    (recur (path/parent path))))))]
    (or s
        (do
          (log/warn (str "No translation for key " keys " (locale=" locale ", path=" path ")."))
          (str "(TODO Translation For " locale (str/join (map name keys)) ")")))))
          ;(str "(" (str/join "-" (map name keys)) ")")))))
