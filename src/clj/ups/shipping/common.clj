(ns ups.shipping.common
  (:require [clojure.data.xml :as xml]) )

;; Used to sign into UPS as a Customer
;;
;; <?xml version="1.0" ?>
;; <AccessRequest xml:lang='en-US'>
;;   <AccessLicenseNumber>YOURACCESSLICENSENUMBER</AccessLicenseNumber>
;;   <UserId>YOURUSERID</UserId>
;;   <Password>YOURPASSWORD</Password>
;; </AccessRequest>
;; todo: pull from secure location?
(def access-request-keys [:lang_locale :license_number :user_id :password])

(defn access-request-info
  "Pull access request information from secure location?"
  [access-data]
  (xml/element :AccessRequest {:xml:lang (access-data :lang_locale)}
    (xml/element :AccessLicenseNumber {} (access-data :license_number))
    (xml/element :UserId {} (access-data :user_id))
    (xml/element :Password {} (access-data :password))
    ) )

;;   <TransactionReference>
;;     <CustomerContext>guidlikesubstance</CustomerContext>
;;     <XpciVersion>1.0001</XpciVersion>
;;   </TransactionReference>
(def txn-reference-keys [:customer_context_id :xpci_version])

(defn transaction-reference-info
  [txn_reference_data]
  (xml/element :TransactionReference {}
    (xml/element :CustomerContext {} (txn_reference_data :customer_context_id))
    (xml/element :XpciVersion {} (txn_reference_data :xpci_version))
    ) )

;; <Address>
;;   <AddressLine1>201 York Rd</AddressLine1>
;;   <City>Timonium</City>
;;   <StateProvinceCode>MD</StateProvinceCode>
;;   <CountryCode>US</CountryCode>
;;   <PostalCode>21093</PostalCode>
;;   <ResidentialAddress/>
;; </Address>
(def address-keys [:address1 :address2 :city :state_province :country_code :postal :residential])

(defn address-info
  "A UPS Address block"
  [address_data]
  (xml/element :Address {}
    (xml/element :AddressLine1 {} (address_data :address1))
    (when-not (nil? (address_data :address2)) (xml/element :AddressLine2 {} (address_data :address2)) )
    (xml/element :City {} (address_data :city))
    (xml/element :StateProvinceCode {} (address_data :state_province))
    (xml/element :CountryCode {} (address_data :country_code))
    (xml/element :PostalCode {} (address_data :postal))
    ;; todo: ugly and this won't render like <ResidentialAddress/> when empty
    (if (nil? (address_data :residential))
      (xml/element :ResidentialAddress {})
      (xml/element :ResidentialAddress{} (address_data :residential)))
    ) )

;; <Shipper>
;;   <Name>Joe's Garage</Name>
;;   <AttentionName>John Smith</AttentionName>
;;   <PhoneNumber>9725551212</PhoneNumber>
;;   <ShipperNumber>123X67</ShipperNumber>
;;   <Address>
;;     <AddressLine1>1000 Preston Rd</AddressLine1>
;;     <City>Plano</City>
;;     <StateProvinceCode>TX</StateProvinceCode>
;;     <CountryCode>US</CountryCode>
;;     <PostalCode>75093</PostalCode>
;;   </Address>
;; </Shipper>
(def shipper-keys [:name :attention_name :phone :shipper_number :address])

(defn sistemi-shipper-xx-info
  "Shipping from a specific Sistemi fabricator."
  [shipper_data]
  (xml/element :Shipper {}
    (xml/element :Name {} (shipper_data :name))
    (xml/element :AttentionName {} (shipper_data :attention_name))
    (xml/element :PhoneNumber {} (shipper_data :phone))
    (xml/element :ShipperNumber {} (shipper_data :shipper_number))
    (address-info (shipper_data :address))
    ) )

;; <ShipTo>
;;   <CompanyName>Pep Boys</CompanyName>
;;   <AttentionName>Manny</AttentionName>
;;   <PhoneNumber>41051255512121234</PhoneNumber>
;;   <Address>
;;     <AddressLine1>201 York Rd</AddressLine1>
;;     <City>Timonium</City>
;;     <StateProvinceCode>MD</StateProvinceCode>
;;     <CountryCode>US</CountryCode>
;;     <PostalCode>21093</PostalCode>
;;     <ResidentialAddress />
;;   </Address>
;; </ShipTo>
(def shipto-keys [:company :attention_name :phone :address])

(defn ship-to-xx-info
  "Ship to a Customer"
  [ship_to_data]
  (xml/element :ShipTo {}
    (xml/element :CompanyName {} (ship_to_data :company))
    (xml/element :AttentionName {} (ship_to_data :attention_name))
    (xml/element :PhoneNumber {} (ship_to_data :phone))
    (address-info (ship_to_data :address)) ) )

;; <Service>
;;   <Code>14</Code>
;;   <Description>Next Day Air Early AM</Description>
;; </Service>
(def service-keys [:code :description])

(defn shipping-service-info
  "The type of shipping service required"
  [service_data]
  (xml/element :Service {}
    (xml/element :Code {} (service_data :code))
    (xml/element :Description {} (service_data :description)) ) )

;; <PaymentInformation>
;;   <Prepaid>
;;     <BillShipper>
;;       <CreditCard>
;;         <Type>06</Type>
;;         <Number>4111111111111111</Number>
;;         <ExpirationDate>121999</ExpirationDate>
;;       </CreditCard>
;;     </BillShipper>
;;   </Prepaid>
;; </PaymentInformation>
(def payment-info-keys [:type :card_number :expiration_date])

(defn payement-info
  "Customer payment information details."
  [payment_info_data]
  (xml/element :PaymentInformation {}
    (xml/element :Prepaid {}
      (xml/element :BillShipper {}
        (xml/element :CreditCard {}
          (xml/element :Type {} (payment_info_data :type))
          (xml/element :Number {} (payment_info_data :card_number))
          (xml/element :ExpirationDate {} (payment_info_data :expiration_date))
          ) ) )
    ) )

;; <ReferenceNumber>
;;   <Code>02</Code>
;;   <Value>1234567</Value>
;; </ReferenceNumber>
(def reference-number-keys [:code :value])

(defn reference-number-info
  "The reference number for the shipping package"
  [reference_number_data]
  (xml/element :ReferenceNumber {}
    (xml/element :Code {} (reference_number_data :code))
    (xml/element :Value {} (reference_number_data :value))
    ) )

;; <LabelSpecification>
;;   <LabelPrintMethod>
;;     <Code>GIF</Code>
;;   </LabelPrintMethod>
;;   <HTTPUserAgent>Mozilla/4.5</HTTPUserAgent>
;;   <LabelImageFormat>
;;     <Code>GIF</Code>
;;   </LabelImageFormat>
;; </LabelSpecification>
(def label-spec-keys [:label_print_code :http_user_agent :label_image_code ])

(defn label-spec-info
  "Label information."
  [label_spec_data]
  (xml/element :LabelSpecification {}
    (xml/element :LabelPrintMethod {}
      (xml/element :Code {} (label_spec_data :label_print_code)) )
    (xml/element :HTTPUserAgent {} (label_spec_data :http_user_agent))
    (xml/element :LabelImageFormat {}
      (xml/element :Code {} (label_spec_data :label_image_code)) )
  ) )