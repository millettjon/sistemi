(ns sistemi.site.cart.update
  "Updates the quantity for an item in the shopping cart."
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
  (f/with-form sf/cart-item-quantity (:params req)
    (let [{:keys [id quantity] :as vals} (f/values)
          resp (resp/redirect (tr/localize "/cart.htm"))]
      (if (f/errors?)
        resp
        (let [resp (cart/swap resp req cart/update id quantity)]
            (ev/send-event :cart/update (cart/get-item resp id) req) ; read updated cart from response
            resp)))))
