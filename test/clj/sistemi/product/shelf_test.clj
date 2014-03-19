(ns sistemi.product.shelf-test
  (:require [sistemi.order :as order])
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
  (let [price (order/get-price shelf order)]
    (is (-> price :total :v))))
