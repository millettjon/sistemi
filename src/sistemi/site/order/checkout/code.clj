(ns sistemi.site.order.checkout
  (:require [clojure.tools.logging :as log]
            [www.url :as url])
  (:use [ring.util.response :only (redirect)]))

;; TODO: Log paypal interaction.
;; TODO: Persist transaction to database.

;; fidjet breaks code navigation
;; ? is this because the generated functions have no source metadata?
;; ? how does code navigation work?

;; TODO:keep app.config code out of both paypal and web handlers
;;   how? where? why?

(use 'paypal)
(use 'app.config)

;; call paypal checkout function
;; log paypal interaction
;; persist to database
;; parse token from response and redirect

(defn xc-money
  "Formats a money amount for use with PayPal."
  [amount]
  (format "%1.2f" amount))

(defn canonicalize
  "Canonicalizes and localizes a url path."
  [req path]
  (url/canonicalize req ((:luri req) path)))

;; TODO: Pass the locale to the paypal side.
(defn make-paypal-order
  [req amount]
  (let [amount (xc-money amount)
        sale-data {:paymentrequest_0_paymentaction "Sale"
                   :paymentrequest_0_currencycode "EUR"
                   :paymentrequest_0_amt amount
                   :l_paymentrequest_0_name0 "Modern Shelving"
                   :l_paymentrequest_0_desc0 "Custom Shelving: 150x100x30; Modern; Green."
                   :l_paymentrequest_0_amt0 amount
                   :l_paymentrequest_0_qty0 "1"
                   :returnurl (canonicalize req "confirm.htm")
                   :cancelurl (canonicalize req "cancel.htm")}]
    sale-data
    #_(with-conf (conf :paypal)
        (xc-setup sale-data))))

;; TODO: Redirect to they paypal express checkout process.
#_(defn redirect-to-paypal-checkout
  [order]
  )
;; TODO: Factor out url to conf.
#_(browse-url (str "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=" (:token checkout-map)))


(defn handle
  [req]
  (let [amount (Double. (get-in req [:params :amount]))
        order (make-paypal-order req amount)]
    (log/info "=====ORDER=====" order)
    )
  (redirect "http://www.yahoo.com"))
