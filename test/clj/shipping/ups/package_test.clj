(ns shipping.ups.package_test
  (:require [shipping.ups.package :as p]
            [clojure.data.xml :as x]
            [shipping.ups.util :as u]
            [shipping.ups.common_test :as ct])
  (:use [clojure.test]) )


(def dimension-xml
  (u/strip-newlines 
"<Dimensions>
<UnitOfMeasurement>
<Code>IN</Code>
</UnitOfMeasurement>
<Length>22</Length>
<Width>20</Width>
<Height>18</Height>
</Dimensions>") )

;; todo: expects everything as String and does not accept int, long, double, etc
(def dimension-data {:unit_code "IN" :length "22" :width "20" :height "18"})

(deftest test-dimension-info
  (let [data1 (p/dimension-info dimension-data)]
    (is (= (str ct/xml-header dimension-xml) (x/emit-str (p/xml data1)) ))
    ) )

(def weight-xml
  (u/strip-newlines
"<PackageWeight>
<Weight>14.1</Weight>
</PackageWeight>") )

(def weight-data {:weight "14.1"})

(deftest test-weight-info
  (let [data1 (p/weight-info weight-data)]
    (is (= (str ct/xml-header weight-xml) (x/emit-str (p/xml data1)) ))
    ) )

(def insurance-xml
  (u/strip-newlines
"<InsuredValue>
<CurrencyCode>EUR</CurrencyCode>
<MonetaryValue>50.00</MonetaryValue>
</InsuredValue>") )

(def insurance-data {:currency_code "EUR" :value "50.00"})

(deftest test-insurance-option-info
  (let [data1 (p/insurance-option-info insurance-data)]
    (is (= (str ct/xml-header insurance-xml) (x/emit-str (p/xml data1)) ))
    ) )

(def verbal-conf-xml
  (u/strip-newlines
"<VerbalConfirmation>
<Name>Eric Romeo</Name>
<PhoneNumber>123456777</PhoneNumber>
</VerbalConfirmation>") )

(def verbal-conf-data {:name "Eric Romeo" :phone "123456777"})

(deftest test-verbal-conf-option-info
  (let [data1 (p/verbal-conf-option-info verbal-conf-data)]
    (is (= (str ct/xml-header verbal-conf-xml) (x/emit-str (p/xml data1)) ))
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

(def service-options-data (merge insurance-data verbal-conf-data))
(def service-options-multi (list p/insurance-option-info p/verbal-conf-option-info))

(deftest test-service-option-info
  (let [data1 (p/service-option-info service-options-data service-options-multi)]
    (is (= (str ct/xml-header service-options-xml) (x/emit-str (p/xml data1)) ))
    ) )

(def shipping-package-data-1 {:type_code "02" :dimension_data dimension-data :weight_data weight-data
                              :reference_data ct/reference-number-data :service_data service-options-data
                              :service_options service-options-multi})

(def shipping-package-1
  (u/strip-newlines
"<Package>
<PackagingType>
<Code>02</Code>
</PackagingType>
<Dimensions>
<UnitOfMeasurement>
<Code>IN</Code>
</UnitOfMeasurement>
<Length>22</Length>
<Width>20</Width>
<Height>18</Height>
</Dimensions>
<PackageWeight>
<Weight>14.1</Weight>
</PackageWeight>
<ReferenceNumber>
<Code>02</Code>
<Value>1234567</Value>
</ReferenceNumber>
<PackageServiceOptions>
<InsuredValue>
<CurrencyCode>EUR</CurrencyCode>
<MonetaryValue>50.00</MonetaryValue>
</InsuredValue>
<VerbalConfirmation>
<Name>Eric Romeo</Name>
<PhoneNumber>123456777</PhoneNumber>
</VerbalConfirmation>
</PackageServiceOptions>
</Package>"))

(deftest test-shipping-package-info
  (let [data1 (p/shipping-package-info shipping-package-data-1)]
    (is (= (str ct/xml-header shipping-package-1) (x/emit-str (p/xml data1)) ))
    ) )

(def shipping-package-2
  (u/strip-newlines
"<Package><PackagingType><Code>02</Code></PackagingType><Dimensions><UnitOfMeasurement>
<Code>IN</Code></UnitOfMeasurement><Length>22</Length><Width>20</Width><Height>18</Height>
</Dimensions><PackageWeight><Weight>14.1</Weight></PackageWeight><ReferenceNumber><Code>02</Code>
<Value>1234567</Value></ReferenceNumber><PackageServiceOptions><InsuredValue>
<CurrencyCode>EUR</CurrencyCode><MonetaryValue>50.00</MonetaryValue></InsuredValue>
<VerbalConfirmation><Name>Eric Romeo</Name><PhoneNumber>123456777</PhoneNumber>
</VerbalConfirmation></PackageServiceOptions></Package>
<Package><PackagingType>
<Code>02</Code></PackagingType><Dimensions><UnitOfMeasurement><Code>IN</Code></UnitOfMeasurement>
<Length>22</Length><Width>20</Width><Height>18</Height></Dimensions><PackageWeight><Weight>14.1</Weight>
</PackageWeight><ReferenceNumber><Code>02</Code><Value>1234567</Value></ReferenceNumber>
<PackageServiceOptions><InsuredValue><CurrencyCode>EUR</CurrencyCode><MonetaryValue>50.00</MonetaryValue>
</InsuredValue><VerbalConfirmation><Name>Eric Romeo</Name><PhoneNumber>123456777</PhoneNumber>
</VerbalConfirmation></PackageServiceOptions></Package>"))

(deftest test-shipping-packages-info
  (let [data1 (p/shipping-packages-info (list shipping-package-data-1 shipping-package-data-1))]
    (is (= (str ct/xml-header shipping-package-2) (x/emit-str (map p/xml data1)) ))
    ) )