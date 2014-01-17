(ns sistemi.site.order.contact
  "Receives posted contact and shipping information."
  (:require [clojure.tools.logging :as log]
            [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [www.session :as sess]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]))

;; validate
;; save it in the session
;; redirect to payment page
(defn handle
  [req]
  ;; Validate
  (f/with-valid-form sf/order-contact (:params req)
    (log/info (f/values))
    (-> (tr/localize "payment.htm")
        resp/redirect
        (assoc-in [:session :contact] (select-keys (f/values) [:first-name :last-name :email :phone]))
        (assoc-in [:session :shipping] (select-keys (f/values) [:address1 :address2 :city :region :code :country]))
        )))
