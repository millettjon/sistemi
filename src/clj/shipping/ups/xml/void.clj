(ns shipping.ups.xml.void
  (:require [shipping.ups.xml.modules :as m]))

(defn create-void-shipment-request-xml
  [void_data]
  (m/access-request void_data)
  [:VoidShipmentRequest
   [:Request
    (m/transaction-reference-info void_data)
    [:RequestAction 1]
    [:RequestOption] ]
   [:ExpandedVoidShipment
    [:ShipmentIdentificationNumber (void_data :shipment_number)]
    ;; 0 - N TrackingNumber values here if they exist.
    ]
   ])