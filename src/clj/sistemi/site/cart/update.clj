(ns sistemi.site.cart.update
  "Updates the quantity for an item in the shopping cart."
  (:require [clojure.tools.logging :as log]
            [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]))

(def names {})

(defn handle
  [req]
  (f/with-form sf/cart-item-quantity (:params req)
    (let [{:keys [id quantity]} (f/values)
          resp (resp/redirect (tr/localize "/cart.htm"))]
      (if (f/errors?)
        resp
        (cart/swap resp req cart/update id quantity)))))

