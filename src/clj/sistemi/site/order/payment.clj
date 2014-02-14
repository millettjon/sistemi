(ns sistemi.site.order.payment
  "Receives posted payment information."
  (:require [clojure.tools.logging :as log]
            [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [www.session :as sess]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]

            [clj-stripe.common :as common]
            [clj-stripe.customers :as customers]
            [clj-stripe.charges :as charges]
            [app.config :as c]
            ))


;;
;; flow
;; payment.htm
;;  - sends cc info to stripe
;;  - gets back info and token
;;  - submits form w/ token to server
;; payment handler
;;  - makes txn
;;  - 

;; https://stripe.com/docs/tutorials/charges
;; ? do we want to save stripe customers?
;; {:stripeToken tok_103PYg27flN4SizkrDfPQ4tg}

;; Create a new customer.
#_ (let [key (c/conf :stripe :secret-key)
         card-token "tok_103PYg27flN4SizkrDfPQ4tg"]
     (common/with-token key
       (common/execute
        (customers/create-customer
         (common/card card-token)
         (customers/email "test@sistemimoderni.com")))))

;; {:discount nil, :metadata {}, :currency nil, :cards {:object "list", :count 1, :url "/v1/customers/cus_3PZQOK7KQ23ygs/cards", :data [{:country "US", :customer "cus_3PZQOK7KQ23ygs", :exp_month 5, :last4 "4242", :address_zip_check nil, :name nil, :address_line2 nil, :fingerprint "RYdQB7XGFSIvdwRm", :cvc_check "pass", :address_line1 nil, :object "card", :address_city nil, :address_zip nil, :address_state nil, :address_line1_check nil, :type "Visa", :exp_year 2014, :address_country nil, :id "card_103PYg27flN4SizkbjtSBpnR"}]}, :livemode false, :object "customer", :delinquent false, :created 1391186206, :email "test@sistemimoderni.com", :subscription nil, :subscriptions {:object "list", :count 0, :url "/v1/customers/cus_3PZQOK7KQ23ygs/subscriptions", :data []}, :account_balance 0, :default_card "card_103PYg27flN4SizkbjtSBpnR", :id "cus_3PZQOK7KQ23ygs", :description nil}


;; Retreive an existing customer by id.
#_ (let [key (c/conf :stripe :secret-key)
         customer-id "cus_3PZQOK7KQ23ygs"]
     (common/with-token key
       (common/execute
        (customers/get-customer customer-id))))

;; Create a charge for a customer.
(let [key (c/conf :stripe :secret-key)
      customer-id "cus_3PZQOK7KQ23ygs"
      customer (common/customer customer-id)
      amount (common/money-quantity 5000 "usd")]
     (common/with-token key
       (common/execute
        (charges/create-charge amount customer (common/description "This an test charge.")))))

;; - validate (or, rely on stripe?)
;; - create txn parameters
;;   - price, description, etc
;; ? create a customer object?
;; save it in the session as order/history
;; make sure to empty the cart (leave contact, shipping, and billing)
;; redirect to order confirmation page
;; send out an order confirmation email
;;
;; ? how to test a txn?
;;   ? web-driver?
;;
(defn handle
  [req]
  ;; Validate
  (log/info "PAYMENT" (:params req))
  (-> (tr/localize "confirmation.htm")
      resp/redirect)

  #_ (f/with-valid-form sf/order-payment (:params req)
       (log/info (f/values))
       (-> (tr/localize "payment.htm")
           resp/redirect
           (assoc-in [:session :billing] (select-keys (f/values) [:first-name :last-name :email :phone]))
        ))
  )

;; TODO: handler to finalize payment

