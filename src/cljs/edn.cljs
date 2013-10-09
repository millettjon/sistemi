(ns edn
  (:require [cljs.reader]
            [clojure.walk :as walk]))

(defmulti keywordize-vals :type)

(defmethod keywordize-vals :default
  [m]
  m)

(defn ^:export stringify
  "Converts a javascript object into an edn map. All keys are
keywordized. A :type key is assumed to exist and is used as the
dispatch fn for the multi-method keywordize-vals."
  [object]
  (-> object
      js->clj
      walk/keywordize-keys
      (#(assoc % :type (-> % :type keyword)))
      keywordize-vals
      pr-str))

(defn ^:export objectify
  "Reads an edn string and converts it to a javascript object."
  [s]
  (-> s
      cljs.reader/read-string
      clj->js))
