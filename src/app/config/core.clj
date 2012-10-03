(ns app.config.core
  "Global configuration map and related helper functions."
  (:require [clojure.string :as str]
            [util.path :as path]
            [util.string :as str2]
            [harpocrates.core :as gpg]
            [util.environment :as env]
            )
  (:use [clojure.contrib.map-utils :only (deep-merge-with)]
        app.config.core
        (util except)))

;; ===== VARS =====
(def config 
  "Global configuration map."
  {})

;; ===== PRIVATE =====
(defn- read-string-safely
  "Reads a string and returns a clojure data structure. If the read fails, the unmodified string
  is returned."
  [s]
  (safely (eval (read-string s)) s))

(defn- normalize-key
  "Normalizes a key by replacing underscore with dash and converting to lowercase and keywordizing.
Useful for converting environment variable names to clojure map keys."
  [k]
  (-> k (str/replace "_" "-") str/lower-case keyword))

;; ===== PUBLIC =====
(defn environment-map
  "Returns the environment as a map. Keys are normalized by converting underscore to dash,
   lowercasing, and keywordizing. Clojure parseable values are coerced to clojure.  The argument
   keys specifies the list of environment variables to include."
  [& keys]
  (reduce (fn [m kv]
            (let [[k v] kv]
              (assoc m (normalize-key k) (read-string-safely v))))
          {}
          (select-keys env/map keys)))

(defn merge-configs
  "Deeply merges the list of configuration maps. Values from rightmost maps take precedence."
  [& maps]
  (apply deep-merge-with (fn [& l] (last l)) maps))

(defn file-map
  "Returns a configuration file as a map."
  [path]
  (eval (read-string (slurp path))))

(defn dir-map
  "Reads and deeply merges all configuration files in a directory. Configuration files are expected to contain
   a single clojure map. If a file default.clj exists, it is read and merged directly into the result map.
   Files ending in .clj (e.g., foo.clj) are read and merged into the result map under the key of their basename.
   Files ending in .clj.gpg are first decrypted and then read. Gpg options can be passed in opts under the gpg key."
  [dir & opts]
  (let [opts (merge {:gpg nil} (apply hash-map opts))]
    (reduce (fn [m f]
              (let [name (path/name f)
                    basename (path/shortname f)
                    decrypt #(apply gpg/decrypt % (flatten (into [] (:gpg opts))))
                    m2 (cond
                        (str2/ends-with? name ".clj") (file-map f)
                        (str2/ends-with? name ".clj.gpg") (decrypt f))]
                (cond
                 (nil? m2) m
                 (= basename "default") (merge-configs m m2)
                 :else (merge-configs m {(keyword basename) m2}))))
            {} (path/files dir))))

(defn set-config!
  "Applies function merge-configs to the list of configuration maps and sets the result in config."
  [& maps]
  ;; Ensure that all arguments are maps.
  (doseq [m maps] (check m map?))
  (alter-var-root #'config (constantly (apply merge-configs maps))))
