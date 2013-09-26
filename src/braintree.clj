(ns braintree
  (:use app.config)
  (:import [com.braintreegateway BraintreeGateway Environment TransactionRequest Transaction$Type]))

;; TODO: handle sandbox and production environments
;; TODO: define api
;; TODO: create unit tests
;; TODO: translate error messages

;; https://www.braintreepayments.com/docs/java/transactions/overview
;; TODO: test workflow for authorization and later capture
;; TODO: implement settle
;; TODO: implement void
;; TODO: implement refund


(app.config/conf :braintree)

#_ (def conf
  {:merchant-id "dwpkd9fpjtf5n9hm"
   :public-key  "fycyy4m5w9kwkghf"
   :private-key "28098df1797197e2bf2829516be7a0da"
   :client-key  "MIIBCgKCAQEAsv3h63OdRY+VNks5YPsLvZxVV/0VUOP61SLySqYTDEQ5NQwmLhXbRFXEIcZrt+rWyliDCSJAIVrDa0KUEUDGepPrRB0vL+6E1lWPvDuXLXqwA575hnspcT+THKtlf+uqhFkcZyWJU6/u8TLKAc7E4u0mZQUIGN4/aaHKquAb3/Dj1QWI3Q1f5+YcaC+cP5Wt43r6E3xltnX8Ydj8W9ItBQVkcw5V109yT1nKFh30oLNblygbF8EpBzhpidKEycMfss+TaNm7nK0gwl7D1uBbtE1WANkOTNWs8/s4pY+jOpjp/Q0/D9zng8Fa4UdGXoTegY0Hs5/6SsDtqrnKwLU22wIDAQAB"})

#_ (def gateway (BraintreeGateway. Environment/SANDBOX (:merchant-id conf), (:public-key conf), (:private-key conf)))

;; Create transaction request.
(def result
  (let [request
        ;; define transaction request
        (.. (TransactionRequest.)
            ;;(amount (BigDecimal. "299.00")) ;; success
            ;;(amount (BigDecimal. "2000.00"))  ;; txn declined
            (amount (BigDecimal. "2500.00")) ;; ?
            (creditCard)
            (number "4111111111111111") ;; works
            ;;(number "4111111111111111x") ;; other error
            (expirationMonth "05")
            (expirationYear "2009")
            (done))

        ;; DO SALE REQUEST
        result (.. gateway transaction (sale request))

        ;; handle errors
        ]
    result
    ))

;; success
(.isSuccess result)
(let [txn (.getTarget result)]
  (.getId txn))

(.getTransaction result) ; nil if other error, txn if processing error

;; processing error
;; To avoid issues with card fraud, typically merchants will show the
;; user a relatively generic message (e.g. “There was a problem
;; processing your credit card, please double check your data and try
;; again.”) for a card decline, but log the processor response code in
;; your system in case a customer contacts you asking why their card was
;; declined.
(do
  (prn "-----------------------------")
  (prn "message" (.getMessage result))
  (let [txn (.getTransaction result)]
    (prn "status" (.getStatus txn))
    (prn "code" (.getProcessorResponseCode txn))
    (prn "text" (.getProcessorResponseText txn))
    ))

;; other error
;; ? how to simulate another error?
(.getMessage result)
(.getErrors result)
(doseq [error (.. result getErrors getAllDeepValidationErrors)]
  (prn "-----------------------------")
  (prn "attribute" (.getAttribute error))
  (prn "code" (.getCode error))
  (prn "message" (.getMessage error))
  )
