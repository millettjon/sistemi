(ns ups.shipping.common_test
  (:require [ups.shipping.common :as c]
            [clojure.data.xml :as xml]
            [ups.shipping.util :as u])
  (:use [clojure.test]) )


(def access-request-xml
  (u/strip-newlines
  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><AccessRequest xml:lang=\"en-US\"><AccessLicenseNumber>Sistemi123
</AccessLicenseNumber><UserId>Sistemi</UserId><Password>foo</Password></AccessRequest>"))

(def access-request-data {:lang_locale "en-US" :license_number "Sistemi123" :user_id "Sistemi" :password "foo"})

(deftest test-access-request-info
  (let [data1 (c/access-request-info access-request-data)]
    (is (= access-request-xml (xml/emit-str data1)))
    ) )


(def txn-reference-xml
  (u/strip-newlines "<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransactionReference>
<CustomerContext>SistemiContextID-XX1122</CustomerContext><XpciVersion>1.0001</XpciVersion></TransactionReference>"))

(def txn-reference-data {:customer_context_id "SistemiContextID-XX1122" :xpci_version "1.0001"})

(deftest test-transaction-reference-info
  (let [data1 (c/transaction-reference-info txn-reference-data)]
    (is (= txn-reference-xml (xml/emit-str data1)))
    ) )

(def address-xml
  (u/strip-newlines
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<Address><AddressLine1>123 Sistemi Drive</AddressLine1><City>St. Martin D'Uriage</City>
<StateProvinceCode>Grenoble</StateProvinceCode><CountryCode>FR</CountryCode>
<PostalCode>12345</PostalCode><ResidentialAddress></ResidentialAddress></Address>") )

(def address-data {:address1 "123 Sistemi Drive" :city "St. Martin D'Uriage" :state_province "Grenoble"
                   :country_code "FR" :postal "12345"})

(deftest test-address-info
  (let [data1 (c/address-info address-data)]
    (is (= address-xml (xml/emit-str data1)))
    ) )