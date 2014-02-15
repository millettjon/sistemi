(ns shipping.ups.xml.response
  (:require [clojure.string :as str]
            [shipping.ups.tools :as t]
            [clojure.xml :as xm]
            [clojure.zip :as zip]
            [clojure.tools.logging :as log])
  (:use [clojure.data.zip.xml :only (text xml->)]))

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