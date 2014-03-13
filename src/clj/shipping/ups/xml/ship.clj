(ns shipping.ups.xml.ship
  (:require [shipping.ups.xml.modules :as m]
            [shipping.ups.xml.tools :as t]
            [app.config :as c]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [clojure.data.xml :as x]
            [clojure.xml :as xm]
            [clojure.zip :as zip]
            [clj-http.client :as client])
  (:use [clojure.data.zip.xml :only (text xml->)]))

(def q "'")
(def xml x/sexp-as-element)

(def ship_confirm_test "https://onlinetools.ups.com/ups.app/xml/ShipConfirm")
(def ship_accept_test "https://onlinetools.ups.com/ups.app/xml/ShipAccept")

(def ship_confirm "https://wwwcie.ups.com/ups.app/xml/ShipConfirm")
(def ship_accept "https://wwwcie.ups.com/ups.app/xml/ShipAccept")

(defn sq
  "Wrap quotes? Shit I can remember ;-)"
  [arg]
  (clojure.string/join (list q arg q)))

;; todo: check with other request error messages
(defn failure-info
  "Checks the ShipmentConfirmResponse for a failure and logs 'ErrorCode'
  and 'ErrorDescription' if true."
  [sc_rsp_data]
  (let [status (first (xml-> sc_rsp_data :Response :ResponseStatusDescription text))
        error (first (xml-> sc_rsp_data :Response :Error text))
        error_msg (first (xml-> sc_rsp_data :Response :Error :ErrorDescription text))]
    (if (or (not (nil? error)) (= "Failure" status))
      (do
        (log/error (str "ShipmentConfirmResponse Failure '"
                     (xml-> sc_rsp_data :Response :Error :ErrorCode text) ", " error_msg))
        {:error_msg error_msg})
      nil
      ) ) )

;; Meat and Potatoes ...............

(def shipping-request-keys [:txn_reference :shipper :ship_to :ship_service :payment :packages :label])

(defn create-ship-confirm-request-xml
  "Part 1 of a 2 part shipping order. General notes:
  RequestOption
    'validate' - street level validation only
    'nonvalidate' - postal code, state validation (no street)
    '' - defaults to 'validate' option
    UPS says full address validation is not performed here ($penalty for address correction)"
  [request_data]
  [:ShipmentConfirmRequest
   [:Request
    [:RequestAction "ShipConfirm"]
    [:RequestOption "nonvalidate"]
    (m/transaction-reference-info (request_data :txn_reference))]
   [:Shipment
    ;      [:Description (m/handle-optional (request_data :description) "")]
    ;      [:ReturnService
    ;        [:Code (m/handle-optional (request_data :service_attempt_code) "5")] ]
    ;      [:DocumentsOnly (m/handle-optional (request_data :documents_only) "")]
    (m/sistemi-shipper-info (request_data :shipper))
    (m/ship-to-info (request_data :ship_to))
    (m/shipping-service-info (request_data :ship_service))
    (m/payement-info (request_data :payment))
    (m/shipping-packages-info (request_data :packages))
    (m/label-spec-info (request_data :label)) ]
   ] )


(defn create-ship-accept-request-xml
  "After receiving a successful 'Shipment Confirm Response' build this request
  and send to UPS."
  [request_accept_data]
  [:AccessRequest
   [:AccessLicenseNumber (request_accept_data :sistemi_license_no)]
   [:UserId (request_accept_data :user_id)]
   [:Password (request_accept_data :password)] ]
  [:ShipmentAcceptRequest
   [:Request
    [:TransactionReference
     [:CustomerContext (request_accept_data :customer_context_id)]
     [:XpciVersion (request_accept_data :xpci_version)] ]
    [:RequestAction "ShipAccept"] ]
   [:ShipmentDigest (request_accept_data :shipment_digest)] ] )


