(ns shipping.ups.xml.request_data)

;(defn merged-ship-confirm-data-basic
;  "A simplified map composed of defined maps."
;  [access_data]
;  (let [shipper_data (shipper-info-data access_data)]
;    (merge
;      ;{:access_data access_data}
;      ;{:shipper_data shipper_data} ; Why is in here twice?
;      ;{:service_attempt_code "5"}  ; optional
;      ;{:ship_service service-data}
;      ;{:shipper shipper_data}
;      ;{:payment payment-data}
;      ;{:label label-spec-data}
;      ;{:txn_reference txn-reference-data}
;      ;{:ship_to receiver-data}
;      ;{:packages (list shipping-package-data-1)}
;      ) ) )

;;
(defn sistemi-data
  "access, fabricator shipping, payment info, shipping option, insurance options,"
  [config_access_data]
  ;; I tried explicit map access within the merge block, but that failed
  (println (str "sistemi-data():\n" config_access_data))
  (let [user (config_access_data :user_id)
        password (config_access_data :password)
        license (config_access_data :license_number)
        account (config_access_data :account_number)]
    (merge
      ; Pull from configuration data
      {:access_data {:user_id user :password password :license_number license :account_number account
                     :lang_locale "en-US"} }
      ; Merge with 'customer-data' Pull customer + session from datomic + session id generator
      {:txn_reference {:xpci_version "1.0001"
                       :customer_context_id "pull-me-from-customer-data"} } ;pull from customer-data

      ; shipping option (explore the other options)
      {:ship_service {:code "11" :description "UPS Standard"}}
      ; How should we set up payments?
      {:payment {:type "06" :card_number "4111111111111111" :expiration_date "102016"}}
      ; probably static information
      {:label {:label_print_code "GIF" :http_user_agent "Mozilla/4.5" :label_image_code "GIF"} }
      ; Depends on which fabricator/finisher is used
      {:shipper {:user_id "SistemiShipper" :attention_name "SistemiFabricator" :phone "0423456789"
                 :shipper_number account
                 :address {:address1 "ZA la Croisette" :city "Clelles en Trièves" :state_province ""
                           :country_code "FR" :postal "38930"}} }
      {:shipper_data {:user_id "SistemiShipper" :attention_name "SistemiFabricator" :phone "0423456789"
                      :shipper_number account
                      :address {:address1 "ZA la Croisette" :city "Clelles en Trièves" :state_province ""
                                :country_code "FR" :postal "38930"}}}
    ) ) )

(def customer-data
  "Receiver address, phone number, customer number and session"
  (merge
    ; Use customer id + customer session (datomic)
    {:customer_context_id "SistemiContextID-XX1122"}
    {:ship_to {:user_id "SistemiReceiver"
               :attention_name "SistemiCustomer"
               :phone "0412345678" ; 10 alpha-numeric
               :shipper_number "123456"
               :company "Sistemi"
               :address {:address1 "130 route de la combette"
                         :city "St. Martin d'Uriage"
                         :state_province ""
                         :country_code "FR"
                         :postal "38410"}} }
    ) )

(def package-data
  "Package dimensions pulled from google docs/frinj/etc"
  {:package_data
     (list
       {:type_code "02"
        :dimension_data {:unit_code "CM" :length "22" :width "20" :height "18"}
        :weight_data {:weight "14.1" :unit_code "KGS"}
        ; No key-name conflict with these 2 service options (future refactor: specify service type)
        ; Depends on shipping-service code (list m/insurance-option-info m/verbal-conf-option-info)
        :service_options '()
        :service_data {:currency_code "EUR" :value "50.00"       ; insurance, what range?
                       :name "Eric Romeo" :phone "123456777"} }  ; verbal confirmation
       )}
    )


(defn simple-request-data
  "Meged data from 'sistemi-data', 'customer-data', and 'package-data'"
  [config_access_info]
  (merge (sistemi-data config_access_info) customer-data package-data) )

;;;;;;;;;;;;  From 'basic_shipping_test'

;(def txn-reference-data {:customer_context_id "SistemiContextID-XX1122" :xpci_version "1.0001"})
;(def service-data {:code "11" :description "UPS Standard"})
;(def payment-data {:type "06" :card_number "4111111111111111" :expiration_date "102016"})
;(def label-spec-data {:label_print_code "GIF" :http_user_agent "Mozilla/4.5" :label_image_code "GIF"})
;
;;; todo: expects everything as String and does not accept int, long, double, etc
;(def dimension-data {:unit_code "CM" :length "22" :width "20" :height "18"})
;(def weight-data {:weight "14.1" :unit_code "KGS"})
;
;(def service-options-none '())
;(def insurance-data {:currency_code "EUR" :value "50.00"})
;(def verbal-conf-data {:name "Eric Romeo" :phone "123456777"})
;(def service-options-data (merge insurance-data verbal-conf-data))
;
;(def shipping-package-data-1 {:type_code "02" :dimension_data dimension-data :weight_data weight-data
;                              :service_data service-options-data ;:reference_data ct/reference-number-data
;                              :service_options service-options-none})
;
;;; Fabricator UPS account information
;(def shipper-address-data {:address1 "ZA la Croisette" :city "Clelles en Trièves" :state_province ""
;                           :country_code "FR" :postal "38930"})
;
;; Phone numbers are 10 alpha-numeric (Europe 2.2.2.2.2  (
;(def shipper-data {:user_id "SistemiShipper" :attention_name "SistemiFabricator" :phone "0423456789"
;                   :shipper_number "123456" :address shipper-address-data})
;
;(def receiver-address-data {:address1 "130 route de la combette" :city "St. Martin d'Uriage" :state_province ""
;                            :country_code "FR" :postal "38410"})
;
;; Phone numbers are 10 alpha-numeric
;(def receiver-data {:user_id "SistemiReceiver" :attention_name "SistemiCustomer" :phone "0412345678"
;                    :shipper_number "123456" :company "Sistemi" :address receiver-address-data})

