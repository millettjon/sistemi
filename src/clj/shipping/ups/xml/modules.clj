(ns shipping.ups.xml.modules
  (:require [shipping.ups.packages :as p]))

(defn m->v
  "Function that takes a map and a key and returns a hiccup style vector."
  [m k]
  [k (k m)])

;; Need a way to leave out
(defn m->v
  "Function that takes a map and a key and returns a hiccup style vector."
  ([m k]
     (m->v m k {}))
  ([m k {:keys [optional?]}]
     (let [v (k m)
           required (not optional?)]
       (if (or v required)
         [k v]))))

;; TODO: add these as tests
#_ (m->v {:foo "FOO"} :foo) ; [:foo "FOO"]
#_ (m->v {:foo "FOO"} :foo {}) ; [:foo "FOO"]
#_ (m->v {:foo "FOO"} :foo {:optional? true}) ; [:foo "FOO"]
#_ (m->v {:bar "BAR"} :foo {:optional? true}) ; nil

(defn access-request
  "Builds xml vector used to authenticate UPS API requests."
  [access-request-map]
  (let [v (partial m->v access-request-map)]
    [:AccessRequest {:xml:lang "en-US"}
     (v :AccessLicenseNumber)
     (v :UserId)
     (v :Password)]))

(defn transaction-reference-info
  [txn-reference-map]
  (let [v (partial m->v txn-reference-map)]
    [:TransactionReference
     (v :CustomerContext)
     ;; [:XpciVersion "1.0001"] ; ? Is this needed?
     ]))

(defn address-info
  "Returns an address as an xml vector."
  [{:keys [AddressLine2 ResidentialAddress] :as address-map}]
  (let [v (partial m->v address-map)]
    [:Address
     (v :AddressLine1)
     (v :AddressLine2 {:optional? true})
     (v :City)
     (v :StateProvinceCode)
     (v :CountryCode)
     (v :PostalCode)
     (v :ResidentialAddress)]))
;; TODO: ? What is the residential address flag used for? Can it take a value?
;; 35.59 w/o ResidentalAddress
;; 38.79 w/ ResidentalAddress

;; TODO: ? Can this be combined with the ship-to-info?
(defn sistemi-shipper-info
  "Returns shipper information as an xml structure."
  [{:keys [Address] :as shipper-map}]
  (let [v (partial m->v shipper-map)]
    [:Shipper
     (v :Name)
     (v :AttentionName)
     (v :PhoneNumber)
     (v :ShipperNumber)
     (address-info Address)]))

;; TODO: ? Does this include the right set of fields?
(defn ship-to-info
  "Returns recipient info as an xml structure."
  [{:keys [Address] :as ship-to-map}]
  (let [v (partial m->v ship-to-map)]
    [:ShipTo
     (v :Name)
     (v :CompanyName) ; required; can't be blank; WTF?
     (v :PhoneNumber)
     (address-info Address)]))

;; TODO: What are the various shipping codes?
;; TODO: Select this from a map of predefined options?
(defn service-info
  "The type of shipping service required"
  [service-map]
  (let [v (partial m->v service-map)]
    [:Service
     (v :Code)
     (v :Description)]))

(defn payment-info
  "Customer payment information details."
  [payment-map]
  (let [v (partial m->v payment-map)]
    [:PaymentInformation
     [:Prepaid
      [:BillShipper
       [:CreditCard
        (v :Type)
        (v :Number)
        (v :ExpirationDate)]]]]))

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

(def label-spec-info
  "Label information."
  [:LabelSpecification
   [:LabelPrintMethod [:Code "GIF"]]
   [:HTTPUserAgent "Mozilla/4.5"]
   [:LabelImageFormat [:Code "GIF"]]])

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
