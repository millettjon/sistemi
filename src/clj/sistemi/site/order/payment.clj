(ns sistemi.site.order.payment
  "Receives posted payment information."
  (:require [clojure.tools.logging :as log]
            [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [www.session :as sess]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]))

;; validate
;; save it in the session
;; redirect to review page
(defn handle
  [req]
  ;; Validate
  (f/with-valid-form sf/order-payment (:params req)
    (log/info (f/values))
    (-> (tr/localize "payment.htm")
        resp/redirect
        (assoc-in [:session :billing] (select-keys (f/values) [:first-name :last-name :email :phone]))
        )))

;; TODO: handler to finalize payment
;; TODO: thank you/receipt page
