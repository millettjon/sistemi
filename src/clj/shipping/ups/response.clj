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

; Could refactor the (first (xml-> data)) to something like (foo data [& tags])
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
          ) ) )
      '() pkgs_xml)
    ) )

(defn get-shipment-accept-response
  "A response to the 2nd of 2 requests for UPS shipping."
  [sa_response]
  (let [input (xm/parse (text-in-bytestream sa_response))
        data (zip/xml-zip input)]
    (assoc {}
      :tracking_number (first (xml-> data :ShipmentResults :ShipmentIdentificationNumber text))
      :response_status (first (xml-> data :Response :ResponseStatus text))
      :response_status_description (first (xml-> data :Response :responseStatusDescription text))
      :transportation_charges (first (xml-> data :ShipmentResults :ShipmentCharges :TransportationCharges :MonetaryValue text))
      :service_options_charges (first (xml-> data :ShipmentResults :ShipmentCharges :ServiceOptionsCharges :MonetaryValue text))
      :total_charges  (first (xml-> data :ShipmentResults :ShipmentCharges :TotalCharges :MonetaryValue text))
      :billing_weight (first (xml-> data :ShipmentResults :BillingWeight text))
      :packages (get-response-packages data)
      ) ) )
