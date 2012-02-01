(ns locale.translate
  "Functions for translating paths and strings."
  (require [clojure.string :as str]
           [clojure.tools.logging :as log]
           [util.path :as path]
           [www.url :as url])
  (use [clojure.contrib.def :only (defnk)]
       [util.except :only (safely)]))

(defn translate-path
  "Translates a path by breaking it into parts and indexing into a translation map. The path argument
can be a path or a sequence of path segments."
  [m path]
  (let [parts (loop [m m
                     parts (if (sequential? path) path (:parts path))
                     result []]
                (if-let [part (first parts)]
                  (if-let [m (m (first parts))]
                    (recur m (rest parts) (conj result (or (:name m) part))))
                  result))]
    (if parts (path/new-path {:parts parts :relative false}))))

(defn localize-path
  "Translates a canonical path to a localized one."
  [m locale path]
  (translate-path m (cons locale (:parts (path/new-path path)))))

(defn canonicalize-path
  "Translates a localized path to a canonical one."
  [m locale path]
  (let [path (localize-path m locale path)]
    (if path
      (assoc path :parts (rest (:parts path))))))

(defn localize
  "Translates a canonical url to a localized one. The locale to use
   is looked up in req (a ring request). Optional arguments are
   converted to a hash and merged in to the localized url which is
   then coerced to a string."
  [url req & opts]
  (let [m (:localized-paths req)
        locale (:locale req)
        url (url/new-URL url)
        path (:path url)
        path (if (path/relative? path)
               (let [parent (path/parent (:uri req))
                     path (localize-path m locale (path/join parent path))]
                 (if path
                   (path/unqualify path (localize-path m locale parent))
                   (do
                     (log/warn (str "Failed to localize path '" url "' (locale=" locale ", parent=" parent ")."))
                     (:path url))))
               (localize-path m locale path))
        url (if path
              (assoc url :path path)
              url)
        url (if (empty? opts)
              url
              (merge url (apply hash-map opts)))]
    (str url)))

(defn canonicalize
  "Translates a localized path to a canonical one."
  [path req]
  (let [m (:canonical-paths req)
        locale (path/first path)
        path (canonicalize-path m locale (path/rest path))]
    (if path
      (str path))))

(defn mangle-path-to-ns
  "Mangles a path segment to the appropriate namespace name."
  [s]
  (str/replace s \. \-))

(defn translate
  "Looks up a string translation. Combines root-ns with path to get a
   namespace and then looks for the var 'strings which should be a
   hash of translation strings. Calls get-in with the supplied keys to
   lookup the value. If no value is found, recurses and looks up the
   same keys in path's parent. If no value is found, a default string
   based on the keys is returned."
  [root-ns locale path & keys]
  (let [s (loop [path path]
            (let [ns-sym (symbol (str/join "." (cons root-ns (map mangle-path-to-ns (path/split path)))))
                  v (safely (ns-resolve ns-sym 'strings))
                  s (if v
                      (get-in (var-get v) (cons locale keys)))]
              (or s
                  (if-not (path/blank? path)
                    (recur (path/parent path))))))]
    (or s
        (do
          (log/warn (str "No translation for key " keys " (locale=" locale ", path=" path ")."))
          (str "(" (str/join "-" (map name keys)) ")")))))
