(ns util.frinj
  (:require [frinj.core :as f]
            [frinj.ops :as fo])
  (:import frinj.core.fjv))

(defn fj-eur
  "Builds a frinj euro value. Useful since fj autoconverts to the
fundamental currency unit which is USD."
  [amount]
  (f/fjv. amount {:EUR 1}))

(defn fj-currency
  "Builds a frinj currency value. Useful since fj autoconverts to the
fundamental currency unit which is USD."
  [amount currency]
  (f/fjv. amount {currency 1}))

(defn reader
  [m]
  (merge (f/fjv. nil nil) m))

(def readers
  {'frinj.core.fjv reader})

(defn fj-round
  [number places]
  (let [v (:v number)]
    (assoc number :v (.setScale (bigdec v) places BigDecimal/ROUND_HALF_EVEN))))
#_ (fj-round (fo/fj 50.1234 :EUR) 2) ; works
#_ (fj-round (fo/fj 50.12M :EUR) 2)  ; works
#_ (fj-round (fj-currency 50.12M :EUR) 2)

(defn fj-bd_
  [{:keys [v] :as num} divisor places]
  (assoc num :v (.divide v (bigdec divisor) places BigDecimal/ROUND_HALF_EVEN)))

(defn fj-max
  "Returns the greatest of the values."
  [& vals]
  (reduce (fn [& [a b :as v]]
            (cond
             (empty? v) nil
             (fo/fj< a b) b
             :else a))
          vals))

(def fj-compare
  (comparator (fn [x y] (fo/fj< x y))))

(defn fj-sort
  [coll]
  (sort fj-compare coll))

(defn fj-sort-by
  [keyfn coll]
  (sort-by keyfn fj-compare coll))

#_ (/ 1 (bigdec 3))
#_ (.divide 1M 3M 2 BigDecimal/ROUND_HALF_EVEN)
;; convert to double, divide, convert to bigint?
;; or, just store as floating point?