; Could refactor the (first (xml-> data)) to something like (foo data [& tags])
(defn get-shipment-confirm-response
  "Map xml response data to internal map for common usage."
  [sc_response]
  (let [input (xm/parse (t/text-in-bytestream sc_response))
        data (zip/xml-zip input)
        failure (failure-info data)]
    (if (nil? failure)
      (assoc {}
        :tracking_number (first (xml-> data :ShipmentIdentificationNumber text))
        :response_status (first (xml-> data :Response :ResponseStatus text))
        :response_status_description (first (xml-> data :Response :ResponseStatusDescription text))
        :billing_weight (first (xml-> data :BillingWeight text))
        :transportation_charges (first (xml-> data :ShipmentCharges :TransportationCharges :MonetaryValue text))
        :service_options_charges (first (xml-> data :ShipmentCharges :ServiceOptionsCharges :MonetaryValue text))
        :total_charges (first (xml-> data :ShipmentCharges :TotalCharges :MonetaryValue text))
        :shipment_digest (first (xml-> data :ShipmentDigest text))
        :customer_context_id (first (xml-> data :CustomerContext text))
        :xpci_version (first (xml-> data :XpciVersion text))
        )
      failure)
    ) )

(defn get-response-packages
  "Get the tracking, image, etc information for each package."
  [data]
  (let [pkgs_xml (xml-> data :ShipmentResults :PackageResults)]
    (reduce
      (fn [pkgs package]
        (conj pkgs
          (assoc {}
            :tracking_number (first (xml-> package :TrackingNumber text))
            :service_options_charges (first (xml-> package :ServiceOptionsCharges :MonetaryValue text))
            :accessory_charges (first (xml-> package :AccessorialCharges :MonetaryValue text))
            :label_image_code (first (xml-> package :LabelImage :LabelImageFormat :Code text))
            :label_image (first (xml-> package :LabelImage :GraphicImage text))
            :label_image_html (first (xml-> package :LabelImage :HTMLImage text))
            ) ) )
      '() pkgs_xml)
    ) )

(defn get-shipment-accept-response
  "A response to the 2nd of 2 requests for UPS shipping."
  [sa_response]
  (let [input (xm/parse (t/text-in-bytestream sa_response))
        data (zip/xml-zip input)
        failure (failure-info data)]
    (if (nil? failure)
      (assoc {}
        :tracking_number (first (xml-> data :ShipmentResults :ShipmentIdentificationNumber text))
        :response_status (first (xml-> data :Response :ResponseStatus text))
        :response_status_description (first (xml-> data :Response :responseStatusDescription text))
        :transportation_charges (first (xml-> data :ShipmentResults :ShipmentCharges :TransportationCharges :MonetaryValue text))
        :service_options_charges (first (xml-> data :ShipmentResults :ShipmentCharges :ServiceOptionsCharges :MonetaryValue text))
        :total_charges  (first (xml-> data :ShipmentResults :ShipmentCharges :TotalCharges :MonetaryValue text))
        :billing_weight (first (xml-> data :ShipmentResults :BillingWeight text))
        :packages (get-response-packages data)
        )
        failure)
      ) )


;;;;;;;;;;;;;;; transactional part (FIX ME) ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- ship-confirm-request-xml
  "Combine AccessRequest and ShipmentConfirmRequest xml"
  [confirm_req_data]
  ;  (m/access-request-info access_data)
  (let [access-xml (xml (m/access-request-xml (confirm_req_data :access_data)))
        confirm-xml (xml (create-ship-confirm-request-xml confirm_req_data))]

    (str (x/emit-str access-xml) (x/emit-str confirm-xml))
    ) )

(defn- create-ship-accept-request-data
  "Build shipment accept request data from txn_reference and shipment_digest (from confirm_response)"
  [access_data confirm_request_data confirm_response_data]
  (let [accept_request_data {:access_data access_data
                             :txn_reference confirm_request_data
                             :shipment_digest (confirm_response_data :shipment_digest) }]

    accept_request_data
    ) )

(defn- ship-accept-request-xml
  "Build the shipment accept request xml"
  [accept_req_data]
  (let [access-xml (xml (m/access-request-xml (accept_req_data :access_data)))
        accept-xml (xml (create-ship-accept-request-xml accept_req_data))]

    (str (x/emit-str access-xml) (x/emit-str accept-xml))
    ) )

