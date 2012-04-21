(ns sistemi.site.order.checkout
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [sistemi.translate :as tr]
            [www.url :as url])
  (:use [locale.core :only (full-locale)]
        [ring.util.response :only (redirect)]))

;; TODO: Log paypal interaction.
;; TODO: Persist transaction to database.

;; fidjet breaks code navigation
;; ? is this because the generated functions have no source metadata?
;; ? how does code navigation work?

(use 'paypal)
(use 'app.config)

(defn xc-money
  "Formats a money amount for use with PayPal."
  [amount]
  (format "%1.2f" amount))

 "Custom Shelving: 150x100x30; Modern; Green."
(defn make-description
  "Makes a one line description of the shelving specifications."
  [req]
  (let [m (:params req)]
    (str "Shelving: " (:width m) "x" (:height m) "x" (:depth m) "cm; " (:cutout m) "; " (str/upper-case (:color m)))))

(defn make-paypal-order
  [req amount]
  (let [amount (xc-money amount)
        sale-data {:paymentrequest_0_paymentaction "Sale"
                   :paymentrequest_0_currencycode "EUR"
                   :paymentrequest_0_amt amount
                   :l_paymentrequest_0_name0 "Custom Shelving"
                   :l_paymentrequest_0_desc0 (make-description req)
                   :l_paymentrequest_0_amt0 amount
                   :l_paymentrequest_0_qty0 "1"
                   :returnurl (tr/qualifys "confirm.htm")
                   :cancelurl (tr/qualifys "cancel.htm")
                   :localecode (full-locale (:locale req))}]
    (with-conf (conf :paypal)
      (xc-setup sale-data))))

(defn redirect-to-express-checkout
  [order]
  (with-conf (conf :paypal)
    (xc-redirect-url (:token order))))

(defn handle
  [req]
  (let [amount (Double. ^String (get-in req [:params :amount]))
        order (make-paypal-order req amount)]
    (log/info "XC ORDER" order)
    (redirect (redirect-to-express-checkout order))))

(sistemi.registry/register)
