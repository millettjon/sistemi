(ns config
  "Merges configuration maps from different sources into a single map."
  (:require [clj-yaml.core :as yaml]
            [clojure.string :as str])
  (:use [util :only (safely)]
        [clojure.contrib.def :only (defvar)]
        [clojure.contrib.map-utils :only (deep-merge-with)]))

;;
;; Configuration maps are expected to be in yaml format.
;; 
;; Requirements of configuration format.
;; - can represent nested data structures
;; - human readable
;; - can add comments (json doesn't support comments)
;; - safe (clj is not safe)
;;
;; Possible Features:
;; - caching and reloading (see fnmap)
;; - log overidden and merged settings
;; - report query statistics to find unused settings (see fnmap)
;; - advanced queries regex, xpath
;; - support more formats: json, xml, properties
;;
;; USAGE:
;; ;; To initialize.
;; (use 'config)
;;
;; (environment-map)
;; (environment-map "USERNAME" "HOME")
;; (file-map "etc/development.yaml")
;;
;; (set-config!
;;  (file-map "etc/default.yaml")
;;  (file-map  (str "etc/" (name (get-run-level)) ".yaml"))
;;  (environment-map "DATABASE_URL" "paypal"))
;;
;; (merge-configs
;;  (file-map "etc/default.yaml")
;;  (file-map  (str "etc/" (name (get-run-level)) ".yaml"))
;;  (environment-map "DATABASE_URL" "paypal"))
;;
;; To read config values.
;; (use '(config :only (*config*))
;; (:foo *config*)
;; (get-in *config* [:foo :bar :baz])


(defn- read-yaml-safely
  "Coerces a yaml string to a clojure data structure. If the coercion fails, the unmodified string
  is returned."
  [s]
  (safely (yaml/parse-string s) s))

(defn- normalize-key
  [k]
  (-> k (str/replace "_" "-") str/lower-case keyword))

(defn environment-map
  "Returns the environment as a map.
   Keys are normalized by converting underscore to dash, lowercasing, and kewyordizing.
   YAML parseable values are coerced from yaml to clojure.
   A list of keys can be supplied to select the desired map entries."
  [& keys]
  (reduce (fn [m kv]
            (let [[k v] kv]
              (assoc m (normalize-key k) (read-yaml-safely v))))
          {}
          (let [env (into {} (System/getenv))]
            (if (empty? keys)
              env
              (select-keys env keys)))))

(defn file-map
  "Returns a configuration file as a map."
  [path]
  (yaml/parse-string (slurp path)))

(defvar *config* (environment-map)
  "Global configuration map. Initialized to the environment.")

(defn merge-configs
  "Deeply merges the list of configuration maps. Values from rightmost maps take precedence."
  [& maps]
  (apply deep-merge-with (fn [a b] b) maps))

(defn set-config!
  "Applies merge-configs to the list of configuration maps and sets the result in *config*."
  [& maps]
  (alter-var-root #'*config* (constantly (apply merge-configs maps))))
