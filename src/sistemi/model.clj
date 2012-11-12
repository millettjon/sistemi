(ns sistemi.model
  "Functions for working with models of nested components."
  (:require [sistemi.model.format :as format])
  (:use frinj.calc))

;; ---------- COMPONENT AND TREE RELATED ----------
(defmulti from-params
  "Creates a component from form parameters."
  :type)

(defn make
  "Makes a new component of type type."
  ([type] {:type type})
  ([type map]
     (merge map (make type)))
  ([type k v & kvs]
     (apply assoc (make type) (concat [k v] kvs))))

(defn children
  "Returns the components of a model."
  [cmp]
  (:components cmp []))

(defn add-child
  "Adds a component to a model."
  [cmp & args]
  (assoc cmp :components (concat (children cmp) args)))

(defn walk-children
  "Maps function f over children of component cmp."
  [cmp f]
  (assoc cmp :components (map f (children cmp))))

(defn walk
  "Maps function f over an entire component tree in a top down breadth first manner."
  [cmp f]
  (f (walk-children cmp #(walk % f))))

;; ----------- UTILS ---------------------
(defn area
  "Calculates :area based on :length and :width."
  [cmp]
  (fj* (:length cmp) (:width cmp)))

(defn bucket-index
  "Takes a seq of bucket boundaries and returns the index of the bucket into which value fits."
  [buckets value]
  (first
   (keep-indexed
    (fn [idx [min max]] (if (and (fj>= value min) (fj< value max)) idx))
    (partition 2 1 buckets))))

(defn total
  [cmp]
  (let [quantity (:quantity cmp 1)
        unit-price (or (:unit-price cmp)
                       (apply fj+ (keep :price (children cmp))))]
    (if unit-price
      (assoc cmp :unit-price unit-price
                 :price (fj* quantity unit-price))
      cmp)))

;; ----------- PRICE ---------------------
(defmulti explode
  "Explodes an item into a tree of constituent parts."
  :type)

(defn calc-price
  "Calculates the total price of an item and inserts it under the item's :price key."
  [item]
  (or (:price item)
      (-> item
          explode
          (walk total))))

(defn price
  "Returns the total price of an item."
  [item]
  (-> item
      calc-price
      :price
      (format/fj-round 2)))

(defn unqualify-keys
  "Recursively transforms all map keys from qualified keywords to unqualified ones."
  {:added "1.1"}
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(keyword (name k)) v] [k v]))]
    ;; only apply to maps
    (clojure.walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defmulti rollup :type)
(defmethod rollup :default [cmp] cmp)

(defmulti rollup-total :type)
(defmethod rollup-total :default [cmp] cmp)

(defn price-report
  [shelf]
  (-> shelf
      calc-price
      (walk rollup)
      (walk rollup-total)
      :rollup
      ;;clojure.walk/stringify-keys
      unqualify-keys
      ))

(defmulti html-price-report :type)

