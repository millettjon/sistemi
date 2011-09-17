(ns locale.middleware.translate
  (:require [clojure.java.io :as io]
            [clj-yaml.core :as yaml]
            [clojure.tools.logging :as log]
            [clojure.set :as set]
            [clojure.contrib.string :as str])
  (:use [locale.core :only (locales default-locale)]
        [util (except :only (check))
              fs]
        [sistemi.handlers :only (make-404)])
  (:import java.io.File))

(defn- load-yaml-safely
  "Loads a map from a yaml file and returns it.  The top level keys are coerced to strings.  If the
   file can't be loaded, logs an error and returns nil."
  [file]
  (try (let [m (-> (yaml/parse-string (slurp file))
                   (check map?))]
         ;; Convert top level keywords to strings.
         (reduce (fn [m [k v]]
                   (-> (dissoc m k)
                       (assoc (name k) v)))
                 m m))
       (catch Exception x 
         (log/error (str "Failed to load file '" file "' (" x ").")))))

(defn- default-translation-map
  "Returns a default translation map where each non default locale is mapped to the canonical name."
  [cname]
  (dissoc
   (apply hash-map (mapcat #(list %1 cname) locales))
   default-locale))

(defn- check-canonical
  "Makes sure the default-locale uses the canonical name."
  [m cname file]
  (when (contains? m default-locale)
    (log/warn (str "Ignoring extraneous entry for default locale '"
                   default-locale "' in file '" file "'.")))
  (assoc m default-locale cname))

(defn- check-extensions
  "Updates a path translation map to add file extensions if ommitted."
  [m]
  (if-let [ext (re-find #"\.[^.]+$" (m default-locale))]
    (reduce (fn [m [locale name]]
              (if (re-find (re-pattern (str ext "$")) name)
                m
                (assoc m locale (str name ext))))
            m
            m)
    m))

(defn- check-locales
  "Adds path entries for missing locales and removes unknown locale entries."
  [m file]
  (let [cname (m default-locale)]
    (reduce
     (fn [m locale]
       (cond
        (and (contains? m locale) (locales locale)) m
        (locales locale) (do (log/error (str "No entry for locale '" locale "' in file '" file "'."))
                             (assoc m locale cname))
        :default (do (log/error (str "Ignoring entry for uknown locale '" locale "' in file '" file "'."))
                     (dissoc m locale))))
     m
     (set/union locales (keys m)))))

(defn- check-names
  "Checks that all name values are strings."
  [m file]
  (let [cname (m default-locale)]
    (reduce
     (fn [m [locale lname]]
       (if (string? lname)
         m
         (do (log/error (str "Invalid entry '" lname "' for locale '"
                             locale "' in file '" file "'."))
                             (assoc m locale cname))))
     m m)))

(defn- load-name-translations
  "Loads all path name translation files under a directory root and returns an array of two maps.
   The first map maps canonical URI paths to localized ones and the second map vice versa."
  [root]
  (reduce
   (fn [[localized canonical] dir]
     (let [cname (.getName ^File dir)
           cpath (str/drop (count root) (.getPath ^File dir))
           file (io/file dir "name.yml")
           name-map (-> (or (load-yaml-safely file)
                            (default-translation-map cname))
                        (check-canonical cname file)
                        (check-locales file)
                        (check-names file)
                        (check-extensions))]
       (reduce
        (fn [[lm cm] [locale lname]]
          (let [lpath (let [ppath (parent cpath)]
                        (if (root? ppath)
                          (ffs locale lname)
                          (fs (lm (ffs locale ppath)) lname)))
                lm (assoc lm (ffs locale cpath) lpath)
                cm (assoc cm lpath cpath)]
            [lm cm]))
        [localized canonical] name-map)))
   [{} {}] (rest (dir-seq-bf root))))
;;(load-name-translations "src/sistemi/site")

(defn- load-string-translations
  "Loads all string translations files under a root directory and returns a
   map of canonical URI path to a submap of string translations."
  [root]
  (reduce
   (fn [m dir]
     (let [cname (.getName ^File dir)
           cpath (str/drop (inc (count root)) (.getPath ^File dir)) ; TODO: unqualify
           file (io/file dir "strings.yml")
           name-map (when (.exists file)
                      (-> (load-yaml-safely file)
                          (check-locales file)))]
       (reduce
        (fn [m [locale string-map]]
          (assoc m (ffs locale cpath) string-map))
        m name-map)))
   {} (dir-seq-bf root)))
;;(load-string-translations "src/sistemi/site")

;; TODO: Add checks and balances.
;; - List which pages/templates are never used (request log analysis).
;; - List which translations are never used ().
;; TODO: List strings which are missing a translation (when page string translations are loaded).

(defn wrap-translate
  "Request wrapper that:
   - Translates :uri from a localized to a canonical version.
   - Adds a function :strings to lookup string translations for the page.
   - Adds a function :luri to translate canonical uris to localized versions."
  [app root]
  (let [[localized canonical] (load-name-translations root)
        page-strings (load-string-translations root)]
    (fn [req]
      (let [locale (req :locale)
            luri (req :uri)
            fluri (ffs locale luri)]
        (if-let [curi (canonical fluri)]
          (app (assoc req
                 ;; canonical URI
                 :uri curi
                 ;; fn to translate strings for the current locale and page
                 :strings (let [strings (page-strings (ffs locale curi) {})]
                            (fn [& keys]
                              (or (get-in strings keys)
                                  (let [key (str/join "-" (map name keys))]
                                    (log/warn (str "No translation for key " key " on page=" fluri "."))
                                    (str "(" key ")")))))
                 ;; fn to localize urls
                 :luri (fn this
                         ([curi] (this locale curi))
                         ([locale curi]
                            (or (localized (ffs locale curi))
                                (log/warn (str "No translation for uri " curi ".")))))

                 ;; map to canonicalize uri
                 ;; TODO: This is *only* used when changing locales. Can/should it can be passed another way?
                 :curi canonical))

          ;; Return a 404 if no path can be found.
          (make-404 req))))))


