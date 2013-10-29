(ns ups.shipping.package_test
  (:require [ups.shipping.package :as p]
            [clojure.data.xml :as xml]
            [ups.shipping.util :as u]
            [ups.shipping.common_test :as ct])
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
    (is (= (str ct/xml-header dimension-xml) (xml/emit-str data1)) )
    ) )

(def weight-xml
  (u/strip-newlines
"<PackageWeight>
<Weight>14.1</Weight>
</PackageWeight>") )

(def weight-data {:weight "14.1"})

(deftest test-weight-info
  (let [data1 (p/weight-info weight-data)]
    (is (= (str ct/xml-header weight-xml) (xml/emit-str data1)))
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
    (is (= (str ct/xml-header insurance-xml) (xml/emit-str data1)))
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
    (is (= (str ct/xml-header verbal-conf-xml) (xml/emit-str data1)))
    ) )

(def service-options-data (merge insurance-data verbal-conf-data))

(deftest test-service-option-info
  (let [data1 (p/service-option-info service-options-data
                (list p/insurance-option-info p/verbal-conf-option-info))]
    ;(println (realized? data1) (count data1) (count (comp data1)) )
    (println (map xml/emit-str data1))
    ;(is (= ("" (apply str map xml/emit-str data1))) )
    ) )
