(ns shipping.ups.response_test
  (:require [shipping.ups.response :as rsp]
            [shipping.ups.util :as u]
            [clojure.data.xml :as x])
  (:use [clojure.test]))

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

(deftest test-xml-parse
  (let [sample (str xml-header sample-xml)
        data (x/parse (java.io.StringReader. sample))]
    (println data)
    (println "")
    ;(println (first (-> (nth (-> (first (-> data :content)) :content) 1) :content)) )
    ;; Success
    ;(println (first (-> (nth (-> (first (-> data :content)) :content) 2) :content)) )
    ) )

(deftest test-get-content-for-tag
  (let [sample (str xml-header sample-xml)
        data (x/parse (java.io.StringReader. sample))
        results (rsp/get-content-for-tag data :ShipmentConfirmResponse)]

    (println results)
    ) )

(deftest test-get-response-status
  (let [sample (str xml-header sample-xml)
        response (u/parse-xml-into-structure sample)
        status (rsp/get-response-status response)]
    (println status)
    ) )