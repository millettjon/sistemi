(ns shipping.ups.xml.transact
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]
            [shipping.ups.xml.request :as req]
            [shipping.ups.xml.response :as rsp]))


;; Test sandbox url
(def shipping-confirm "https://onlinetools.ups.com/ups.app/xml/ShipConfirm")
(def shipping-accept "https://onlinetools.ups.com/ups.app/xml/ShipAccept")

;; Broken -- UPS says "put" not supported by this url
(defn request-shipping
  "Use Customer, Shelving, and Sistemi information to build the
  2 part UPS shipping request (ShippingConfirm, ShippingAccept)"
  [ship_confirm_xml]
  (let [ship_confirm_raw_rsp (client/post shipping-confirm {:body ship_confirm_xml :insecure? true})
        ship_confirm_rsp (ship_confirm_raw_rsp :body)
        ;ship_accept_xml (req/)
        ;ship_accept_raw (client/post shipping-accept {:body ship_accept_xml :insecure? true})
        ]
    ship_confirm_rsp
    ) )

