(ns locale.middleware.translate
  (:require [clojure.java.io :as io]
            [clj-yaml.core :as yaml]
            [clojure.tools.logging :as log]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.contrib.string :as strc]
            [clojure.contrib.str-utils2 :as stru])
  (:use [locale.core :only (locales default-locale)]
        [util (except :only (check))
              fs
              (core :only (contains-in?))]
        [www.url :only (url)]
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
        :default (do (log/error (str "Ignoring entry for unknown locale '" locale "' in file '" file "'."))
                     (dissoc m locale))))
     m
     (set/union locales (keys m)))))

(defn- check-names
  "Checks that all name values are strings and encodes any unsafe characters."
  [m file]
  (let [cname (m default-locale)]
    (reduce
     (fn [m [locale lname]]
       (let [is-string (string? lname)]
         (or is-string (log/error (str "Invalid entry '" lname "' for locale '" locale "' in file '" file "'.")))
         (assoc m locale (str (url (if is-string lname cname))))))
     m m)))
#_(load-name-translations "src/sistemi/site")

(defn load-name-translations
  "Loads all path name translation files under a directory root and returns an array of two maps.
   The first map maps canonical URI paths to localized ones and the second map vice versa."
  [root]
  (reduce
   (fn [[localized canonical] dir]
     (let [cname (.getName ^File dir)
           cpath (stru/drop (.getPath ^File dir) (count root))
           file (io/file dir "name.yml")
           name-map (-> (or (and (empty? cpath) (default-translation-map cname))
                            (load-yaml-safely file)
                            (default-translation-map cname))
                        (check-canonical cname file)
                        (check-locales file)
                        (check-names file)
                        (check-extensions))]
       (reduce
        (fn [[lm cm] [locale lname]]
          (let [lpath (let [ppath (parent cpath)]
                        (if (nil? ppath)
                          (ffs locale)
                          (fs (lm (ffs locale ppath)) lname)))
                lm (assoc lm (ffs locale cpath) lpath)
                cm (assoc cm (ffs lpath) (if (empty? cpath) "/" cpath))]
            [lm cm]))
        [localized canonical] name-map)))
   [{} {}] (dir-seq-bf root)))
;(load-name-translations "src/sistemi/site")

;; TODO: Move these notes somewhere.
;; - Keep the canonical path uri safe (i.e., no special chars that need encoding).
;; - Encode the path translations as they are read from name.yml.

(defn load-string-translations
  "Loads all string translations files under a root directory and returns a
   map of canonical URI path to a submap of string translations."
  [root]
  (reduce
   (fn [m dir]
     (let [cname (.getName ^File dir)
           cpath (stru/drop (.getPath ^File dir) (inc (count root))) ; TODO: unqualify; load strings for root path
           file (io/file dir "strings.yml")
           name-map (when (.exists file)
                      (-> (load-yaml-safely file)
                          (check-locales file)))]
       (reduce
        (fn [m [locale string-map]]
          (assoc m (ffs locale cpath) string-map))
        m name-map)))
   {} (dir-seq-bf root)))
#_(load-string-translations "src/sistemi/site")

;; TODO: Add checks and balances.
;; - List which pages/templates are never used (request log analysis).
;; - List which translations are never used ().
;; TODO: List strings which are missing a translation (when page string translations are loaded).

(defn wrap-translate-uri
  "Request wrapper that:
   - Translates :uri from a localized to a canonical version.
   - Adds a function :luri to translate canonical uris to localized versions."
  [app localized canonical]
  (fn [req]
    (let [locale (req :locale)
          luri (req :uri)
          fluri (ffs locale luri)]
      (if-let [curi (canonical fluri)]
        (app (assoc req
               ;; canonical URI
               :uri curi
               ;; fn to localize urls
               :luri (fn this
                       ([curi] (this locale curi))
                       ([locale curi]
                          ;; FIX: Relative urls are incorrectly qualified for non english.
                          ;; The uri of the current request is in the actual locale,
                          ;; whereas the relative part is in the canonical locale.
                          (let [curi (if (relative? curi) (qualify curi (parent (req :uri)))  curi)] 
                            (or (localized (ffs locale curi))
                                (log/warn (str "No translation for uri " curi "."))))))

               ;; map to canonicalize uri
               ;; TODO: This is *only* used when changing locales. Can/should it can be passed another way?
               :curi canonical))

        ;; Return a 404 if no path can be found.
        (make-404 req)))))

(defn wrap-translate-strings
  "Request wrapper that adds a function :strings to lookup string translations for the page."
  [app page-strings canonical]
  (fn [req]
    (let [locale (req :locale)
          uri (req :uri)]
      (app (assoc req
             :strings (let [strings (page-strings (ffs locale uri) {})]
                        (fn [& keys]
                          (let [val (get-in strings keys)
                                ;; Map values have their string value stored under the key :_.
                                val (if (map? val) (val :_) val)
                                ;; Strings in the default locale use their key as the value by default.
                                val (or val (and (= locale default-locale)
                                                 (contains-in? strings keys)
                                                 (name (last keys))))]
                            (or val
                                (do
                                  (log/warn (str "No translation for key " keys " (locale=" locale ", page=" uri ")."))
                                  (str "(" (str/join "-" (map name keys)) ")")))))))))))
