(ns shipping.ups.xml.modules_test
  (:require [clojure.data.xml :as x]
            [shipping.ups.xml.modules :as m]
            [shipping.ups.xml.util :as u])
  (:use [clojure.test]) )

(def xml x/sexp-as-element)

(deftest test-handle-optional
  (let [one (m/handle-optional "a" "")
        two (m/handle-optional nil "")]
    (are [x y] (= x y)
      "a" one
      "" two
      ) ) )

;; Known error cases
; 1) Setting StateRegionCode for Europe
; 2) Invalid credit card number (use sample below)
; 3) Invalid RequestOption

;; todo: refactor to use ship_data_basic
;; Sample working request data required
(def access-request-data {:lang_locale "en-US" :license_number "Sistemi123" :user_id "Sistemi" :password "foo"})
(def txn-reference-data {:customer_context_id "SistemiContextID-XX1122" :xpci_version "1.0001"})
(def payment-data {:type "06" :card_number "4111111111111111" :expiration_date "102016"})
(def label-spec-data {:label_print_code "GIF" :http_user_agent "Mozilla/4.5" :label_image_code "GIF"})
(def service-data {:code "11" :description "UPS Standard"})
(def reference-number-data {:code "02" :value "1234567"})
(def service-attempt-data {:description "Sistemi Test Data" :return_service_code "5" :documents_only ""})

;; Fabricator UPS account information
(def shipper-address-data {:address1 "ZA la Croisette" :city "Clelles en Trièves" :state_province ""
                           :country_code "FR" :postal "38930"})

(def receiver-address-data {:address1 "130 route de la combette" :city "St. Martin d'Uriage" :state_province ""
                            :country_code "FR" :postal "38410"})

