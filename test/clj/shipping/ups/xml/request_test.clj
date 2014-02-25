(ns shipping.ups.xml.request_test
  (:require [shipping.ups.xml.request :as sr]
            [shipping.ups.xml.util :as u]
            [clojure.data.xml :as x]
            [shipping.ups.xml.common_test :as ct]
            [shipping.ups.xml.package_test :as pt])
  (:use clojure.test) )


(def xml x/sexp-as-element)

(def xml-header "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")


(def shipment-confirm-data {:txn_reference ct/txn-reference-data
                            :description "Sistemi Test Shipment"
                            :service_attempt_code "5"
                            :documents_only nil
                            :shipper ct/shipper-data
                            :ship_to ct/ship-to-data
                            :ship_service ct/service-data
                            :payment ct/payment-data
                            :packages (list pt/shipping-package-data-1)
                            :label ct/label-spec-data})

(def shipment-confirm-xml
  (u/strip-newlines
"<ShipmentConfirmRequest><Request><RequestAction>ShipConfirm</RequestAction>
<RequestOption>nonvalidate</RequestOption><TransactionReference><CustomerContext>SistemiContextID-XX1122</CustomerContext>
<XpciVersion>1.0001</XpciVersion></TransactionReference></Request><Shipment>
<Shipper><Name>SistemiShipper</Name>
<AttentionName>SistemiFabricator</AttentionName><PhoneNumber>000111222</PhoneNumber><ShipperNumber>123456</ShipperNumber>
<Address><AddressLine1>ZA la Croisette</AddressLine1><City>Clelles en Trièves</City>
<StateProvinceCode></StateProvinceCode><CountryCode>FR</CountryCode><PostalCode>38930</PostalCode>
<ResidentialAddress></ResidentialAddress></Address></Shipper><ShipTo><CompanyName>Sistemi Fans</CompanyName>
<AttentionName>Big Fan</AttentionName><PhoneNumber>123456777</PhoneNumber><Address>
<AddressLine1>123 Sistemi Drive</AddressLine1><City>St. Martin D'Uriage</City>
<StateProvinceCode></StateProvinceCode><CountryCode>FR</CountryCode><PostalCode>12345</PostalCode>
<ResidentialAddress></ResidentialAddress></Address></ShipTo><Service><Code>11</Code>
<Description>UPS Standard</Description></Service><PaymentInformation><Prepaid><BillShipper>
<CreditCard><Type>06</Type><Number>4111111111111111</Number><ExpirationDate>102016</ExpirationDate></CreditCard>
</BillShipper></Prepaid></PaymentInformation><Package><PackagingType><Code>02</Code></PackagingType><Dimensions>
<UnitOfMeasurement><Code>CM</Code></UnitOfMeasurement><Length>22</Length><Width>20</Width><Height>18</Height>
</Dimensions><PackageWeight><UnitOfMeasurement><Code>KGS</Code></UnitOfMeasurement><Weight>14.1</Weight>
</PackageWeight><PackageServiceOptions><InsuredValue><CurrencyCode>EUR</CurrencyCode
><MonetaryValue>50.00</MonetaryValue></InsuredValue><VerbalConfirmation><Name>Eric Romeo</Name>
<PhoneNumber>123456777</PhoneNumber></VerbalConfirmation></PackageServiceOptions></Package><LabelSpecification>
<LabelPrintMethod><Code>GIF</Code></LabelPrintMethod><HTTPUserAgent>Mozilla/4.5</HTTPUserAgent><LabelImageFormat>
<Code>GIF</Code></LabelImageFormat></LabelSpecification></Shipment></ShipmentConfirmRequest>"))

(deftest test-shipment-confirm-request
  (let [data (sr/shipment-confirm-request shipment-confirm-data)]
    (is (= (str xml-header shipment-confirm-xml) (x/emit-str (xml data)) ))
    ) )

(def shipment-accept-xml
  (u/strip-newlines
    "<ShipmentAcceptRequest>
<Request>
<TransactionReference>
<CustomerContext>guidlikesubstance</CustomerContext>
<XpciVersion>1.0001</XpciVersion>
</TransactionReference>
<RequestAction>ShipAccept</RequestAction>
</Request>
<ShipmentDigest>SistemiModerniABC</ShipmentDigest>
</ShipmentAcceptRequest>
") )

(def shipment-accept-data
  {:sistemi_license_no "SistemiLicenseNo"
   :user_id "SistemiModerni"
   :password "SomePassword"
   :customer_context_id "guidlikesubstance"
   :xpci_version "1.0001"
   :request_action "ShipAccept"
   :shipment_digest "SistemiModerniABC"})

(deftest test-shipment-accept-request
  (let [data (sr/shipment-accept-request shipment-accept-data)]
    (is (= (str xml-header shipment-accept-xml) (x/emit-str (xml data)) ))
    ) )