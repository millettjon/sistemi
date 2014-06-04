(ns sistemi.product.shelf-test
  (:require [sistemi.order :as o]
            sistemi.product.shelf)
  (:use clojure.test))

(def shelf
  {:type :shelf
   :color {:rgb "#C51D34", :type :ral, :code 3027},
   :quantity 3
   :finish :laquer-matte
   :width 120
   :depth 30})

(def order
  {:taxable true})

(deftest get-price
  (let [price (o/get-price shelf order)]
    (is (-> price :total :v))))