(defn shipping-trans-part1
  "The first part of scheduling a UPS shipment (ship confirm). It is the first of two parts.
  Use this to calculate shipping costs or potentially as a holding state for the shipping txn.
  Itsrequires user + sistemi + fabricator information.

  This requires that config be initialized"
  ([ship_confirm_data]
    (shipping-trans-part1 ship_confirm_data (t/access-data-from-config (c/conf :usp))) )
  ([ship_data access_data]
    (let [ship_confirm_data (merge ship_data access_data)
          ship_confirm_req_xml (ship-confirm-request-xml ship_confirm_data)
          ; Should this be secure?
          ship_confirm_raw_rsp (client/post ship_confirm {:body ship_confirm_req_xml :insecure? true})
          ship_confirm_rsp (get-shipment-confirm-response (ship_confirm_raw_rsp :body))]

      ;(println (str "ship_confirm_request:\n" ship_confirm_req "\n"))
      ;(println (str "ship_confirm_raw_response:\n" ship_confirm_raw_rsp "\n"))
      ;(println (str "ship_confirm_response:\n" ship_confirm_rsp "\n"))
      ship_confirm_rsp
    ) ) )

(defn shipping-trans-part2
  "Part 2 of a UPS shipping request 'ship accept'. It is fully dependent on the information from
  the first part of a shipping request 'ship confirm'. Notably, it depends on the huge digest.

  This requires config initialization."
  ([ship_confirm_response]
    (shipping-trans-part2 ship_confirm_response (t/access-data-from-config (c/conf :usp))) )
  ([ship_confirm_response access_data]
    (let [ship_accept_data (create-ship-accept-request-data access_data ship_confirm_response)
          ship_accept_req_xml (ship-accept-request-xml ship_accept_data)
          ship_accept_raw_rsp (client/post ship_accept {:body ship_accept_req_xml :insecure? true})
          ship_accept_rsp (get-shipment-accept-response (ship_accept_raw_rsp :body))]

      ;(println (str "ship accept data:\n" ship_accept_data "\n"))
      ;(println (str "ship accept request:\n" ship_accept_req "\n"))
      ;(println (str "ship accept raw response:\n" ship_accept_raw_rsp "\n"))
      ;(println (str "ship_accept_response:\n" ship_accept_rsp "\n"))
    ship_accept_rsp
    ) ) )

(defn shipping-transaction-full
  "The simplest full ship transaction I could get to work. It is composed of two parts:
  1) ship confirm, 2) ship accept   (see shipping-trans-part1, part2)
  This requires initialized config."
  [access_data shipping_data]
  (let [ups_access (c/conf :ups)
        access_data (t/access-data-from-config ups_access)
        ; part 1: shipping confirm
        ship_confirm_data (merge shipping_data access_data)
        ship_confirm_req_xml (ship-confirm-request-xml ship_confirm_data)
        ship_confirm_raw_rsp (client/post ship_confirm {:body ship_confirm_req_xml :insecure? true})
        ship_confirm_rsp (get-shipment-confirm-response (ship_confirm_raw_rsp :body))
        ; part 2: shipping accept
        ship_accept_data (create-ship-accept-request-data access_data ship_confirm_rsp)
        ship_accept_req_xml (ship-accept-request-xml ship_accept_data)
        ship_accept_raw_rsp (client/post ship_accept {:body ship_accept_req_xml :insecure? true})
        ship_accept_rsp (get-shipment-accept-response (ship_accept_raw_rsp :body))
        ]

    ;(println (str "ship_confirm_request:\n" ship_confirm_req "\n"))
    ;(println (str "ship_confirm_raw_response:\n" ship_confirm_raw_rsp "\n"))
    ;(println (str "ship_confirm_response:\n" ship_confirm_rsp "\n"))
    ;(println (str "ship accept data:\n" ship_accept_data "\n"))
    ;(println (str "ship accept request:\n" ship_accept_req "\n"))
    ;; Might fail with credit card auth
    ;(println (str "ship accept raw response:\n" ship_accept_raw_rsp "\n"))
    ;(println (str "ship_accept_response:\n" ship_accept_rsp "\n"))
    ) )
