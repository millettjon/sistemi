(ns sistemi.site.cart.add
  "Adds an item or items to the shopping cart."
  (:require [clojure.tools.logging :as log]
            [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]))

(defn handle
  [req]
  ;; Determine the type of item being added.
  (f/with-form sf/cart-item (:params req)
    (if (f/errors?)
      ;;(resp/redirect (get-in req [:headers "referer"]) {:query (:params req)})
      ;;(resp/redirect (tr/localize "/shelving.htm" {:query (:params req)}))
      ;; ? how to list the errors?
      (do
        (log/error f/*fields*) ;; TODO: how to display errors?
        (throw "got errors")
        )
      ;; Validate the customization attributes.
      (f/with-form ((f/default :type) sf/cart-items) (:params req)
        (if (f/errors?)
          (do
            (log/error f/*fields*) ;; TODO: how to display errors?
            (throw "got errors")
            )
          (do
            ;; add/update cart-item
            (log/info (f/values))
            (-> (tr/localize "/cart.htm")
                resp/redirect
                (cart/swap req cart/add (f/values)))))))))

(sistemi.registry/register)
