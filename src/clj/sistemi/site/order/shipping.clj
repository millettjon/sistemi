(ns sistemi.site.order.shipping
  "Receives posted shipping information."
  (:require [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [www.session :as sess]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]))

(defn handle
  [req]
  (f/with-valid-form sf/order-shipping (:params req)
    (-> (tr/localize "payment.htm")
        resp/redirect
        (assoc :session
          (assoc (:session req) :shipping (f/values))))))
