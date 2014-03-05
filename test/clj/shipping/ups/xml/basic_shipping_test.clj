(ns shipping.ups.xml.basic_shipping_test
  (:require [clj-http.client :as client]
            [clojure.data.xml :as x]
            [sistemi.config]
            [app.config :as c]
            [shipping.ups.xml.transact :as trans]
            [shipping.ups.xml.modules :as m]
            [shipping.ups.xml.request :as sr]
            [shipping.ups.xml.response :as rsp]
            [shipping.ups.xml.request_test :as rqt])
  (:use [clojure.test]) )

(def ship_confirm "https://onlinetools.ups.com/ups.app/xml/ShipConfirm")
(def ship_accept "https://onlinetools.ups.com/ups.app/xml/ShipAccept")
(def xml x/sexp-as-element)

;; ************ Request Data *********************************
(def txn-reference-data {:customer_context_id "SistemiContextID-XX1122" :xpci_version "1.0001"})
(def service-data {:code "11" :description "UPS Standard"})
(def payment-data {:type "06" :card_number "4111111111111111" :expiration_date "102016"})
(def label-spec-data {:label_print_code "GIF" :http_user_agent "Mozilla/4.5" :label_image_code "GIF"})

;; todo: expects everything as String and does not accept int, long, double, etc
(def dimension-data {:unit_code "CM" :length "22" :width "20" :height "18"})
(def weight-data {:weight "14.1" :unit_code "KGS"})