; Phone numbers are 10 alpha-numeric (Europe 2.2.2.2.2  (
(def shipper-data {:user_id "SistemiShipper" :attention_name "SistemiFabricator" :phone "0423456789"
                   :shipper_number "123456" :address shipper-address-data})

; Phone numbers are 10 alpha-numeric
(def receiver-data {:user_id "SistemiReceiver" :attention_name "SistemiCustomer" :phone "0412345678"
                    :shipper_number "123456" :company "Sistemi" :address receiver-address-data})

;(def shipping-package-data-1 {:type_code "02" :dimension_data dimension-data :weight_data weight-data
;                              :service_data service-options-data ;:reference_data reference-number-data
;                              :service_options service-options-none})


(def xml-header "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")

(def access-request-xml
  (u/strip-newlines
"<AccessRequest xml:lang=\"en-US\">
<AccessLicenseNumber>Sistemi123</AccessLicenseNumber>
<UserId>Sistemi</UserId>
<Password>foo</Password>
</AccessRequest>") )

(deftest test-access-request-xml
  (let [data1 (m/access-request-xml access-request-data)]
    (is (= (str xml-header access-request-xml) (x/emit-str (xml data1)) ))
    ) )


(def txn-reference-xml
  (u/strip-newlines
"<TransactionReference>
<CustomerContext>SistemiContextID-XX1122</CustomerContext>
<XpciVersion>1.0001</XpciVersion>
</TransactionReference>"))

(deftest test-transaction-reference-info
  (let [data1 (m/transaction-reference-info txn-reference-data)]
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

(def address-fabricator-1-xml
  (u/strip-newlines
"<Address>
<AddressLine1>ZA la Croisette</AddressLine1>
<City>Clelles en Trièves</City>
<StateProvinceCode></StateProvinceCode>
<CountryCode>FR</CountryCode>
<PostalCode>38930</PostalCode>
<ResidentialAddress></ResidentialAddress>
</Address>" ) )

(def address-sistemi-hq-xml
  (u/strip-newlines
"<Address>
<AddressLine1>130 route de la combette</AddressLine1>
<City>St. Martin D'Uriage</City>
<StateProvinceCode></StateProvinceCode>
<CountryCode>FR</CountryCode>
<PostalCode38410></PostalCode>
<ResidentialAddress></ResidentialAddress>
</Address>" ) )

(def address-data {:address1 "123 Sistemi Drive" :city "St. Martin D'Uriage" :state_province ""
                   :country_code "FR" :postal "12345"})

(deftest test-address-info
  (let [data1 (m/address-info address-data)]
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

(def shipper-data-test {:user_id "Sistemi" :attention_name "SistemiFabricator" :phone "000111222"
                        :shipper_number "123456" :address address-data})

(deftest test-shipper-info
  (let [data1 (m/sistemi-shipper-info shipper-data-test)]
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


(def ship-to-data {:company "Sistemi Fans" :attention_name "Big Fan" :phone "123456777"
                   :address address-data})

(deftest test-ship-to-info
  (let [data1 (m/ship-to-info ship-to-data)]
    (is (= (str xml-header ship-to-xml) (x/emit-str (xml data1)) ))
    ) )

(def service-xml
  (u/strip-newlines
"<Service>
<Code>11</Code>
<Description>UPS Standard</Description>
</Service>") )


(deftest test-shipping-service-info
  (let [data1 (m/shipping-service-info service-data)]
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
  (let [data1 (m/payement-info payment-data)]
    (is (= (str xml-header payment-xml) (x/emit-str (xml data1)) ))
    ) )

(def reference-number-xml
  (u/strip-newlines
"<ReferenceNumber>
<Code>02</Code>
<Value>1234567</Value>
</ReferenceNumber>") )

(deftest test-reference-number-info
  (let [data1 (m/reference-number-info reference-number-data)]
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
  (let [data1 (m/label-spec-info label-spec-data)]
    (is (= (str xml-header label-spec-xml) (x/emit-str (xml data1)) ))
    ) )

(def dimension-xml
  (u/strip-newlines
"<Dimensions>
<UnitOfMeasurement>
<Code>CM</Code>
</UnitOfMeasurement>
<Length>22</Length>
<Width>20</Width>
<Height>18</Height>
</Dimensions>") )

;; todo: expects everything as String and does not accept int, long, double, etc
(def dimension-data {:unit_code "CM" :length "22" :width "20" :height "18"})

(deftest test-dimension-info
  (let [data1 (m/dimension-info dimension-data)]
    (is (= (str xml-header dimension-xml) (x/emit-str (xml data1)) ))
    ) )

(def weight-xml
  (u/strip-newlines
"<PackageWeight>
<UnitOfMeasurement>
<Code>KGS</Code>
</UnitOfMeasurement>
<Weight>14.1</Weight>
</PackageWeight>") )

(def weight-data {:weight "14.1" :unit_code "KGS"})

(deftest test-weight-info
  (let [data1 (m/weight-info weight-data)]
    (is (= (str xml-header weight-xml) (x/emit-str (xml data1)) ))
    ) )

(def insurance-xml
  (u/strip-newlines
"<InsuredValue>
<CurrencyCode>EUR</CurrencyCode>
<MonetaryValue>50.00</MonetaryValue>
</InsuredValue>") )

(def insurance-data {:currency_code "EUR" :value "50.00"})

(deftest test-insurance-option-info
  (let [data1 (m/insurance-option-info insurance-data)]
    (is (= (str xml-header insurance-xml) (x/emit-str (xml data1)) ))
    ) )

(def verbal-conf-xml
  (u/strip-newlines
"<VerbalConfirmation>
<Name>Eric Romeo</Name>
<PhoneNumber>123456777</PhoneNumber>
</VerbalConfirmation>") )

(def verbal-conf-data {:name "Eric Romeo" :phone "123456777"})

(deftest test-verbal-conf-option-info
  (let [data1 (m/verbal-conf-option-info verbal-conf-data)]
    (is (= (str xml-header verbal-conf-xml) (x/emit-str (xml data1)) ))
    ) )

(def service-options-xml
  (u/strip-newlines
"<PackageServiceOptions>
<InsuredValue>
<CurrencyCode>EUR</CurrencyCode>
<MonetaryValue>50.00</MonetaryValue>
</InsuredValue>
<VerbalConfirmation>
<Name>Eric Romeo</Name>
<PhoneNumber>123456777</PhoneNumber>
</VerbalConfirmation>
</PackageServiceOptions>"))

(def service-options-insurance2-xml
  (u/strip-newlines
"<PackageServiceOptions>
<DeclaredValue>
<Type>
<Code>01</Code>
</Type>
<CurrencyCode>EUR</CurrencyCode>
<MonetaryValue>50.00</MonetaryValue>
</DeclaredValue>
</PackageServiceOptions>"))

(def service-options-data (merge insurance-data verbal-conf-data))
;; for simple test case, no service options work.
(def service-options-none '())
(def service-options-multi
  {:insurance {:currency_code "EUR" :value "50.00"}
   :verbal_conf {:name "Eric Romeo" :phone "123456777"}} )

(deftest test-service-option-info
  (let [data1 (m/package-service-option-info service-options-multi)]
    (is (= (str xml-header service-options-insurance2-xml) (x/emit-str (xml data1)) ))
    ) )

(def shipping-package-data-1 {:type_code "02" :dimension_data dimension-data :weight_data weight-data
                              :service_data service-options-data ;:reference_data reference-number-data
                              :service_options service-options-none})

(def shipping-package-1
  (u/strip-newlines
"<Package>
<PackagingType>
<Code>02</Code>
</PackagingType>
<Dimensions>
<UnitOfMeasurement>
<Code>CM</Code>
</UnitOfMeasurement>
<Length>22</Length>
<Width>20</Width>
<Height>18</Height>
</Dimensions>
<PackageWeight>
<UnitOfMeasurement>
<Code>KGS</Code>
</UnitOfMeasurement>
<Weight>14.1</Weight>
</PackageWeight>
<PackageServiceOptions>
</PackageServiceOptions>
</Package>"))

(deftest test-shipping-package-info
  (let [data1 (m/shipping-package-info shipping-package-data-1)]
    (is (= (str xml-header shipping-package-1) (x/emit-str (xml data1)) ))
    ) )

;; Had to remove package service options (for now)
;; <InsuredValue><CurrencyCode>EUR</CurrencyCode><MonetaryValue>50.00</MonetaryValue>
;;</InsuredValue><VerbalConfirmation><Name>Eric Romeo</Name><PhoneNumber>123456777</PhoneNumber>
;;</VerbalConfirmation

(def shipping-package-2
  (u/strip-newlines
"<Package><PackagingType><Code>02</Code></PackagingType><Dimensions><UnitOfMeasurement>
<Code>CM</Code></UnitOfMeasurement><Length>22</Length><Width>20</Width><Height>18</Height>
</Dimensions><PackageWeight><UnitOfMeasurement>
<Code>KGS</Code></UnitOfMeasurement><Weight>14.1</Weight></PackageWeight><PackageServiceOptions>
</PackageServiceOptions></Package><Package><PackagingType>
<Code>02</Code></PackagingType><Dimensions><UnitOfMeasurement><Code>CM</Code></UnitOfMeasurement>
<Length>22</Length><Width>20</Width><Height>18</Height></Dimensions><PackageWeight><UnitOfMeasurement>
<Code>KGS</Code></UnitOfMeasurement><Weight>14.1</Weight>
</PackageWeight><PackageServiceOptions></PackageServiceOptions></Package>"))

(deftest test-shipping-packages-info
  (let [data1 (m/shipping-packages-info (list shipping-package-data-1 shipping-package-data-1))]
    (is (= (str xml-header shipping-package-2) (x/emit-str (map xml data1)) ))
    ) )