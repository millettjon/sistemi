(ns www.cart-test
  (:require [www.cart :as cart]
            [sistemi.order :as order]
            [util.frinj :as f])
  (:use [clojure.test]))

(defmethod order/get-price :default
  [item order]
  {:total (f/fj-eur 3)}
  )

(deftest test-add
  (let [cart (cart/add nil {:id -1 :item :foo})]
    ;; Create a new cart and add a new item.
    (is (not (nil? cart)))
    (is (= 1 (count (:items cart))))
    (let [[k v] (-> cart :items first)]
      (is (= 0 k (:id v)))    ; item id is 0
      (is (= :foo (:item v))))

    ;; Add a second item.
    (let [cart (cart/add cart {:id -1 :item :bar})]
      (is (= 2 (count (:items cart))))
      (let [[k v] (-> cart :items second)]
        (is (= 1 k (:id v)))    ; item id is 0
        (is (= :bar (:item v))))

      ;; Update item 0.
      (let [cart (cart/add cart {:id 0 :item :baz})]
        (is (= 2 (count (:items cart))))
        (is (= :baz (get-in cart [:items 0 :item])))))))
