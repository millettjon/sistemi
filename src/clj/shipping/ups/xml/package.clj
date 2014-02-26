(ns shipping.ups.xml.package)
;   (:require [shipping.ups.xml.common :as c]))
;
;;; <Dimensions>
;;;   <UnitOfMeasurement>
;;;     <Code>IN</Code>
;;;   </UnitOfMeasurement>
;;;   <Length>22</Length>
;;;   <Width>20</Width>
;;;   <Height>18</Height>
;;; </Dimensions>
;(def dimension-keys [:unit_code :length :width :height])
;
;(defn dimension-info
;  "The physical dimensions for the package (excludes weight)"
;  [dimension_data]
;  [:Dimensions
;    [:UnitOfMeasurement
;      [:Code (dimension_data :unit_code)]]
;    [:Length (dimension_data :length)]
;    [:Width (dimension_data :width)]
;    [:Height (dimension_data :height)]
;  ])
;
;;; <PackageWeight>
;;;   <Weight>14.1</Weight>
;;; </PackageWeight>
;(def weight-keys [:weight :unit])
;
;(defn weight-info
;  "How much the package weighs -- lbs or SI?????"
;  [weight_data]
;  [:PackageWeight
;    [:UnitOfMeasurement
;      [:Code (weight_data :unit_code)]]
;    [:Weight (weight_data :weight)]
;  ])
;
;;; <InsuredValue>
;;;   <CurrencyCode>USD</CurrencyCode>
;;;   <MonetaryValue>149.99</MonetaryValue>
;;; </InsuredValue>
;(def insurance-keys [:currency_code :value])
;
;;; todo: check optional values
;(defn insurance-option-info
;  "If the package is insured."
;  [insurance_data]
;  [:InsuredValue
;    [:CurrencyCode (insurance_data :currency_code)]
;    [:MonetaryValue (insurance_data :value)]
;  ])
;
;;; <VerbalConfirmation>
;;;   <Name>Sidney Smith</Name>
;;;   <PhoneNumber>4105551234</PhoneNumber>
;;; </VerbalConfirmation>
;(def verbal-conf-keys [:name :phone])
;
;(defn verbal-conf-option-info
;  "A Shipping option for each package."
;  [verbal_conf_data]
;  [:VerbalConfirmation
;   [:Name (verbal_conf_data :name)]
;   [:PhoneNumber (verbal_conf_data :phone)]
;  ])
;
;;; Not really necessary for use, other than to provide map depth {:insurance {:currency_code :value}}
;(def service-option-keys [:insurance :verbal_conf])
;
;;; todo: pass data onto
;(defn service-option-info
;  "The service options for each package in a Shipment like
;  'InsuredValue' and 'VerbalConfirmation'"
;  [option_data options]
;  [:PackageServiceOptions
;    (for [option options]
;      (option option_data) )
;  ])
;
;(def package-keys [:type_code :dimension_data :weight_data :reference_data :service_data :service_options])
;
;(defn shipping-package-info
;  "Package information for shipping."
;  [package_data]
;  [:Package
;    [:PackagingType
;      [:Code (package_data :type_code)] ]
;    (dimension-info (package_data :dimension_data))
;    (weight-info (package_data :weight_data))
;    ; Generates error with and error without
;    ;(c/reference-number-info (package_data :reference_data))
;    (service-option-info (package_data :service_data) (package_data :service_options))
;    ])
;
;(defn shipping-packages-info
;  "The information for each package to be shipped. Dump xml with
;  (map p/xml result), otherwise it's just a collection of vectors."
;  [packages_data]
;  (for [data packages_data] (shipping-package-info data)) )
