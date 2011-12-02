(ns paypal.express-checkout
  "Functions for working with Paypal Express Checkout."
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [paypal.nvp :as nvp])
  (:use app.config))

;; References:
;; - https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_ECGettingStarted

;; TODO: Add unit tests.
;; TODO: Test a non USD currency (EUR?)
;;       PAYMENTREQUEST_0_CURRENCYCODE=currencyID

;; ? Where can the latest version number be found?
;; - https://www.x.com/people/PP_MTS_Chad/blog/2010/06/22/checking-for-the-most-recent-version
;; - https://www.x.com/thread/36729
;; - the live and test sites may be on different versions
;; - view the source and search for "web version:" on:
;;   - https://www.paypal.com          live
;;   - https://www.sandbox.paypal.com  test
;; ? Where are the release notes?
;; https://www.x.com/community/ppx/release_notes/blog


(defn set-express-checkout
  [conf params]
  (nvp/do-request conf "SetExpressCheckout" params))

(def sale-data {
                :PAYMENTREQUEST_0_PAYMENTACTION "Sale"
                :L_PAYMENTREQUEST_0_NAME0 "10% Decaf Kona Blend Coffee"
                :L_PAYMENTREQUEST_0_NUMBER0 "623083"
                :L_PAYMENTREQUEST_0_DESC0 "Size: 8.8-oz"
                :L_PAYMENTREQUEST_0_AMT0 "9.95"
                :L_PAYMENTREQUEST_0_QTY0 "2"
                :L_PAYMENTREQUEST_0_NAME1 "Coffee Filter bags"
                :L_PAYMENTREQUEST_0_NUMBER1 "623084"
                :L_PAYMENTREQUEST_0_DESC1 "Size: Two 24-piece boxes"
                :L_PAYMENTREQUEST_0_AMT1 "39.70"
                :L_PAYMENTREQUEST_0_QTY1 "2"
                :PAYMENTREQUEST_0_ITEMAMT "99.30"
                :PAYMENTREQUEST_0_TAXAMT "2.58"
                :PAYMENTREQUEST_0_SHIPPINGAMT "3.00"
                :PAYMENTREQUEST_0_HANDLINGAMT "2.99"
                :PAYMENTREQUEST_0_SHIPDISCAMT "-3.00"
                :PAYMENTREQUEST_0_INSURANCEAMT "1.00"
                :PAYMENTREQUEST_0_AMT "105.87"
                :PAYMENTREQUEST_0_CURRENCYCODE "USD"
                :ALLOWNOTE "1"
                :RETURNURL "https://www.sistemimoderni.com/en/confirm.htm"
                :CANCELURL "https://www.sistemimoderni.com/en/cancel.htm"})

;; SAMPLE CODE:
;; https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/library_code
;; https://www.paypal-labs.com/integrationwizard/ecpaypal/cart.php

(def sale-data {
                :PAYMENTREQUEST_0_PAYMENTACTION "Sale"
                :PAYMENTREQUEST_0_CURRENCYCODE "USD"
                :PAYMENTREQUEST_0_AMT "495.00"
                :L_PAYMENTREQUEST_0_NAME0 "Modern Shelving"
                :L_PAYMENTREQUEST_0_DESC0 "Modern Green 150x100x30"
                :L_PAYMENTREQUEST_0_AMT0 "495.00"
                :L_PAYMENTREQUEST_0_QTY0 "1"
                :RETURNURL "https://www.sistemimoderni.com/en/confirm.htm"
                :CANCELURL "https://www.sistemimoderni.com/en/cancel.htm"})

(def conf-data
  ;; Load paypal config and convert keys to uppercase.
  (let [m (conf :paypal)]
    (reduce #(assoc %1 (keyword (str/upper-case (name %2))) (%2 m)) {} (keys m))))

#_ (use 'clojure.java.browse)
#_(def checkout
    (set-express-checkout conf-data sale-data))
#_(browse-url (str "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=" (:TOKEN checkout)))

;; PROBLEMS:
;; description is empty
;; current purchase is empty
;; amount doesn't show up

;; CUSTOMIZING EXPRESS CHECKOUT
;; https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_ECCustomizing

;; ITEM DATA
;; PAYMENTREQUEST_n_XXXm: (n = payment method #, m = item #)
;; NAME
;; NUMBER
;; DESC
;; AMT
;; QTY

;; TOTALS
;; ------
;; ITEMAMT
;; TAXAMT
;; SHIPPINGAMT

;; PAYMENT REQUEST DATA
;; ------
;; PAYMENTREQUEST_n_XXX: (n = payment method #)
;; HANDLINGAMT
;; SHIPDISCAMT
;; INSURANCEAMT
;; AMT

;; MISC
;; ------
;; ALLOWNOTE



;; ----------
;; Redirect the client to: https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=tokenValue

;; ----------
;; Client does some paypal stuff...
;; https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=EC-9KW52943GT3422021
;;
;; Fake phone numbers:
;; - http://en.wikipedia.org/wiki/555_%28telephone_number%29
;; - 269 555 0100
;;
;; - the browser used to test the client in the sandbox must be
;;   logged in using a real paypal sandbox account
;;   - CI and developers must have a paypal sandbox account set up
;;   - regular testers can't test at all WTF?
;; - neither the total nor any items show up

;; ? how to we calculate price, vat, and shipping?

;; ----------
;; Client gets redirected back to the return or cancel url.
;; https://www.sistemimoderni.com/en/confirm.htm?token=EC-9KW52943GT3422021&PayerID=YGZNZL2T74SPS

;; To obtain details about an Express Checkout transaction, you can
;; invoke the GetExpressCheckoutDetails API operation.

;; To complete an Express Checkout transaction, you must invoke the
;; DoExpressCheckoutPayment API operation.
;;   - the total may be overridable here?
;;   - shipping, handling, tax, currency
