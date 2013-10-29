(ns shipping.ups.package
   (:require [clojure.data.xml :as x]
             [shipping.ups.common :as c]))

(def xml x/sexp-as-element)

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
  (xml
    [:Dimensions
      [:UnitOfMeasurement
        [:Code (dimension_data :unit_code)]]
      [:Length (dimension_data :length)]
      [:Width (dimension_data :width)]
      [:Height (dimension_data :height)]
    ]) )

;; <PackageWeight>
;;   <Weight>14.1</Weight>
;; </PackageWeight>
(def weight-keys [:weight :unit])

(defn weight-info
  "How much the package weighs -- lbs or SI?????"
  [weight_data]
  (xml
    [:PackageWeight
      [:Weight (weight_data :weight)]
    ] ) )

;; <InsuredValue>
;;   <CurrencyCode>USD</CurrencyCode>
;;   <MonetaryValue>149.99</MonetaryValue>
;; </InsuredValue>
(def insurance-keys [:currency_code :value])

;; todo: check optional values
(defn insurance-option-info
  "If the package is insured."
  [insurance_data]
  (xml
    [:InsuredValue
      [:CurrencyCode (insurance_data :currency_code)]
      [:MonetaryValue (insurance_data :value)]
    ] ) )

;; <VerbalConfirmation>
;;   <Name>Sidney Smith</Name>
;;   <PhoneNumber>4105551234</PhoneNumber>
;; </VerbalConfirmation>
(def verbal-conf-keys [:name :phone])

(defn verbal-conf-option-info
  "A Shipping option for each package."
  [verbal_conf_data]
  (xml
    [:VerbalConfirmation
     [:Name (verbal_conf_data :name)]
     [:PhoneNumber (verbal_conf_data :phone)]]
    ) )

;; Not really necessary for use, other than to provide map depth {:insurance {:currency_code :value}}
(def service-option-keys [:insurance :verbal_conf])

;; todo: pass data onto
(defn service-option-info
  "The service options for each package in a Shipment like
  'InsuredValue' and 'VerbalConfirmation'"
  [option_data options]
  (xml
    [:PackageServiceOptions
    ;  (reduce (fn [result option] (conj result (option option_data))) '() options)
    ;  (map option_data (list options))
    ;  (apply option_data (comp options))
    (comp (for [option options]
        (option option_data) ) )
  ] ) )

(defn shipping-package
  "Package information for shipping."
  [package_data packages]
  (x/element :Package {}
    (x/element :PackagingType {}
      (x/element :Code {}) )
    (dimension-info {})
    (weight-info {})
    (c/reference-number-info {})
    (service-option-info {}) ) )

(defn shipping-packages
  "The information for each package to be shipped."
  [package_data packages]
  (apply package_data packages) )
