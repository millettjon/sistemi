(ns util.edn
  (:require [util.frinj :as f]
            [clojure.edn :as edn]
            [util.path :as p])
  (:use ordered.map)
  (:refer-clojure :exclude [read-string slurp]))

(def read-opts
  {:readers (merge f/readers
                   {'ordered/map ordered.map/ordered-map})})

(defn read-string
  "Calls clojure.edn/read-string with custom options."
  [s]
  (edn/read-string read-opts s))

(defn slurp
  [file]
  (let [f (p/new-path file)]
    (if (p/exists? f)
      (-> f p/to-file clojure.core/slurp read-string))))
