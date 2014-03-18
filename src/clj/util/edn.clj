(ns util.edn
  (:require [util.frinj :as f]
            [clojure.edn :as edn])
  (:use ordered.map)
  (:refer-clojure :exclude [read-string]))

(def read-opts
  {:readers (merge f/readers
                   {'ordered/map ordered.map/ordered-map})})

(defn read-string
  "Like"
  [s]
  (edn/read-string read-opts s))
