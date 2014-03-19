(ns sistemi.product.bookcase-test
  (:require [sistemi.order :as order])
  (:use clojure.test))

(def bookcase
  {:type :bookcase
   :color {:rgb "#C51D34", :type :ral, :code 3027},
   :quantity 3
   :finish :laquer-matte
   :width 120
   :height 200
   ;; Is cutount needed?
   :depth 30})

(def order
  {:taxable true})

(deftest get-price
  (let [price (order/get-price bookcase order)]
    (is (-> price :total :v))))
