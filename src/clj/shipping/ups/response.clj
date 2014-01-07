(ns shipping.ups.response
  (:import [java.io ByteArrayInputStream])
  (:require [clojure.string :as str]
            [shipping.ups.util :as u]
            [clojure.xml :as xm]
            [clojure.zip :as zip])
  (:use [clojure.data.zip.xml :only (text xml->)]))

(defn is-xml-element
  "Is the current object an XML Element defrecord?"
  [object]
  (if (nil? object)
    false
    (= clojure.data.xml.Element (class object)) ) )

(defn text-in-bytestream
  "Turn text into ByteArrayInputStream (trimmed text)"
  [text]
  (ByteArrayInputStream. (.getBytes (str/trim text))) )

;; todo: was hoping to refactor extraction calls to this
(defn- get-xml-value
  [data & tags]
  (first (xml-> data tags)) )

(defn get-shipment-confirm-response
  "Map xml response data to internal map for common usage."
  [sc_response]
  (let [input (xm/parse (text-in-bytestream sc_response))
        data (zip/xml-zip input)]
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
    ) ) )

