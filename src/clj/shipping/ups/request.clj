(ns shipping.ups.request
  (:require [shipping.ups.common :as c]
            [shipping.ups.package :as p]))

;; Reference vectors of keys below when looking up information for the order.
;; Use (zipmap) or equivalent to combine keys with order data and then pass
;; into functions below to build XML request/response

(def q "'")

(defn sq
  [arg]
  (clojure.string/join (list q arg q)))

;; Meat and Potatoes ...............

(def shipping-request-keys [:txn_reference :shipper :ship_to :ship_service :payment :packages :label])

;; <Request>
;;   <TransactionReference>
;;     <CustomerContext>guidlikesubstance</CustomerContext>
;;     <XpciVersion>1.0001</XpciVersion>
;;   </TransactionReference>
;;   <RequestAction>ShipConfirm</RequestAction>
;;   <RequestOption>nonvalidate</RequestOption>
;; </Request>
(defn shipment-confirm-request
  "Part 1 of a 2 part shipping order."
  [request_data]
  [:ShipmentConfirmRequest
    [:Request
      (c/transaction-reference-info (request_data :txn_reference)) ]
    [:RequestAction "ShipConfirm"]
    [:RequestOption "nonvalidate"]
    [:Shipment
      (c/sistemi-shipper-info (request_data :shipper))
      (c/ship-to-info (request_data :ship_to))
      (c/shipping-service-info (request_data :ship_service))
      (c/payement-info (request_data :payment))
      (p/shipping-packages-info (request_data :packages))
      (c/label-spec-info (request_data :label)) ]
    ] )

(defn shipment-accept-request
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
      [:RequestAction (request_accept_data :request_action)] ]
    [:ShipmentDigest (request_accept_data :shipment_digest)] ] )
