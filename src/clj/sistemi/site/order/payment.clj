;; See: https://stripe.com/docs/tutorials/charges
;; See: https://stripe.com/docs/testing
;; 
;; - Don't bother creating a customer object for now as we don't have
;;   one ourselves (and hence no where to store the stripe customer
;;   id).

(ns sistemi.site.order.payment
  "Receives stripe token and charges credit card."
  (:require [taoensso.timbre :as log]
            [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [www.session :as sess]
            [sistemi.form :as sf]
            [sistemi.order :as order]
            [www.cart :as cart]
            [www.form :as f]

            [clj-stripe.common :as common]
            [clj-stripe.customers :as customers]
            [clj-stripe.charges :as charges]

            [app.config :as c]
            [clojure.data.json :as json]
            ))

(defn get-key
  "Returns the stripe secret key."
  []
  (c/conf :stripe :secret-key))

(defn ->cents
  "Converts a currency amount to cents."
  [amount]
  (-> amount
      (* 100)
      .longValue)) ; assumes BigDecimail (from frinj)

(defn ->amount
  "Converts a frinj currency amount to a stripe amount."
  [{:keys [v u]}]
  {"amount" (->cents v) ; stripe requires cents
   "currency" (-> u ffirst name)})

(defn cart-total
  "Returns the total amount from the cart."
  [req]
  (-> req :session :cart :price :total))

(defn cart-description
  "Returns description of cart contents to show alongside order in stripe web ui."
  [req]
  (let [items (-> req :cart :items)]
    (->> 
     (for [{:keys [type quantity]} items] (str (name type) "(" quantity ")"))
     (interpose ", ")
     (apply str))))

(defn charge-card
  "Charges a credit card for the specified amount."
  [token amount description]
  (common/with-token (get-key)
    (common/execute
     (charges/create-charge
      {"card" token
       "description" description}
      (->amount amount)))))

;; TODO: Save the order in datomic.
;; TODO: Send the confirmation email.
(defn complete-order
  ""
  [session response]
  (order/create session))

;; TODO: handle stripe error
;;       - stay on page
;;       - display error to user
;;       - log error
;; TODO: handle success
;;       - move cart to order history
;;       - redirect to order confirmation page
;;       - send order confirmation email
;;
;; TODO: handle request error (e.g., by testing with no network connection)
;;       - stay on page
;;       - display error to user
;;       - log error
;; Stripe.js error message if network is down:
;; An unexpected error has occurred submitting your credit card to our
;; secure credit card processor. This may be due to network
;; connectivity issues, so you should try again (you won't be charged
;; twice). If this problem persists, please let us know!

;; {:error {:type "invalid_request_error", :message "Invalid integer: 310.00", :param "amount"}}
;; TODO: format 310.00 as integer


;; SAMPLE ERROR RESPONSE:
;; {:error {:message "Your card was declined.", :type "card_error", :code "card_declined", :charge "ch_103e6h27flN4SizkJcWhgLDM"}}

;; SAMPLE SUCCESSFUL RESPONSE
;; {:invoice nil, :refunded false, :metadata {}, :refunds [], :balance_transaction "txn_103e6r27flN4SizkR7bIjmPQ", :currency "eur", :card {:country "US", :customer nil, :exp_month 5, :last4 "4242", :address_zip_check "pass", :name "Jonathan Millett", :address_line2 "", :fingerprint "RYdQB7XGFSIvdwRm", :cvc_check "pass", :address_line1 "23950 Butternut", :object "card", :address_city "Sturgis", :address_zip "49091", :address_state "MI", :address_line1_check "pass", :type "Visa", :exp_year 2015, :address_country "USA", :id "card_103e6r27flN4SizkUYMbNk9C"}, :customer nil, :captured true, :dispute nil, :livemode false, :amount 15700, :object "charge", :failure_code nil, :created 1394539561, :amount_refunded 0, :failure_message nil, :paid true, :id "ch_103e6r27flN4SizkQw8ud60j", :description ""}

(defn handle
  [{:keys [body session]:as req}]

  (let [stripe-token (-> body :stripe-token)]
    ;; Save the event for posterity.
    (log/info {:event :payment/request :stripe-token stripe-token})

    ;; Attempt to charge the card.
    (let [{:keys [object failure_code error] :as response} (charge-card
                                                            stripe-token
                                                            (cart-total req)
                                                            (cart-description req))

          ;; Log the stripe response.
          _ (log/info (assoc response :event :payment/response))

          success? (and (= object "charge") (nil? failure_code))

          ;; Build the response data map.
          response-data (cond
                         ;; successful charge
                         success? (let [order-id (complete-order session response)]
                                      {:code 0 :message "success" :location (tr/localize "confirmation.htm" :query {:id order-id})})

                         ;; stripe error -> display to user
                         ;; TODO: Translate error messages.
                         error {:code 1 :message (:message error)}

                         ;; network error -> display to user
                         (-> response class (isa? Exception)) {:code 2 :message "network error"}

                         ;; unknown response
                         :else  {:code 2 :message "unknown error"})
          json-response  (-> response-data
                             json/json-str
                             resp/response
                             (resp/content-type "application/json"))]
      (if success?
        (assoc json-response :session (dissoc session :cart))
        json-response))))
