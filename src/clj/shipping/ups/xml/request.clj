(ns shipping.ups.xml.request
  (:require [shipping.ups.xml.modules :as m]))

;; Reference vectors of keys below when looking up information for the order.
;; Use (zipmap) or equivalent to combine keys with order data and then pass
;; into functions below to build XML request/response

;; *************** xml tags (required vs optional) ***************************
; <AccessRequest xml:lang="en-US">
;   <AccessLicenseNumber>6CC91514112476D6</AccessLicenseNumber>
;   <UserId>sistemiups</UserId>
;   <Password>foobarzoo</Password>
; </AccessRequest>
; <?xml version="1.0" encoding="UTF-8"?>
; <ShipmentConfirmRequest>
;   <Request>
;     <RequestAction>ShipConfirm</RequestAction>
;     <!-- nonvalidate: no street level, just postal and state
;          validate (default): street level validation, no state/postal -->
;     <RequestOption>nonvalidate</RequestOption>
;    <!-- Optional block
;    <TransactionReference>
;       <CustomerContext>SistemiContextID-XX1122</CustomerContext>
;       <XpciVersion>1.0001</XpciVersion>
;     </TransactionReference>
;     -->
;   </Request>
;
;    <Shipment>
;      <!-- Optional value
;      <Description></Description>
;      -->
;      <ReturnService>
;        <Code>5</Code>
;      </ReturnService>
;      <!-- Optional value
;      <DocumentsOnly></DocumentsOnly>
;      -->
;      <Shipper>
;        <Name>Sistemi</Name>
;        <AttentionName>SistemiFabricator</AttentionName>
;        <PhoneNumber>000111222</PhoneNumber>
;        <ShipperNumber>123456</ShipperNumber>
;        <!-- Optional/Conditional value
;        <TaxIdentificationNumber></TaxIdentificationNumber>
;        <FaxNumber></FaxNumber>
;        <EmailAddress></EmailAddress>
;        -->
;        <Address>
;          <AddressLine1>123 Sistemi Drive</AddressLine1>
;          <!-- Optional
;          <AddressLine2></AddressLine2>
;          <AddressLine3></AddressLine3>
;          -->
;          <City>St. Martin D'Uriage</City>
;          <!-- Required in US, Canada, Ireland -->
;          <StateProvinceCode></StateProvinceCode>
;          <CountryCode>FR</CountryCode>
;          <PostalCode>12345</PostalCode>
;          <ResidentialAddress></ResidentialAddress>
;        </Address>
;      </Shipper>


(def q "'")

(defn sq
  "Wrap quotes? Shit I can remember ;-)"
  [arg]
  (clojure.string/join (list q arg q)))

;; Meat and Potatoes ...............

(def shipping-request-keys [:txn_reference :shipper :ship_to :ship_service :payment :packages :label])

(defn shipment-confirm-request
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
