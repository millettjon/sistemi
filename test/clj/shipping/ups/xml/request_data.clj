(ns shipping.ups.xml.request_data)

;;
(defn sistemi-data
  "access, fabricator shipping, payment info, shipping option, insurance options, etc
  Please note that config_access_data has already been pulled from (config :ups), the keys
  are slightly different."
  [config_access_data]
  ;; I tried explicit map access within the merge block, but that failed
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
  {:packages
     (list
       {:type_code "02"
        :dimension_data {:unit_code "CM" :length "22" :width "20" :height "18"}
        :weight_data {:weight "14.1" :unit_code "KGS"}
        ; verbal confirmation does not always work
        :service_options '()
        :service_data {:insurance {:currency_code "EUR" :value "50.00"}        ; insurance, what range?
                       :verbal_conf {:name "Eric Romeo" :phone "123456777"}} }  ; verbal confirmation
       )}
    )


(defn simple-request-data
  "Meged data from 'sistemi-data', 'customer-data', and 'package-data'"
  [config_access_info]
  (merge (sistemi-data config_access_info) customer-data package-data) )


