(ns shipping.ups.transact
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]))


;; Test sandbox url
(def shipping-confirm "https://onlinetools.ups.com/ups.app/xml/ShipConfirm")
(def shipping-accept "")

;; Broken -- UPS says "put" not supported by this url
(defn request-shipping
  "Use Customer, Shelving, and Sistemi information to build the
  2 part UPS shipping request (ShippingConfirm, ShippingAccept)"
  [raw_xml_data]
  (let [ship_confirm_rsp (client/post shipping-confirm {:content raw_xml_data})]
    ship_confirm_rsp
    ) )