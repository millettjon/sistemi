(ns shipping.ups.xml.modules
  (:require [shipping.ups.packages :as p]))

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

(defn access-request-xml
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

;; <Dimensions>
;;   <UnitOfMeasurement>
;;     <Code>IN</Code>
;;   </UnitOfMeasurement>
;;   <Length>22</Length>
;;   <Width>20</Width>
;;   <Height>18</Height>
;; </Dimensions>
(def dimension-keys [:unit_code :length :width :height])

(defn dimension-info
  "The physical dimensions for the package (excludes weight)"
  [dimension_data]
  [:Dimensions
   [:UnitOfMeasurement
    [:Code (dimension_data :unit_code)]]
   [:Length (dimension_data :length)]
   [:Width (dimension_data :width)]
   [:Height (dimension_data :height)]
   ])

;; <PackageWeight>
;;   <Weight>14.1</Weight>
;; </PackageWeight>
(def weight-keys [:weight :unit])

(defn weight-info
  "How much the package weighs, default KG (pseudo SI).
  It appears that > 26 kg billing weight triggers an extra
  fee via UPS warning. 'LargePackageIndicator' does not seem
  to do much."
  [weight_data]
  [:PackageWeight
   [:UnitOfMeasurement
    [:Code (weight_data :unit_code)]]
   [:Weight (weight_data :weight)]
   ;[:LargePackageIndicator]
   ])

;; <InsuredValue>
;;   <CurrencyCode>USD</CurrencyCode>
;;   <MonetaryValue>149.99</MonetaryValue>
;; </InsuredValue>
(def insurance-keys [:currency_code :value])

;; todo: check optional values
(defn insurance-option-info
  "Optional field for package insurance. If no data is present, then no xml appears.
  This also works. "
  [insurance_data]
  (if (nil? insurance_data)
    nil
    [:InsuredValue
      [:CurrencyCode (insurance_data :currency_code)]
      [:MonetaryValue (insurance_data :value)]
      ]) )

(def insurance-declared-value-keys [:currency_code :code :value])

(defn insurance-declared-value
  "Provided by UPS's Sebastien (it works) Use insurance-declared-value-keys"
  [insurance_data]
  (if (nil? insurance_data)
    nil
    [:DeclaredValue
     [:Type
      [:Code "01"]]
     [:CurrencyCode (insurance_data :currency_code)]
     [:MonetaryValue (insurance_data :value)]
     ]) )

;; <VerbalConfirmation>
;;   <Name>Sidney Smith</Name>
;;   <PhoneNumber>4105551234</PhoneNumber>
;; </VerbalConfirmation>
(def verbal-conf-keys [:name :phone])

(defn verbal-conf-option-info
  "A Shipping option for each package. This does not work when paired with
  "
  [verbal_conf_data]
  (if (nil? verbal_conf_data)
    nil
    [:VerbalConfirmation
      [:Name (verbal_conf_data :name)]
      [:PhoneNumber (verbal_conf_data :phone)]
      ]) )

(def service-option-keys [:insurance :verbal_conf])

(defn package-service-option-info
  "Generate optional XML for things like 'insurance' and 'verbal confirmation'."
  [service_data]
  [:PackageServiceOptions
   ;(insurance-option-info (service_data :insurance))
   (insurance-declared-value (service_data :insurance))
   ;; Does not seem to work for this option
   ;(verbal-conf-option-info (service_data :verbal_conf))
   ]
  )

(def package-keys [:type_code :dimension_data :weight_data :reference_data :service_data :service_options])

(defn shipping-package-info
  "Package information for shipping."
  [package_data]
  [:Package
   [:PackagingType
    [:Code (package_data :type_code)] ]
   ;; Pass the 'heavy' indicator up through weight
   (dimension-info (package_data :dimension_data))
   (weight-info (package_data :weight_data))
   (package-service-option-info (package_data :service_data))
   ])

(defn shipping-packages-info
  "The information for each package to be shipped. Dump xml with
  (map p/xml result), otherwise it's just a collection of vectors."
  [packages_data]
  (for [data packages_data] (shipping-package-info data)) )


(defn void-shipment-info
  "Build void xml node for each tracking number"
  [void_data]
  ; get tracking numbers list here
  (for [data void_data] ([:TrackingNumber data]) ) )