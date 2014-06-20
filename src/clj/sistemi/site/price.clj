(ns sistemi.site.price
  (:require [www.form :as f]
            [sistemi.form :as sf]
            [sistemi.product :as p]
            [sistemi.format :as fmt]
            [sistemi.order :as o]
            [ring.util.response :as rsp]))

(def names {})

(defn handle
  "Calculates and returns the price of an item."
  [req]
  ;; Validate generic cart-item parameters.
  (f/with-valid-form sf/cart-item (:params req)

    ;; Validate item specific paramters.
    (f/with-valid-form
      ((f/default :type) sf/cart-items)
      (:params req)
      
      ;; Add or update cart item.
      (let [vals (f/values)]
        (-> vals
            (o/get-price {})
            :total
;;            :foo
            fmt/eur-short
            rsp/response
            (rsp/content-type "text/plain; charset=utf-8"))))))
