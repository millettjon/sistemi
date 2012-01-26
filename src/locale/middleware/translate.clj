(ns locale.middleware.translate
  (:require [clj-yaml.core :as yaml]
            [clojure.tools.logging :as log]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.contrib.str-utils2 :as stru]
            [www.url :as url]
            [util.path :as path])
  (:use [locale.core :only (locales default-locale)]
        locale.translate
        [util (except :only (check))
              (core :only (contains-in?))]
        [sistemi.handlers :only (make-404)])
  (:import java.io.File))

(defn- load-yaml-safely
  "Loads a map from a yaml file and returns it.  The top level keys are coerced to strings.  If the
   file can't be loaded, logs an error and returns nil."
  [file]
  (try (let [m (-> (yaml/parse-string (slurp (path/to-file file)))
                   (check map?))]
         ;; Convert top level keywords to strings.
         (reduce (fn [m [k v]]
                   (-> (dissoc m k)
                       (assoc (name k) v)))
                 m m))
       (catch Exception x 
         (log/error (str "Failed to load file '" file "' (" x ").")))))

(defn- root-translation-map
  "Returns a default translation map where each non default locale is mapped to the canonical name."
  []
  (apply hash-map (mapcat #(list % %) locales)))

(defn- default-translation-map
  "Returns a default translation map where each non default locale is mapped to the canonical name."
  [cname]
  (dissoc
   (apply hash-map (mapcat #(list %1 cname) locales))
   default-locale))

(defn- check-canonical
  "Makes sure the default-locale uses the canonical name."
  [m cname file]
  (if (str/blank? cname)
    ;; The cname at the web root is "" but should be mapped to "en"
    ;; (i.e., the default locale.
    m
    (do 
      (if (contains? m default-locale)
        (log/warn (str "Ignoring extraneous entry for default locale '"
                       default-locale "' in file '" file "'.")))
      (assoc m default-locale cname))))

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
         (assoc m locale (str (url/encode-path (if is-string lname cname))))))
     m m)))
#_(load-name-translations "src/sistemi/site")

#_ (load-name-translations "src/sistemi/site")
(defn load-name-translations
  [root]
  ;; Process each directory under the website root.
  (reduce
   (fn [[localized canonical] dir]
     ;; Load and check the path name translations.
     (let [cpath (path/unqualify dir root)
           cname (path/last cpath)
           file (path/join dir "name.yml")
           name-map (-> (or (and (path/blank? cpath) (root-translation-map))
                            (load-yaml-safely file)
                            (default-translation-map cname))
                        (check-canonical cname file)
                        (check-locales file)
                        (check-names file)
                        (check-extensions))]
       ;; Process each translation of the directory's name.
       (reduce
        (fn [[lm cm] [locale lname]]
          (let [lm (assoc-in lm (concat [locale] (:parts cpath) [:name]) lname)
                cm (assoc-in cm (concat (:parts (localize-path lm locale cpath)) [:name]) cname)]
            [lm cm]))
        [localized canonical] name-map)))
   [{} {}] (path/dir-seq-bf root)))

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
           file (path/to-file dir "strings.yml")
           name-map (when (.exists ^File file)
                      (-> (load-yaml-safely file)
                          (check-locales file)))]
       (reduce
        (fn [m [locale string-map]]
          (assoc m (path/joinq locale cpath) string-map))
        m name-map)))
   {} (path/dir-seq-bf root)))
#_(load-string-translations "src/sistemi/site")

;; TODO: Add checks and balances.
;; - List which pages/templates are never used (request log analysis).
;; - List which translations are never used ().
;; TODO: List strings which are missing a translation (when page string translations are loaded).

(defn wrap-translate-uri
  "Translates the request :uri and injects path translation maps into the request."
  [app
   localized ; map for translating canonical urls to localized ones
   canonical] ; map for translating localized urls to canonical ones
  (fn [req]
    (let [locale (req :locale)
          luri (req :uri)
          fluri (path/joinq locale luri)]
      (if-let [curi (canonicalize-path canonical locale luri)]
        (app (assoc req
               ;; canonical URI
               :uri (str curi)
               :localized-paths localized

               ;; TODO: This is *only* used when changing locales. Can/should it can be passed another way?
               :canonical-paths canonical))

        ;; Return nil no path can be found.
        nil))))

(defn wrap-translate-strings
  "Request wrapper that adds a function :strings to lookup string translations for the page."
  [app page-strings canonical]
  (fn [req]
    (let [locale (req :locale)
          uri (req :uri)]
      (app (assoc req
             :strings (let [strings (page-strings (path/joinq locale uri) {})]
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
