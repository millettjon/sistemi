(ns shipping.ups.xml.void_test
  (:import [java.io ByteArrayInputStream])
  (:require [shipping.ups.xml.void :as void]
            [shipping.ups.xml.util :as u]
            [clojure.data.xml :as x]
            [sistemi.config]
            [app.config :as c]
            [clojure.data.xml :as x]
            [clojure.xml :as xm]
            [clojure.zip :as zip])
  (:use [clojure.test]
        [clojure.data.zip.xml :only (text xml->)]))

(def xml x/sexp-as-element)
(def xml-header "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
(def ship_confirm_test_url "https://onlinetools.ups.com/ups.app/xml/Void")

(def void-data
  { ;access_data todo
   :shipment_number "1ZFOO1234"
   :customer_context_id "session-like-data"
   :xpci_version "1.0001"})

(def void-request-sample-xml
  (u/strip-newlines
"<VoidShipmentRequest>
<Request>
<TransactionReference>
<CustomerContext>session-like-data</CustomerContext>
<XpciVersion>1.0001</XpciVersion>
</TransactionReference>
<RequestAction>1</RequestAction>
<RequestOption></RequestOption>
</Request>
<ExpandedVoidShipment>
<ShipmentIdentificationNumber>1ZFOO1234</ShipmentIdentificationNumber>
</ExpandedVoidShipment>
</VoidShipmentRequest>"))

(deftest test-create-void-shipment-request-xml
  (let [data (void/create-void-shipment-request-xml void-data)]
    ;(println (x/emit-str (xml data)))
    (is (= (str xml-header void-request-sample-xml) (x/emit-str (xml data))))
    ) )