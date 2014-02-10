(ns sistemi.site.order.contact
  "Receives posted contact information."
  (:require [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [www.session :as sess]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]))

(defn handle
  [req]
  (f/with-valid-form sf/order-contact (:params req)
    (-> (tr/localize "shipping.htm")
        resp/redirect
        (assoc :session
          (assoc (:session req) :contact (f/values))))))
