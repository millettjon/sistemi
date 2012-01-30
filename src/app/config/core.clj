(ns app.config.core
  "Global configuration map and related helper functions."
  (:require [clj-yaml.core :as yaml]
            [clojure.string :as str])
  (:use [clojure.contrib.map-utils :only (deep-merge-with)]
        app.config.core
        (util environment except)))

;; ===== VARS =====
(def config 
  "Global configuration map. Initialized to the environment."
  {})

;; ===== PRIVATE FUNCTIONS =====
(defn- read-yaml-safely
  "Coerces a yaml string to a clojure data structure. If the coercion fails, the unmodified string
  is returned."
  [s]
  (safely (yaml/parse-string s) s))

(defn- normalize-key
  "Normalizes a key by replacing underscore with dash and converting to lowercase."
  [k]
  (-> k (str/replace "_" "-") str/lower-case keyword))

;; ===== PUBLIC FUNCTIONS =====
(defn environment-map
  "Returns the environment as a map. Keys are normalized by converting underscore to dash,
   lowercasing, and keywordizing.  YAML parseable values are coerced from yaml to clojure.  A list
   of keys can be supplied to restrict the set of environment variables."
  [& keys]
  (reduce (fn [m kv]
            (let [[k v] kv]
              (assoc m (normalize-key k) (read-yaml-safely v))))
          {}
          (if (empty? keys)
            environment
            (select-keys environment keys))))

(defn file-map
  "Returns a configuration file as a map."
  [path]
  (yaml/parse-string (slurp path)))

(defn merge-configs
  "Deeply merges the list of configuration maps. Values from rightmost maps take precedence."
  [& maps]
  (apply deep-merge-with (fn [& l] (last l)) maps))

(defn set-config!
  "Applies function merge-configs to the list of configuration maps and sets the result in config."
  [& maps]
  ;; Ensure that all arguments are maps.
  (doseq [m maps] (check m map?))
  (alter-var-root #'config (constantly (apply merge-configs maps))))

;; Intialize the configuration to the environment.
(set-config! (environment-map))






