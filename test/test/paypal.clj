(ns test.paypal
  (:use clojure.test
        paypal
        app.config))

(deftest check-versions
  "Check that the live API versions for the sandbox and production sites match what is expected."
  (doseq [site [:sandbox :production]]
    (with-conf {:site site}
      (let [expected (get-in paypal.core/site-conf [site :version])
            actual (last (re-find #"^([\d\.]+)-" (get-version)))]
        (is (= expected actual) (str "A new version of the " (name site) " PayPal API is available."))))))

(def sale-data {:paymentrequest_0_paymentaction "Sale"
                :paymentrequest_0_currencycode "USD"
                :paymentrequest_0_amt "495.00"
                :l_paymentrequest_0_name0 "Modern Shelving"
                :l_paymentrequest_0_desc0 "Modern Green 150x100x30"
                :l_paymentrequest_0_amt0 "495.00"
                :l_paymentrequest_0_qty0 "1"
                :returnurl "http://localhost:5000/en/confirm.htm"
                :cancelurl "http://localhost:5000/en/cancel.htm"})

;; TODO: checkout handler
;; - calculate the return and cancel urls based on the url of the current request.
;; - call set-express-checkout to get the token
;; - redirect the client to the appropriate url
;; - client: completes paypal workflow and gets redirected back

;; TODO: confirm handler:
;; - call GetExpressCheckoutDetails
;; - call DoExpressCheckoutPayment
;; - cancel handler:

(def checkout-map
  (with-conf (conf :paypal)
    (xc-setup sale-data)))

#_(use 'clojure.java.browse)
#_(browse-url (str "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=" (:token checkout-map)))
;; buyer_1301327961_per@sistemimoderni.com
;; visa/delta/electron
;;   4342506098458263
;;   03/16
;;   000
;; must use valid address and phone number

;; http://localhost:5000/en/order/confirm.htm?token=EC-2CX87631XB186964J&PayerID=YGZNZL2T74SPS
(def details-map
  (with-conf (conf :paypal)
    (xc-details {:token "EC-5LR968397J2504621"})))
(:token details-map)
(:payerid details-map)
(:paymentrequest_0_amt details-map)
(:paymentrequest_0_paymentaction details-map)

(def payment-map
  (with-conf (conf :paypal)
    (xc-pay (merge
             (select-keys details-map [:token :payerid :paymentrequest_0_amt])
             {:paymentrequest_0_paymentaction "Sale"}))))


;; CUSTOMIZING EXPRESS CHECKOUT
;; https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_ECCustomizing

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

;; http://localhost:5000/en/order/confirm.htm?token=EC-4MA98371JS754964S&PayerID=YGZNZL2T74SPS

;; how to do automated testing?
;; - html-unit
;; - clojurescript

;; ----------
;; Client gets redirected back to the return or cancel url.

;; To obtain details about an Express Checkout transaction, you can
;; invoke the GetExpressCheckoutDetails API operation.

;; To complete an Express Checkout transaction, you must invoke the
;; DoExpressCheckoutPayment API operation.
;;   - the total may be overridable here?
;;   - shipping, handling, tax, currency

