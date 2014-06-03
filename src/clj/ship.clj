(ns ship
  (:require [schema.core :as s]
            [clojure.data.xml :as x]
            [shipping.ups.xml.ship :as dship]
            [frinj.ops :as f]
            [util.frinj :as fu]
            pack)
  (:use [app.config :only (conf)]
        clojure.pprint
        ))

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
;; (defn address->ups
;;   "Converts a sistemi address map to a ups one."
;;   [m]
;;   {:AddressLine1 (:address1 m)
;;    :City (:city m)
;;    :StateProvinceCode (:region m)
;;    :CountryCode (-> m :country name)
;;    :PostalCode (:code m)})

(defn fj->ups
  "Converts a frinj value to a ups value."
  [value unit decimals]
  (-> value
      (f/to unit)
      (fu/fj-round decimals)
      :v
      bigdec
      str))
(defn to->cm [v] (fj->ups v :cm 0))
(defn to->kg [v] (fj->ups v :kg 2))

(defn box->ups
  "Converts box data to ups format."
  [box]
  {:Code "02"
   :Dimensions {:Width  (to->cm pack/box-width)
                :Height (to->cm pack/box-height)
                :Length (-> box pack/box-max-part-length to->cm)
                :Code "CM"}
   :PackageWeight {:Weight (-> box pack/box-mass to->kg)
                   :Code "KGS"}
   :PackageServiceOptions {}})

;; Country Code List: http://www.ups.com/worldshiphelp/WS14/ENU/AppHelp/Codes/Country_Territory_and_Currency_Codes.htm
(defn address->ups
  "Converts a phone and address to a ups address."
  [{:keys [contact address1 address2 city region code country] :as address}
   {:keys [phone] :as order-contact}]
  {:CompanyName (:name contact)
   :AttentionName (:name contact)
   :PhoneNumber phone
   :Address {:AddressLine1 address1
             :AddressLine2 address2
             :City city
             :StateProvinceCode region
             :PostalCode code
             :CountryCode (clojure.core/name country)}})

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
    (prn "RESPONSE" (dissoc response :shipment_digest))
    {:price {:total (:total-charges response)}}))


;; TODO: Need a way to tell if a shipment is internationl when building the ShipTo address.
;; TODO: Review shipping.ups.packages with Dave.
;; TODO: Packages over 26kg are "heavy" and need a sticker.
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
  [{:keys [id items contact shipping] :as order}]
  (let [url (conf :ups :urls :confirm)
        access (conf :ups :AccessRequest)
        boxes (mapcat #(->> % second pack/parts pack/make-boxes (map box->ups)) items)
        data {;; Free form field used to store order id.
              ;; TODO: ? Is it feasable to not use this?
              ;; TODO: ? Should this use a random id that can't be linked back?
              :TransactionReference {:CustomerContext id}

              ;; Hardcode to using Stephane's factory for now.
              :Shipper (conf :ups :Shippers :stephane)
              :PaymentInformation (conf :ups :CreditCard)
              :ShipTo (address->ups (:address shipping) contact)
              :Packages boxes
              :Description "Shelving" ; required for international; 35 chars
              }]
    (-> (estimate* url access data)
        (assoc :boxes boxes))))

;; TODO: ? Do the charges include sales tax?
;; TODO: ? Can we estimate the charges using a formula? In excel?
;;
;; Note: They round the billing weight up to the nearest 1/2 kg.
;; 120 $ 50.56 33.0kg
;; 200 $ 53.76 49.5kg
;; 220 $ 62.33 64.5kg (56kg)
;; 230 $ 62.33 65.6kg (57kg)
;; 232 $ 62.33 66.0kg
;; 233 $159.77 98.0kg
;; 240 $159.77 98.0kg
;;                     
;; http://en.wikipedia.org/wiki/Dimensional_weight
;; http://www.ups.com/content/us/en/resources/ship/packaging/dim_weight.html (us units)
;; http://www.ups.com/content/gb/en/resources/ship/packaging/dim_weight.html (metric)
;; - round up to next 0.5 kg
;; - dimensional weight is cubic volume (cm3) divided by 5000 (round up to next 0.5kg)
;; - large package: length + girth > 330cm
;; - max package: length + girth > 419cm
;; - large packages are subject to a surcharge
;; - large packages have a minimum billable weight of 40kg
;;
;; - Compare the package's actual weight to its dimensional
;;   weight. The greater of the two is the billable weight and should
;;   be used to calculate the rate.
;; - A Large Package Surcharge may apply to domestic and international
;;   shipments. A package is considered a "Large Package" when its
;;   length plus girth [(2 x width) + (2 x height)] combined exceeds
;;   130 inches.
;; - Large Packages are subject to a minimum billable weight of 90
;;   pounds. An Additional Handling charge will not be assessed when a
;;   Large Package Surcharge is applied.
;;
;; TASKS
;; - insert shipping address from order
;;   - the shipping estimate should be re-calculated any time the items or shipping address changes
;; - generate a unique order id to pass in
;; - when shipping address is submitted
;;   - get ups shipping estimate
;;   - inject shipping estimate into spreadsheet
;;   - update price estimate to include shipping
;;
;; - if the order is placed
;;   ? Should we send the tracking number immediately? (future?)
;;   ? Should we store the shipping estimate in datomic? (future?)
;; - work on an optimal packing strategy
;;   - combine parts from multiple items in the same box
;;   - avoid large box surcharge
;;   - pack shorter boards in one layer in a longer box
;; - handle response errors from ups
;; - handle timeout errors from ups
;;

;; BUG: ? Why is tax not being calculated on a french order?
;;        ? Is it an issue of the country field not being validated?

;; - add customer-context
;; - log ups request event
;; - log ups response event
;; - add order number as customer context
;; - get tracking number (don't bother displaying until package ships)
;; - get total charges
;; - easter egg for package details
;; - save shipping info in datomic (packing, prices, ups info etc)
;;
;; {:customer_context_id nil, :service_options_charges "0.00", :total_charges "35.53", :billing_weight "KGS9.5", :response_status nil, :transportation_charges "35.53", :response_status_description "Success", :tracking_number "1ZAY34136893867193", :xpci_version nil}
