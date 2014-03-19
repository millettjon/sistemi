(ns sistemi.product
  "Functions for working with sistemi products."
  (:use frinj.ops))

(defmulti from-params
  "Creates a component from form parameters."
  :type)

(defmulti to-params
  "Coerce a component into a map of form parameters."
  :type)

(defn fj-params-to-str
  "Converts frinj values in a map to strings in cm."
  [product]
  (reduce (fn [m [k v]]
            (let [v (if (isa? (class v) frinj.core.fjv)
                      (-> (to v :cm) :v str)
                      v)]
              (assoc m k v)))
          {}
          product))

(defn cm->mm
  "Converts cm to mm."
  [n]
  (* n 10))

;; TODO Remove this if possible and force the same set of options in the
;;      spreadsheet as the code model.
(defn convert-finish
  "Converts the item finish value to the format expected by the spreadsheet."
  [finish]
  (case finish
    (:laquer-matte :laquer-satin :laquer-glossy) "laquer"
    :valchromat-oiled "oil"
    :valchromat-raw "raw"))
