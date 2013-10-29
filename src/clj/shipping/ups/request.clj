(ns shipping.ups.request
  (:require [clojure.data.xml :as xml]
            [shipping.ups.common :as c]
            [shipping.ups.package :as pkg]))

;; Reference vectors of keys below when looking up information for the order.
;; Use (zipmap) or equivalent to combine keys with order data and then pass
;; into functions below to build XML request/response


(def q "'")

(defn sq
  [arg]
  (clojure.string/join (list q arg q)))

;; Meat and Potatoes ...............

;; <Request>
;;   <TransactionReference>
;;     <CustomerContext>guidlikesubstance</CustomerContext>
;;     <XpciVersion>1.0001</XpciVersion>
;;   </TransactionReference>
;;   <RequestAction>ShipConfirm</RequestAction>
;;   <RequestOption>nonvalidate</RequestOption>
;; </Request>
(def shipment-confirm-request
  "Part 1 of a 2 part shipping order."
  [shipper]
  (xml/element :ShipmentConfirmRequest {}
    (xml/element :Request {}
      (c/transaction-reference {})
      (xml/element :RequestAction {} "ShipConfirm")
      (xml/element :RequestOption {} "nonvalidate") )
    (xml/element :Shipment {}
      (c/sistemi-shipper-xx {})
      (c/ship-to-xx {})
      (c/shipping-service {})
      (c/payement-info {})
      ()
       )
    ) )

;(def shipment-accept-request)

;(defn -main []
;  (println (xml/emit-str access-request)))
