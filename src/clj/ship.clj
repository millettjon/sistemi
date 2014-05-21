(ns ship
  (:require [schema.core :as s]
            [clojure.data.xml :as x]
            [shipping.ups.xml.ship :as dship]
            )
  (:use [app.config :only (conf)]))

;; TODO:
;; - Make sure sensitive info is redacted when logging ups requests/responses.
;; - Vett shipper information.

;; It will lock you out.
;; - UPS: Repeated failures can result in a temporary lock out.
;; - They want paty
;; - W

;; Inputs needed:
;;   - ship_to section
;;   - package details
;;     - for each package
;;       - size: width, length, height (cm)
;;       - weight (kg)
;;

;;
;; fn to convert hash to vector

;; DESIGN DECISION: Use keywords on the same format as required in the xml.

(def Access
  "A schema for a nested data type"
  {:AccessLicenseNumber s/Str
   :UserId s/Str
   :Password s/Str})


#_ (s/validate
    Access
    {:AccessLicenseNumber "6CC91514112476D6"
     :UserId "sistemiups"
     :Password "*redacted*"})

;; --------------------------------------------------
;; SYSTEMI WRAPPED INTERFACE
;; --------------------------------------------------

;; TODO: Make sure to use correct country code (e.g., FR).
(defn address->ups
  "Converts a sistemi address map to a ups one."
  [m]
  {:AddressLine1 (:address1 m)
   :City (:city m)
   :StateProvinceCode (:region m)
   :CountryCode (:country m)
   :PostalCode (:code m)})

(def confirm-data
  {
   ;; from order
   :ship_to {:Name "SistemiReceiver",
             :PhoneNumber "0412345678",
             :CompanyName "Sistemi", ; TODO: ? why is this required? what should it be?
             :Address {:AddressLine1 "130 route de la combette",
                       :City "St. Martin d'Uriage",
                       :StateProvinceCode "",
                       :CountryCode "FR",
                       :PostalCode "38410"
                       }},

   ;; from order
   :packages [{:type_code "02",
                :dimension_data {:width "20",
                                 :length "22",
                                 :unit_code "CM",
                                 :height "18"},
                :weight_data {:weight "14.1",
                              :unit_code "KGS"},
                :service_data {}}],
   })

;; ? How to get package dimentions and weight from spreadsheet?
;; ? How to get the number of packages?
;; ? How to do packaging for shelves?

(s/defn ^:always-validate estimate*
  [url
   access :- Access
   data]
  (let [data (assoc data
               :Service {:Code "11", :Description "UPS Standard"})
        response (dship/shipping-trans-part1 data access url)]
    ;; TODO: Verify that response is valid. Handle errors.
    ;; {:error_msg "Invalid Authentication Information.", :status "Failure", :severity "Hard"}
    ;; {:error_msg "The XML document is well formed but the document is not valid", :status "Failure", :severity "Hard"}
    (prn "RESPONSE" response)
    ))

;; TODO: Make sure client post uses https.
;; TODO: How should errors be handled?
;; TODO: Figure out what data needs to be saved. Does any data need to be saved?
;;       - total charges
;;       - tracking number
;; TODO: ? How long are the confirm requests valid for?
;; TODO: ? Do they need to be canceled?
;; TODO: ? Is it easier to just do a new request at the time of shipping?

(defn estimate
  "Returns a shipping estimate given an order."
  [{:keys [id] :as order}]
  (let [url (conf :ups :urls :confirm)
        access (conf :ups :AccessRequest)
        data (assoc confirm-data

               ;; Free form field used to store order id.
               ;; TODO: ? Is it feasable to not use this?
               ;; TODO: ? Should this use a random id that can't be linked back?
               :TransactionReference {:CustomerContext id}

               ;; Hardcode to using Stephane's factory for now.
               :Shipper (conf :ups :Shippers :stephane)

               :PaymentInformation (conf :ups :CreditCard)
               )]
    (estimate* url access data)))

#_ (estimate {:id "a5c3"})





