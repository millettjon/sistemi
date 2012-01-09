(ns sistemi.site.order.pay
  (:require [clojure.tools.logging :as log]
            [www.url :as url])
  (:use paypal
        app.config
        [locale.core :only (full-locale)]
        [ring.util.response :only (redirect)]))

(defn pay-paypal-order
  [amount req]
  (let [pay-data {:paymentrequest_0_paymentaction "Sale"
                  :paymentrequest_0_currencycode "EUR"
                  :paymentrequest_0_amt amount}]
    (with-conf (conf :paypal)
      (xc-pay (merge pay-data (select-keys (req :params) [:token :payerid]))))))

;; hmn, paypal redirects like this: so it has to be https!
;; http://localhost:5000/en/order/confirm.htm?token=EC-2CX87631XB186964J&PayerID=YGZNZL2T74SPS
;; TODO: save the pertinent items in a session cookie
;;   - amount
;;   - is there a way to have paypal pass through an order id we generate?
(defn handle
  [req]
  (let [amount (get-in req [:params :paymentrequest_0_amt])
        token (req :token)
        result (pay-paypal-order amount req)]
    (log/info "XC PAY" result)
    (redirect (url/qualify (url/localize {:path "view.htm" :query {:id token}} req)))))

;; TODO: Can something else other than the token be used?
;; TODO: Are there any other related nvp api calls?

;; TODO: How do we handle two sessions at once in multiple windows?
;; - no way to differentiate if using sessions (memory/db/cookie)
;; - pass a unique transaction id in a hidden form field? (could be saved in a cookie) 

;; TODO: What is the maximum cookie size?
;; http://myownplayground.atspace.com/cookietest.html
;; 4095k max size per cookie
;; 4095k total bytes per domain
;; 20 cookies max per domain
;; 300 total cookies
