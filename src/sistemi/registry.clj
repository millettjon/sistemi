(ns sistemi.registry
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.tools.logging :as log]
            [locale.translate :as tr]
            [util.path :as path]
            [www.url :as url])
  (:use [locale.core :only (locales default-locale)]))

;;; --------------------------------------------------
;;; FUNCTIONS

(defn- load-file-safely
  "Loads a clj file catching and logging any exceptions."
  [file]
  (let [file (str file)]
    (try
      (log/info "Loading file" file)
      (load-file file)
      (catch Exception x
        (log/error x (str "Exception loading file " file)))
      (catch LinkageError x
        (log/error x (str "LinkageError loading file " file))))))

(defn load-files
  "Loads all clj files in a directory in a breadth first manner."
  [root]
  ;; Load the namespace for the root if there is one.
  (let [file (str root ".clj")]
    (if (path/exists? (str root ".clj"))
      (load-file-safely file)))

  ;; Load all sub namespaces in breadth first order.
  (doseq [file (filter #(= (path/extension %) "clj") (path/file-seq-bf root))]
    (load-file-safely (str file))))

(defn ns-to-uri-path
  "Converts namespace ns to a uri path under the web root namespace."
  [ns root-ns]
  (let [s (name (ns-name ns))
        s (str/replace s (re-pattern (str "^" (ns-name root-ns) "\\.?")) "")
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
  (let [dl (keyword default-locale)]
    (if (contains? m dl)
      (log/warn (str "Ignoring extraneous entry for default locale " dl " in namespace " ns ".")))
    (assoc m dl cname)))

(defn- check-extensions
  "Updates a path translation map to add file extensions if ommitted."
  [m]
  (if-let [ext (re-find #"\.[^.]+$" (m (keyword default-locale)))]
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
  (let [locales (into #{} (map keyword locales))
        cname (m (keyword default-locale))]
    (reduce
     (fn [m locale]
       (cond
        (and (contains? m locale) (locales locale)) m
        (locales locale) (do (log/error (str "No entry for locale " locale " in namespace " ns "."))
                             (assoc m locale cname))
        :default (do (log/error (str "Ignoring entry for unknown locale " locale " in namespace " ns "."))
                     (dissoc m locale))))
     m
     (set/union locales (keys m)))))

(defn- check-names
  "Checks that all name values are strings and encodes any unsafe characters."
  [m ns]
  (let [cname (m (keyword default-locale))]
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

(defn register
  "Allows a handler to register itself when loaded."
  []
  (let [root-ns (find-ns 'sistemi.site)]
    (log/info "Registering handler" *ns*)
    (register-strings #'strings *ns* root-ns)
    (register-names #'localized-paths #'canonicalized-paths *ns* root-ns)
    (register-handler #'handlers *ns* root-ns)))
