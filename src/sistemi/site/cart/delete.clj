(ns sistemi.site.cart.delete
  "Deletes an item from the shopping cart."
  (:require [clojure.tools.logging :as log]
            [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]))

(def names {})

(defn handle
  [req]
  ;; Determine the type of item being added.
  (f/with-form sf/cart-item-id (:params req)
    (-> (tr/localize "/cart.htm")
        resp/redirect
        (cart/swap req cart/delete (:id (f/values))))))

(sistemi.registry/register)
