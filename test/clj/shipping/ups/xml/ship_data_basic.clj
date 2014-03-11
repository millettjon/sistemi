(ns shipping.ups.xml.ship_data_basic)

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

(defn shipper-info-data
  "Pull test address data (todo: move from ct)"
  [access_data]
  (let [sd1 shipper-data]
    (assoc-in sd1 [:shipper_number] (access_data :account_number))
    ) )

(defn merged-ship-confirm-data-basic
  "A simplified map composed of defined maps."
  [access_data]
  (let [shipper_data (shipper-info-data access_data)]
    (merge
      {:access_data access_data}
      {:shipper_data shipper_data}
      {:service_attempt_code "5"}
      {:packages (list shipping-package-data-1)}
      {:txn_reference txn-reference-data}
      {:shipper shipper_data}
      {:ship_to receiver-data}
      {:ship_service service-data}
      {:payment payment-data}
      {:label label-spec-data}
      ) ) )