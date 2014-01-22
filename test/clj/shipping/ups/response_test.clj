(ns shipping.ups.response_test
  (:import [java.io ByteArrayInputStream])
  (:require [shipping.ups.response :as rsp]
            [shipping.ups.util :as u]
            [shipping.ups.tools :as t]
            [clojure.data.xml :as x]
            [clojure.xml :as xm]
            [clojure.zip :as zip])
  (:use [clojure.test]
        [clojure.data.zip.xml :only (text xml->)]))

(def xml-header "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")

(def error-response-1
  (u/strip-newlines
  "<ShipmentConfirmResponse>
     <Response>
       <ResponseStatusCode>0</ResponseStatusCode>
       <ResponseStatusDescription>Failure</ResponseStatusDescription>
       <Error>
         <ErrorSeverity>Hard</ErrorSeverity>
         <ErrorCode>10001</ErrorCode>
         <ErrorDescription>The XML document is not well formed</ErrorDescription>
       </Error>
    </Response>
  </ShipmentConfirmResponse>") )

(deftest test-get-shipment-confirm-response_error
  (let [raw (str xml-header error-response-1)
        input (xm/parse (t/text-in-bytestream raw))
        data (zip/xml-zip input)
        error (rsp/failure-info data)]

    (is (not (nil? error)))
    (is (= "The XML document is not well formed" (error :error_msg)))
    ) )

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

(def shipment-accept-response-xml
  (u/strip-newlines
"<ShipmentAcceptResponse>
<Response>
<TransactionReference>
<CustomerContext>guidlikesubstance</CustomerContext>
<XpciVersion>1.0001</XpciVersion>
</TransactionReference>
<ResponseStatus>0</ResponseStatus>
<ResponseStatusDescription>success</ResponseStatusDescription>
</Response>
<ShipmentResults>
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
<ShipmentIdentificationNumber>
1Z123X670299567041
</ShipmentIdentificationNumber>
<PackageResults>
  <TrackingNumber>1Z123X670299567041</TrackingNumber>
  <ServiceOptionsCharges>
  <MonetaryValue>1.20</MonetaryValue>
  </ServiceOptionsCharges>
  <LabelImage>
  <LabelImageFormat>
  <Code>GIF</Code>
  </LabelImageFormat>
  <GraphicImage>FSDJHSDJHJ3487EHNE9U8DY9VHRFV89SDFHFSDJHFSDIDFH
  SJKDFSJKDFSJIU9GFIUGJIFDUJG9UKGLDJFDKJDGKJDFKGDJLDFKSJGKDFJDKGFDG9E0ER
  IJGE39IWURE9U9ER0UW9R0UR9WEGU9URE9WGUW90U90GRUG90GERUG9REUGWERGJIO
  JGIODFGUIOFDUGIOFUIGRUE090U9TERUT90RUT9EU90ERUT9ERU9EUER9TUT9R0UTE90R
  U9TERU90RTEU9SDKHGJHGDFU</GraphicImage>
  <HTMLImage>SKJJKLHGIGKHGKJHGFJGFJHDFJGHDDJFHFDJHFJHFJKDHJK
  FDHJFJDFHDFJHJDFHGJDHGDFSHJKFSDHSDFJHFJSDHJKDFHFJKSHDSKJHGFDJSJDFSKSK
  JJKLHGIGKHGKJHGFJGFJHDFJGHDDJFHFDJHFJHFJKDHJKFDHJFJDFHDFJHJDFHGJDHGDF
  SHJKFSDHSDFJHFJSDHJKDFHFJKSHDSKJHGFDJSJDFSKHGJKDS</HTMLImage>
  </LabelImage>
</PackageResults>
<PackageResults>
  <TrackingNumber>1Z123X670292134678</TrackingNumber>
  <AccessorialCharges>
  <MonetaryValue>2.20</MonetaryValue>
  </AccessorialCharges>
  <LabelImage>
  <LabelImageFormat>
  <Code>GIF</Code>
  </LabelImageFormat>
  <GraphicImage>895UIGJ89XCASDVIGFUISDFNKLFSDANUI43UIT34IONSDFK
  HG89GUKGJNGKDJFKDJDGKJDKFSDU089REUTDRKJOEIOUTERIJREIKGRJIGOWEJIEJIEGJ
  GRIOEJGRIGJIODJGFIODFJSIOUDFIOGDFUGDF890ERUTRIOGTJRDIOOGJGIOSDFJGIOJGIOJ
  IOGFUGJIOGU90E8T9TRFIRWEU90WERU90WU90WTU90WUT09WEUTWRJGKSDFJGIOSDFJ
  GOISDFJGIOSJSD</GraphicImage>
  <HTMLImage>JDFSKAATRIOERHIOEGHNVIXCUIFGJMFDGMAN8Y89H54JM
  N1MK345H8SDHFDHGJKGHFDJKGHDGKSFJAH893YTUITNGDFJSGH8935Y5RTHDDFJKHT89
  HTJETHWER8934Y89534KGNDFOJKKH893RYETFIHTRUIOEY89TY34IHDFUIHGRU9T38934UI
  THDIUJTHEQW89RY8WIERHT9RI</HTMLImage>
  </LabelImage>
  </PackageResults>
</ShipmentResults>
</ShipmentAcceptResponse>") )

(deftest test-get-response-packages
  (let [sample (str xml-header shipment-accept-response-xml)
        input (xm/parse (t/text-in-bytestream sample))
        data (zip/xml-zip input)
        result (rsp/get-response-packages data)
        one (second result)
        two (first result)]

    (are [x y] (= x y)
      "1Z123X670299567041" (one :tracking_number)
      "1Z123X670292134678" (two :tracking_number)
      nil (one :accessory_charges)
      "2.20" (two :accessory_charges)
      ) ) )

(deftest test-get-shipment-accept-response
  (let [sample (str xml-header shipment-accept-response-xml)
        result (rsp/get-shipment-accept-response sample)]

    (are [x y] (= x y)
      "1Z123X670299567041" (result :tracking_number)
      2 (count (result :packages))
      "36.0" (result :billing_weight)
      "23.00" (result :total_charges)
      "19.60" (result :transportation_charges)
      "3.40"  (result :service_options_charges)
      ) ) )