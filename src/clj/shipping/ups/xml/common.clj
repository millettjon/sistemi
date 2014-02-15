(ns shipping.ups.xml.common)

(defn handle-optional
  "If a value is null, then return the 'default'."
  [value default]
  (if (nil? value)
    default
    value
    ) )


;; Used to sign into UPS as a Customer
;;
;; <?xml version="1.0" ?>
;; <AccessRequest xml:lang='en-US'>
;;   <AccessLicenseNumber>YOURACCESSLICENSENUMBER</AccessLicenseNumber>
;;   <UserId>YOURUSERID</UserId>
;;   <Password>YOURPASSWORD</Password>
;; </AccessRequest>
(def access-request-keys [:lang_locale :license_number :user_id :password])

(defn access-request-info
  "Pull access request information from secure location?"
  [access-data]
  [:AccessRequest {:xml:lang (access-data :lang_locale)}
    [:AccessLicenseNumber {} (access-data :license_number)]
    [:UserId {} (access-data :user_id)]
    [:Password {} (access-data :password)] ]
  )

;;   <TransactionReference>
;;     <CustomerContext>guidlikesubstance</CustomerContext>
;;     <XpciVersion>1.0001</XpciVersion>
;;   </TransactionReference>
(def txn-reference-keys [:customer_context_id :xpci_version])

(defn transaction-reference-info
  [txn_reference_data]
  [:TransactionReference
    [:CustomerContext (txn_reference_data :customer_context_id)]
    [:XpciVersion (txn_reference_data :xpci_version)] ]
  )


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
  [:Address
    [:AddressLine1 (address_data :address1)]
    (when-not (nil? (address_data :address2))
      [:AddressLine2 (address_data :address2)])
    [:City (address_data :city)]
    [:StateProvinceCode (address_data :state_province)]
    [:CountryCode (address_data :country_code)]
    [:PostalCode (address_data :postal)]
    ;; todo: ugly and this won't render like <ResidentialAddress/> when empty
    (if (nil? (address_data :residential))
      [:ResidentialAddress]
      [:ResidentialAddress (address_data :residential)]) ]
  )

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

(defn sistemi-shipper-info
  "Shipping from a specific Sistemi fabricator."
  [shipper_data]
  [:Shipper
    [:Name (shipper_data :user_id)]
    [:AttentionName (shipper_data :attention_name)]
    [:PhoneNumber (shipper_data :phone)]
    [:ShipperNumber (shipper_data :shipper_number)]
    (address-info (shipper_data :address)) ]
  )

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

(defn ship-to-info
  "Ship to a Customer"
  [ship_to_data]
  [:ShipTo
    [:CompanyName (ship_to_data :company)]
    [:AttentionName (ship_to_data :attention_name)]
    [:PhoneNumber (ship_to_data :phone)]
    (address-info (ship_to_data :address))]
  )

;; <Service>
;;   <Code>14</Code>
;;   <Description>Next Day Air Early AM</Description>
;; </Service>
(def service-keys [:code :description])

(defn shipping-service-info
  "The type of shipping service required"
  [service_data]
  [:Service
    [:Code (service_data :code)]
    [:Description (service_data :description)] ]
  )

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
  [:PaymentInformation
    [:Prepaid
      [:BillShipper
        [:CreditCard
          [:Type (payment_info_data :type)]
          [:Number (payment_info_data :card_number)]
          [:ExpirationDate (payment_info_data :expiration_date)] ]
        ] ] ]
  )

;; <ReferenceNumber>
;;   <Code>02</Code>
;;   <Value>1234567</Value>
;; </ReferenceNumber>
(def reference-number-keys [:code :value])

(defn reference-number-info
  "The reference number for the shipping package"
  [reference_number_data]
  [:ReferenceNumber
    [:Code (reference_number_data :code)]
    [:Value (reference_number_data :value)] ]
  )

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
  [:LabelSpecification
    [:LabelPrintMethod
      [:Code (label_spec_data :label_print_code)] ]
    [:HTTPUserAgent (label_spec_data :http_user_agent)]
    [:LabelImageFormat
      [:Code (label_spec_data :label_image_code)] ] ]
  )