(ns shipping.ups.common_test
  (:require [shipping.ups.common :as c]
            [clojure.data.xml :as xml]
            [shipping.ups.util :as u])
  (:use [clojure.test]) )

(def xml-header "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")

(def access-request-xml
  (u/strip-newlines
"<AccessRequest xml:lang=\"en-US\">
<AccessLicenseNumber>Sistemi123</AccessLicenseNumber>
<UserId>Sistemi</UserId>
<Password>foo</Password>
</AccessRequest>"))

(def access-request-data {:lang_locale "en-US" :license_number "Sistemi123" :user_id "Sistemi" :password "foo"})

(deftest test-access-request-info
  (let [data1 (c/access-request-info access-request-data)]
    (is (= (str xml-header access-request-xml) (xml/emit-str (c/xml data1)) ))
    ) )


(def txn-reference-xml
  (u/strip-newlines
"<TransactionReference>
<CustomerContext>SistemiContextID-XX1122</CustomerContext>
<XpciVersion>1.0001</XpciVersion>
</TransactionReference>"))

(def txn-reference-data {:customer_context_id "SistemiContextID-XX1122" :xpci_version "1.0001"})

(deftest test-transaction-reference-info
  (let [data1 (c/transaction-reference-info txn-reference-data)]
    (is (= (str xml-header txn-reference-xml) (xml/emit-str (c/xml data1)) ))
    ) )

(def address-xml
  (u/strip-newlines
"<Address>
<AddressLine1>123 Sistemi Drive</AddressLine1>
<City>St. Martin D'Uriage</City>
<StateProvinceCode>Grenoble</StateProvinceCode>
<CountryCode>FR</CountryCode>
<PostalCode>12345</PostalCode>
<ResidentialAddress></ResidentialAddress>
</Address>") )

(def address-data {:address1 "123 Sistemi Drive" :city "St. Martin D'Uriage" :state_province "Grenoble"
                   :country_code "FR" :postal "12345"})

(deftest test-address-info
  (let [data1 (c/address-info address-data)]
    (is (= (str xml-header address-xml) (xml/emit-str (c/xml data1)) ))
    ) )

(def shipper-xml
  (u/strip-newlines
"<Shipper>
<Name>Sistemi Client</Name>
<AttentionName>Client</AttentionName>
<PhoneNumber>000111222</PhoneNumber>
<ShipperNumber>123456</ShipperNumber>"
address-xml
"</Shipper>") )

(def shipper-data {:name "Sistemi Client" :attention_name "Client" :phone "000111222"
                   :shipper_number "123456" :address address-data})

(deftest test-shipper-xx-info
  (let [data1 (c/sistemi-shipper-xx-info shipper-data)]
    (is (= (str xml-header shipper-xml) (xml/emit-str (c/xml data1)) ))
    ) )

(def ship-to-xml
  (u/strip-newlines
"<ShipTo>
<CompanyName>Sistemi Fans</CompanyName>
<AttentionName>Big Fan</AttentionName>
<PhoneNumber>123456777</PhoneNumber>"
address-xml
"</ShipTo>") )

(def ship-to-data {:company "Sistemi Fans" :attention_name "Big Fan" :phone "123456777"
                   :address address-data})

(deftest test-ship-to-xx-info
  (let [data1 (c/ship-to-xx-info ship-to-data)]
    (is (= (str xml-header ship-to-xml) (xml/emit-str (c/xml data1)) ))
    ) )

(def service-xml
  (u/strip-newlines
"<Service>
<Code>42</Code>
<Description>The answer to life, etc</Description>
</Service>") )

(def service-data {:code "42" :description "The answer to life, etc"})

(deftest test-shipping-service-info
  (let [data1 (c/shipping-service-info service-data)]
    (is (= (str xml-header service-xml) (xml/emit-str (c/xml data1)) ))
    ) )

(def payment-xml
  (u/strip-newlines 
"<PaymentInformation>
<Prepaid>
<BillShipper>
<CreditCard><Type>06</Type>
<Number>1234123412341234</Number>
<ExpirationDate>102016</ExpirationDate>
</CreditCard>
</BillShipper>
</Prepaid>
</PaymentInformation>"))

(def payment-data {:type "06" :card_number "1234123412341234" :expiration_date "102016"})

(deftest test-payment-info
  (let [data1 (c/payement-info payment-data)]
    (is (= (str xml-header payment-xml) (xml/emit-str (c/xml data1)) ))
    ) )

(def reference-number-xml
  (u/strip-newlines
"<ReferenceNumber>
<Code>02</Code>
<Value>1234567</Value>
</ReferenceNumber>") )

(def reference-number-data {:code "02" :value "1234567"})

(deftest test-reference-number-info
  (let [data1 (c/reference-number-info reference-number-data)]
    (is (= (str xml-header reference-number-xml) (xml/emit-str (c/xml data1)) ))
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

(def label-spec-data {:label_print_code "GIF" :http_user_agent "Mozilla/4.5" :label_image_code "GIF"})

(deftest test-label-spec-info
  (let [data1 (c/label-spec-info label-spec-data)]
    (is (= (str xml-header label-spec-xml) (xml/emit-str (c/xml data1)) ))
    ) )