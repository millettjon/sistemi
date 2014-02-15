(ns util.frinj
  (:require [frinj.core :as f])
  (:import frinj.core.fjv))

(defn fj-eur
  "Builds a frinj euro value. Useful since fj autoconverts to the
fundamental currency unit which is USD."
  [amount]
  (f/fjv. amount {:EUR 1}))

(defn fj-round
  [number places]
  (let [v (:v number)]
    (assoc number :v (.setScale (bigdec v) places BigDecimal/ROUND_HALF_EVEN))))
#_ (fj-round (fj 50.1234 :EUR) 2)

(defn fj-bd_
  [{:keys [v] :as num} divisor places]
  (assoc num :v (.divide v (bigdec divisor) places BigDecimal/ROUND_HALF_EVEN)))


#_ (/ 1 (bigdec 3))
#_ (.divide 1M 3M 2 BigDecimal/ROUND_HALF_EVEN)
;; convert to double, divide, convert to bigint?
;; or, just store as floating point?
