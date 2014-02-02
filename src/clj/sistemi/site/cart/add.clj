(ns sistemi.site.cart.add
  "Adds an item or items to the shopping cart."
  (:require [clojure.tools.logging :as log]
            [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]
            [www.event :as ev]))

(def names {})

(defn handle
  [req]
  ;; Validate generic cart-item parameters.
  (f/with-valid-form sf/cart-item (:params req)

    ;; Validate item specific paramters.
    (f/with-valid-form ((f/default :type) sf/cart-items) (:params req)
      
      ;; Add or update cart item.
      (let [vals (f/values)]
        (ev/send-event :cart/add vals req)
        (-> (tr/localize "/cart.htm")
            resp/redirect
            (cart/swap req cart/add vals))))))
