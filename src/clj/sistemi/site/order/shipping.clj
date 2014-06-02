(ns sistemi.site.order.shipping
  "Receives posted shipping information."
  (:require [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [www.session :as sess]
            [sistemi.form :as sf]
            [sistemi.order :as order]
            [www.cart :as cart]
            [www.form :as f]))

(defn taxable?
  "Returns true if an address is taxable."
  [address]
  (= :FR (:country address)))

(defn update-shipping
  [cart shipping]
  (-> cart
      (assoc-in [:shipping :address] shipping)
      (assoc :taxable? (taxable? shipping))
      order/recalc))

(defn handle
  [{:keys [params session] :as req}]
  (f/with-valid-form sf/order-shipping params
    (-> (tr/localize "payment.htm")
        resp/redirect
        (cart/swap req update-shipping (f/values))
        )))
