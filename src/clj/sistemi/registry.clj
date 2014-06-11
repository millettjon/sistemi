(ns sistemi.registry
  (:require [clojure.string :as str]
            [util.string :as str2]
            [clojure.set :as set]
            [clojure.tools.logging :as log]
            [locale.translate :as tr]
            [util.path :as path]
            [www.url :as url]
            [locale.core :as locale]
            [clojure.java.io :as io]
            [clojure.tools.namespace.find :as ns-find])
  (:use app.config))

;;; --------------------------------------------------
;;; PURE FUNCTIONS

(defn ns-to-uri-path
  "Converts namespace ns to a uri path under the web root namespace."
  [ns root-ns]
  (let [s (str ns)
        s (str/replace s (re-pattern (str "^" root-ns "\\.?")) "")
        s (str/replace s \. \/)
        s (str/replace s #"\-htm$" ".htm")]
    s))
#_(ns-to-uri-path (find-ns 'sistemi.site) (find-ns 'sistemi.site))
#_(ns-to-uri-path (find-ns 'sistemi.site.vision-htm) (find-ns 'sistemi.site))

(defn register-strings
  "Registers string translations for a namespace."
  [v ns root-ns]
  (when-let [m (if-let [m (ns-resolve ns 'strings)] (var-get m))]
    (let [keys (path/split (ns-to-uri-path ns root-ns))]
      (if (empty? keys)
        (alter-var-root v merge m)
        (alter-var-root v assoc-in keys m)))))

(defn- check-canonical
  "Make sure the default-locale uses the canonical name."
  [m cname ns]
  (let [dl locale/default-locale-kw]
    (if (contains? m dl)
      (log/warn (str "Ignoring extraneous entry for default locale " dl " in namespace " ns ".")))
    (assoc m dl cname)))

(defn- check-extensions
  "Updates a path translation map to add file extensions if ommitted."
  [m]
  (if-let [ext (re-find #"\.[^.]+$" (m locale/default-locale-kw))]
    (reduce (fn [m [locale name]]
              (if (re-find (re-pattern (str ext "$")) name)
                m
                (assoc m locale (str name ext))))
            m
            m) 
    m))

(defn- check-locales
  "Adds path entries for missing locales and removes unknown locale entries."
  [m ns]
  (let [locales (into #{} (map keyword locale/locales))
        cname (m locale/default-locale-kw)]
    (reduce
     (fn [m locale]
       (cond
        (and (contains? m locale) (locales locale)) m
        (locales locale) (do
                           (if (conf :internationalization :require-url-translations)
                             (log/warn (str "No entry for locale " locale " in namespace " ns ".")))
                             (assoc m locale cname))
        :default (do (log/info (str "Ignoring entry for unknown locale " locale " in namespace " ns "."))
                     (dissoc m locale))))
     m
     (set/union locales (keys m)))))

(defn- check-names
  "Checks that all name values are strings and encodes any unsafe characters."
  [m ns]
  (let [cname (m locale/default-locale-kw)]
    (reduce
     (fn [m [locale lname]]
       (let [is-string (string? lname)]
         (or is-string (log/error (str "Invalid entry '" lname "' for locale " locale " in namespace " ns ".")))
         (assoc m locale (str (url/encode-path (if is-string lname cname))))))
     m m)))

;; - Keep the canonical path uri safe (i.e., no special chars that need encoding).
;; - Encode the path translations as they are registered from the
;;   handler's namespace.

(defn register-names
  "Registers path name translations from a namespace."
  [lm-var cm-var ns root-ns]
  (when-let [m (if-let [m (ns-resolve ns 'names)] (var-get m))]
    (let [cpath (ns-to-uri-path ns root-ns)
          keys (path/split cpath)
          cname (last keys)
          m (-> m
                ;; sanity checks
                (check-canonical cname ns)
                (check-locales ns)
                (check-names ns)
                (check-extensions))]
      ;; update localized path map
      (alter-var-root lm-var assoc-in keys m)

      ;; update canonicalized path map
      (doseq [[locale name] m]
        (let [lpath (tr/localize-path cpath (var-get lm-var) locale)
              keys (concat [locale] (rest (path/split lpath)) [:name])]
          (alter-var-root cm-var assoc-in keys cname))))))

(defn register-handler
  "Registers the ring request handler for a namespace."
  [v ns root-ns]
  (when-let [f (if-let [f (ns-resolve ns 'handle)] (var-get f))]
    (let [keys (concat (path/split (ns-to-uri-path ns root-ns)) [:handler])]
      (alter-var-root v assoc-in keys f))))

;;; --------------------------------------------------
;;; VARS

(def strings
  "String translation map."
  {})

(def localized-paths
  "Maps canonical to localized paths."
  {})

(def canonicalized-paths
  "Maps localized to canonical paths."
  {})

(def handlers
  "Maps canonical paths to handler functions."
  {})

;;; --------------------------------------------------
;;; FUNCTIONS DEPENDENT ON VARS
(defn get-handler
  "Returns the handler fn for a canonical path."
  [path]
  (->> path
       path/split
       (get-in handlers)
       :handler))

(defn register-namespace
  "Registers string translations, path translations and the handler for a namespace."
  [ns root-ns]
  (log/info "Registering handler" ns)
  (register-strings #'strings ns root-ns)
  (register-names #'localized-paths #'canonicalized-paths ns root-ns)
  (register-handler #'handlers ns root-ns))

(defn load-handlers
  "Loads and registers url handler namespaces with prefix root-ns by searching in directory src."
  [root-ns dir]

  (doseq [ns (ns-find/find-namespaces-in-dir (io/as-file dir))]
    (when (str2/starts-with? (str ns) (str root-ns))
      (require ns)
      (register-namespace ns root-ns))))
