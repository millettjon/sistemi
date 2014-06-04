(ns ship-test
  (:require ship
            [frinj.ops :as f])
  (:use clojure.test))

(deftest fj->ups
  (is (= "24.0"
         (ship/fj->ups (f/fj 239.878 :mm) :cm 1))))

(deftest box->ups
  (let [box [{:mass #frinj.core.fjv{:v 8.264999999999999, :u {"kg" 1, "m" 0}}
              :z {:v 0.019, :u {"m" 1}}
              :x {:v 2N, :u {"m" 1}}
              :y {:v 3/10, :u {"m" 1}}}
             {:mass #frinj.core.fjv{:v 8.264999999999999, :u {"kg" 1, "m" 0}}
              :z {:v 0.019, :u {"m" 1}}
              :x {:v 2N, :u {"m" 1}}
              :y {:v 3/10, :u {"m" 1}}}]]
    (ship/box->ups box)))

(deftest address->ups
  (let [shipping {:region "MI"
                  :code "49091"
                  :city "Sturgis"
                  ;;:address2 ""
                  :address1 "23950 Butternut"
                  :name "Jonathan Millett"
                  :country "USA"}
        contact {:phone "12345"}]
       (ship/address->ups shipping contact)))

(deftest ^:integration estimate*
  (let [{:keys [items] :as order}
        {:id "a5c3"
         :items {1 {:depth 30
                    :width 240
                    :height 240
                    :quantity 1
                    :type :bookcase}}
         :shipping {:name "Jonathan Millett"
                    :address1 "130 route de la combette"
                    ;;:address2 ""
                    :city "St. Martin d'Uriage"
                    :code "38410"
                    :country "FR"}}]
    ;;     (pprint (mapcat #(->> % second pack/parts pack/make-boxes) items))
    ;;     (pprint (mapcat #(->> % second pack/parts pack/make-boxes (map box->ups)) items))
    (ship/estimate order)))

;; Response with no errors.
#_ "<?xml version=\"1.0\"?>\n
<ShipmentConfirmResponse>
  <Response>
    <TransactionReference><CustomerContext>a5c3</CustomerContext></TransactionReference>
    <ResponseStatusCode>1</ResponseStatusCode>
    <ResponseStatusDescription>Success</ResponseStatusDescription>
  </Response>
  <ShipmentCharges>
    <TransportationCharges><CurrencyCode>EUR</CurrencyCode><MonetaryValue>50.56</MonetaryValue></TransportationCharges>
    <ServiceOptionsCharges><CurrencyCode>EUR</CurrencyCode><MonetaryValue>0.00</MonetaryValue></ServiceOptionsCharges>
    <TotalCharges><CurrencyCode>EUR</CurrencyCode><MonetaryValue>50.56</MonetaryValue></TotalCharges>
  </ShipmentCharges>
  <BillingWeight><UnitOfMeasurement><Code>KGS</Code></UnitOfMeasurement><Weight>33.0</Weight></BillingWeight>
  <ShipmentIdentificationNumber>1ZAY34136896927158</ShipmentIdentificationNumber>
  <ShipmentDigest>...</ShipmentDigest>
</ShipmentConfirmResponse>"

;; Response with large package errors.
;; TODO: The Response parser doesn't handle this correctly when there is more than one error.
#_ "<?xml version=\"1.0\"?>\n
<ShipmentConfirmResponse>
  <Response>
    <TransactionReference><CustomerContext>a5c3</CustomerContext></TransactionReference>
    <ResponseStatusCode>1</ResponseStatusCode>
    <ResponseStatusDescription>Success</ResponseStatusDescription>
    <Error>
      <ErrorSeverity>Warning</ErrorSeverity>
      <ErrorCode>129070</ErrorCode>
      <ErrorDescription>Large Package indicator has automatically been set on Package 1.\nLarge Package Surcharge has been added to Package 1.</ErrorDescription>
    </Error>
    <Error>
      <ErrorSeverity>Warning</ErrorSeverity>
      <ErrorCode>120014</ErrorCode>
      <ErrorDescription>A Large Package Minimum Surcharge has been applied to Package 1</ErrorDescription>
    </Error>
  </Response>
  <ShipmentCharges>
    <TransportationCharges><CurrencyCode>EUR</CurrencyCode><MonetaryValue>222.21</MonetaryValue></TransportationCharges>
    <ServiceOptionsCharges><CurrencyCode>EUR</CurrencyCode><MonetaryValue>0.00</MonetaryValue></ServiceOptionsCharges>
    <TotalCharges><CurrencyCode>EUR</CurrencyCode><MonetaryValue>222.21</MonetaryValue></TotalCharges>
  </ShipmentCharges>
  <BillingWeight><UnitOfMeasurement><Code>KGS</Code></UnitOfMeasurement><Weight>130.0</Weight></BillingWeight>
  <ShipmentIdentificationNumber>1ZAY34136899449197</ShipmentIdentificationNumber>
  <ShipmentDigest>...</ShipmentDigest>
</ShipmentConfirmResponse>"

;; BUG: The customer_context_id is nil.
;;   ? Does Dave have a generic way to parse the Response element?
;; BUG: The response_status is nil.
;; BUG: xpci_version is not provided.
;; BUG: response parser logs a lazy seq if there is more than one "Error"
;; BUG: response parser should user ErrorSeverity as warning level.

;; TODO: ? Is there any other xml that Dave is not parsing in the reponse?
;; TODO: Check the response for errors.
;; TODO: Log how long the request takes.
;; TODO: How to handle long requests:
;;   - timeouts
;;   - async updates
;;   - get estimate from direct formula
;; TODO: Redesign as a single page app. Use om.
