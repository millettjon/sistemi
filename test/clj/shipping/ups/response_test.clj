(ns shipping.ups.response_test
  (:import [java.io ByteArrayInputStream])
  (:require [shipping.ups.response :as rsp]
            [shipping.ups.util :as u]
            [clojure.data.xml :as x]
            [clojure.xml :as xm]
            [clojure.zip :as zip])
  (:use [clojure.test]
        [clojure.data.zip.xml :only (text xml->)]))

(def xml-header "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")

(def sample-xml
  (u/strip-newlines
"<ShipmentConfirmResponse>
  <Response>
    <TransactionReference>
      <CustomerContext>guidlikesubstance</CustomerContext>
      <XpciVersion>1.0001</XpciVersion>
    </TransactionReference>
   <ResponseStatus>0</ResponseStatus>
   <ResponseStatusDescription>success</ResponseStatusDescription>
  </Response>
  <ShipmentCharges>
    <TransportationCharges>
      <MonetaryValue>19.60</MonetaryValue>
    </TransportationCharges>
    <ServiceOptionsCharges>
      <MonetaryValue>3.40</MonetaryValue>
    </ServiceOptionsCharges>
    <TotalCharges>
      <MonetaryValue>23.00</MonetaryValue>
    </TotalCharges>
  </ShipmentCharges>
  <BillingWeight>
    <Weight>36.0</Weight>
  </BillingWeight>
  <ShipmentIdentificationNumber>1Z123X670299567041</ShipmentIdentificationNumber>
  <ShipmentDigest>FSDJHFSDJSHDJK47873487489KFSDJKQSDFSJDFK9
4238093489034KSDFJSDFKLJFDSKFKDJFSDKJFLSDKA923809234893402K
LSDFJKLSDFJDFKSJFSDKLJFDSKLJFSDKLJ49230843920814309KLSDFJF
KLSDJFDKLSJSDFKLJDKFLJDSKLJ092348349223098IJKLFJKLFSDJFKLA
SDJFKAJFSDIUR897348574KJWEHRIQEWU8948348(truncated)</ShipmentDigest>
</ShipmentConfirmResponse>"))

(deftest test-get-shipment-confirm-response
  (let [input (str xml-header sample-xml)
        values (rsp/get-shipment-confirm-response input)]
    (are [x y] (= x y)
      "1Z123X670299567041" (values :tracking_number)
      "0" (values :response_status)
      "success" (values :response_status_description)
      "36.0" (values :billing_weight)
      "19.60" (values :transportation_charges)
      "3.40" (values :service_options_charges)
      "23.00" (values :total_charges)
      ) ) )

(defn get-tag-content
  [values tag]
  (reduce (fn [data value]
            (if (= tag (-> value :tag))
              (conj data (first (-> value :content)))
              data) )
    '() values) )

(deftest test-get-tag-content
  (let [sample (str xml-header sample-xml)
        data (x/parse (java.io.StringReader. sample))
        result (get-tag-content (-> data :content) :ShipmentIdentificationNumber)]

    (is (= '("1Z123X670299567041") result))
    ) )

