(ns shipping.ups.xml.common_test
  (:require [shipping.ups.xml.common :as c]
            [clojure.data.xml :as x]
            [shipping.ups.xml.util :as u])
  (:use [clojure.test]) )

(deftest test-handle-optional
  (let [one (c/handle-optional "a" "")
        two (c/handle-optional nil "")]
    (are [x y] (= x y)
      "a" one
      "" two
      ) ) )


(def xml x/sexp-as-element)

;; Known error cases
; 1) Setting StateRegionCode for Europe
; 2) Invalid credit card number (use sample below)
; 3) Invalid RequestOption

;; Sample working request data required
(def access-request-data {:lang_locale "en-US" :license_number "Sistemi123" :user_id "Sistemi" :password "foo"})
(def txn-reference-data {:customer_context_id "SistemiContextID-XX1122" :xpci_version "1.0001"})
(def payment-data {:type "06" :card_number "4111111111111111" :expiration_date "102016"})
(def label-spec-data {:label_print_code "GIF" :http_user_agent "Mozilla/4.5" :label_image_code "GIF"})
(def service-data {:code "03" :description "Ground"})
(def reference-number-data {:code "02" :value "1234567"})
(def service-attempt-data {:description "Sistemi Test Data" :return_service_code "5" :documents_only ""})

(def address-data {:address1 "123 Sistemi Drive" :city "St. Martin D'Uriage" :state_province ""
                   :country_code "FR" :postal "12345"})

;; Fabricator UPS account information
(def shipper-data {:user_id "Sistemi" :attention_name "SistemiFabricator" :phone "000111222"
                   :shipper_number "123456" :address address-data})

(def ship-to-data {:company "Sistemi Fans" :attention_name "Big Fan" :phone "123456777"
                   :address address-data})


(def xml-header "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")

(def access-request-xml
  (u/strip-newlines
"<AccessRequest xml:lang=\"en-US\">
<AccessLicenseNumber>Sistemi123</AccessLicenseNumber>
<UserId>Sistemi</UserId>
<Password>foo</Password>
</AccessRequest>"))

(deftest test-access-request-info
  (let [data1 (c/access-request-info access-request-data)]
    (is (= (str xml-header access-request-xml) (x/emit-str (xml data1)) ))
    ) )


(def txn-reference-xml
  (u/strip-newlines
"<TransactionReference>
<CustomerContext>SistemiContextID-XX1122</CustomerContext>
<XpciVersion>1.0001</XpciVersion>
</TransactionReference>"))

(deftest test-transaction-reference-info
  (let [data1 (c/transaction-reference-info txn-reference-data)]
    (is (= (str xml-header txn-reference-xml) (x/emit-str (xml data1)) ))
    ) )


(def address-xml
  (u/strip-newlines
"<Address>
<AddressLine1>123 Sistemi Drive</AddressLine1>
<City>St. Martin D'Uriage</City>
<StateProvinceCode></StateProvinceCode>
<CountryCode>FR</CountryCode>
<PostalCode>12345</PostalCode>
<ResidentialAddress></ResidentialAddress>
</Address>") )

(deftest test-address-info
  (let [data1 (c/address-info address-data)]
    (is (= (str xml-header address-xml) (x/emit-str (xml data1)) ))
    ) )

(def shipper-xml
  (u/strip-newlines
"<Shipper>
<Name>Sistemi</Name>
<AttentionName>SistemiFabricator</AttentionName>
<PhoneNumber>000111222</PhoneNumber>
<ShipperNumber>123456</ShipperNumber>"
address-xml
"</Shipper>") )


(deftest test-shipper-info
  (let [data1 (c/sistemi-shipper-info shipper-data)]
    (is (= (str xml-header shipper-xml) (x/emit-str (xml data1)) ))
    ) )

(def ship-to-xml
  (u/strip-newlines
"<ShipTo>
<CompanyName>Sistemi Fans</CompanyName>
<AttentionName>Big Fan</AttentionName>
<PhoneNumber>123456777</PhoneNumber>"
address-xml
"</ShipTo>") )

(deftest test-ship-to-info
  (let [data1 (c/ship-to-info ship-to-data)]
    (is (= (str xml-header ship-to-xml) (x/emit-str (xml data1)) ))
    ) )

(def service-xml
  (u/strip-newlines
"<Service>
<Code>03</Code>
<Description>Ground</Description>
</Service>") )


(deftest test-shipping-service-info
  (let [data1 (c/shipping-service-info service-data)]
    (is (= (str xml-header service-xml) (x/emit-str (xml data1)) ))
    ) )

(def payment-xml
  (u/strip-newlines 
"<PaymentInformation>
<Prepaid>
<BillShipper>
<CreditCard><Type>06</Type>
<Number>4111111111111111</Number>
<ExpirationDate>102016</ExpirationDate>
</CreditCard>
</BillShipper>
</Prepaid>
</PaymentInformation>"))


(deftest test-payment-info
  (let [data1 (c/payement-info payment-data)]
    (is (= (str xml-header payment-xml) (x/emit-str (xml data1)) ))
    ) )

(def reference-number-xml
  (u/strip-newlines
"<ReferenceNumber>
<Code>02</Code>
<Value>1234567</Value>
</ReferenceNumber>") )

(deftest test-reference-number-info
  (let [data1 (c/reference-number-info reference-number-data)]
    (is (= (str xml-header reference-number-xml) (x/emit-str (xml data1)) ))
    ) )

(def label-spec-xml
  (u/strip-newlines
"<LabelSpecification>
<LabelPrintMethod>
<Code>GIF</Code>
</LabelPrintMethod>
<HTTPUserAgent>Mozilla/4.5</HTTPUserAgent>
<LabelImageFormat>
<Code>GIF</Code>
</LabelImageFormat>
</LabelSpecification>"))

(deftest test-label-spec-info
  (let [data1 (c/label-spec-info label-spec-data)]
    (is (= (str xml-header label-spec-xml) (x/emit-str (xml data1)) ))
    ) )