(def service-options-none '())
(def insurance-data {:currency_code "EUR" :value "50.00"})
(def verbal-conf-data {:name "Eric Romeo" :phone "123456777"})
(def service-options-data (merge insurance-data verbal-conf-data))

(def shipping-package-data-1 {:type_code "02" :dimension_data dimension-data :weight_data weight-data
                              :service_data service-options-data ;:reference_data ct/reference-number-data
                              :service_options service-options-none})

;; Fabricator UPS account information
(def shipper-address-data {:address1 "ZA la Croisette" :city "Clelles en Trièves" :state_province ""
                           :country_code "FR" :postal "38930"})

(def receiver-address-data {:address1 "130 route de la combette" :city "St. Martin d'Uriage" :state_province ""
                            :country_code "FR" :postal "38410"})

; Phone numbers are 10 alpha-numeric (Europe 2.2.2.2.2  (
(def shipper-data {:user_id "SistemiShipper" :attention_name "SistemiFabricator" :phone "0423456789"
                   :shipper_number "123456" :address shipper-address-data})

; Phone numbers are 10 alpha-numeric
(def receiver-data {:user_id "SistemiReceiver" :attention_name "SistemiCustomer" :phone "0412345678"
                    :shipper_number "123456" :company "Sistemi" :address receiver-address-data})


(defn access-data-from-config
  "Pulled from our encrypted config"
  [access_info]
  { :user_id (access_info :user-id)
    :password (access_info :password)
    :license_number (access_info :access-key)
    :account_number (access_info :account-number)
    :lang_locale "en-US"
    } )

(defn shipper-info-data
  "Pull test address data (todo: move from ct)"
  [access_data]
  (let [sd1 shipper-data]
    (assoc-in sd1 [:shipper_number] (access_data :account_number))
    ) )

(defn access-request
  "Pulled from encrypted config (reuse for all transactions).
  This returns 'header' information for confirmed access."
  [access_info]
  (m/access-request-info access_info) )

(defn shipment-confirm-request
  [shipping_data]
  (let [confirm_request {:txn_reference txn-reference-data
                         :shipper (shipping_data :shipper_data)
                         :ship_to receiver-data
                         :ship_service service-data
                         :payment payment-data
                         :packages (list shipping-package-data-1)
                         :label label-spec-data}]

    (sr/shipment-confirm-request confirm_request)
    ) )

(defn shipment-confirm-request-xml
  "Combine AccessRequest and ShipmentConfirmRequest xml"
  [access_info]
  ; This top section should be called "build-data" and return 'req_data'
  (let [access_data (access-data-from-config access_info)
        shipper_data (shipper-info-data access_data)
        req_data {:access_data access_data :shipper_data shipper_data :service_attempt_code "5"}
        access (xml (access-request access_data))
        confirm (xml (shipment-confirm-request req_data))]

    (str (x/emit-str access) (x/emit-str confirm))
    ) )

;(deftest test-shipment-confirm-request
;  (sistemi.config/init!)
;  ;(pr c/config)
;  (let [ups_access (c/conf :ups)
;        raw_req (shipment-confirm-request-xml ups_access)
;        raw_rsp (trans/request-shipping raw_req)
;        response (rsp/get-shipment-confirm-response raw_rsp)]
;
;    ;(println (str "raw request:\n" raw_req "\n"))
;    ;(println (str "raw response:\n" raw_rsp))
;    (println (str "response:\n" response))
;    ) )


;; ****************** XML Sample **************************
; integration test
;<?xml version="1.0" encoding="UTF-8"?>
;<AccessRequest xml:lang="en-US">
;<AccessLicenseNumber>see-config-retrieval</AccessLicenseNumber>
;<UserId>sistemiups</UserId>
;<Password>ups-password</Password>
;</AccessRequest><?xml version="1.0" encoding="UTF-8"?><ShipmentConfirmRequest>
;<Request>
;<RequestAction>ShipConfirm</RequestAction>
;<RequestOption>nonvalidate</RequestOption>
;<TransactionReference>
;<CustomerContext>SistemiContextID-XX1122</CustomerContext>
;<XpciVersion>1.0001</XpciVersion>
;</TransactionReference>
;</Request>
;<Shipment>
;<Shipper>
;<Name>SistemiShipper</Name>
;<AttentionName>SistemiFabricator</AttentionName>
;<PhoneNumber>0423456789</PhoneNumber>
;<ShipperNumber>AY3413</ShipperNumber>
;<Address>
;<AddressLine1>ZA la Croisette</AddressLine1>
;<City>Clelles en Trièves</City>
;<StateProvinceCode></StateProvinceCode>
;<CountryCode>FR</CountryCode>
;<PostalCode>38930</PostalCode>
;<ResidentialAddress></ResidentialAddress>
;</Address>
;</Shipper>
;<ShipTo>
;<CompanyName>Sistemi</CompanyName>
;<AttentionName>SistemiCustomer</AttentionName>
;<PhoneNumber>0412345678</PhoneNumber>
;<Address>
;<AddressLine1>130 route de la combette</AddressLine1>
;<City>St. Martin d'Uriage</City>
;<StateProvinceCode></StateProvinceCode>
;<CountryCode>FR</CountryCode>
;<PostalCode>38410</PostalCode>
;<ResidentialAddress></ResidentialAddress>
;</Address>
;</ShipTo>
;<Service>
;<Code>11</Code>
;<Description>UPS Standard</Description>
;</Service>
;<PaymentInformation>
;<Prepaid>
;<BillShipper>
;<CreditCard>
;<Type>06</Type>
;<Number>4111111111111111</Number>
;<ExpirationDate>102016</ExpirationDate>
;</CreditCard>
;</BillShipper>
;</Prepaid>
;</PaymentInformation>
;<Package>
;<PackagingType>
;<Code>02</Code>
;</PackagingType>
;<Dimensions>
;<UnitOfMeasurement>
;<Code>CM</Code>
;</UnitOfMeasurement>
;<Length>22</Length>
;<Width>20</Width>
;<Height>18</Height>
;</Dimensions>
;<PackageWeight>
;<UnitOfMeasurement>
;<Code>KGS</Code>
;</UnitOfMeasurement>
;<Weight>14.1</Weight>
;</PackageWeight>
;<PackageServiceOptions></PackageServiceOptions>
;</Package>
;<LabelSpecification>
;<LabelPrintMethod>
;<Code>GIF</Code>
;</LabelPrintMethod>
;<HTTPUserAgent>Mozilla/4.5</HTTPUserAgent>
;<LabelImageFormat>
;<Code>GIF</Code>
;</LabelImageFormat>
;</LabelSpecification>
;</Shipment>
;</ShipmentConfirmRequest>
