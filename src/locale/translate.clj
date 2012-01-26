(ns locale.translate
  "Functions for translating paths and strings."
  (require [util.path :as path]
           [www.url :as url])
  (use [clojure.contrib.def :only (defnk)]))

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
  "Translates a canonical path to a localized one. The locale to use
is looked up in req (a ring request). Optional arguments are converted
to a hash and merged in to the localized path to form a url. The url
is then coerced to a string and returned."
  [path req & opts]
  (let [m (:localized-paths req)
        locale (:locale req)
        path (localize-path m locale path)
        url (and path (url/new-URL path))]
    (if url
      (str (if (empty? opts)
             url
             (merge url (apply hash-map opts)))))))

(defn canonicalize
  "Translates a localized path to a canonical one."
  [path req]
  (let [m (:canonical-paths req)
        locale (path/first path)
        path (canonicalize-path m locale (path/rest path))]
    (if path
      (str path))))

