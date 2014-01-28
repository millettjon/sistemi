(ns shipping.ups.transact_test
  (:require [clj-http.client :as client]
            [clojure.data.xml :as x]
            [sistemi.config]
            [app.config :as c]
            [shipping.ups.transact :as trans]
            [shipping.ups.common :as cmn]
            [shipping.ups.request :as sr]
            [shipping.ups.common_test :as ct]
            [shipping.ups.package_test :as pt]
            [shipping.ups.request_test :as rqt])
  (:use [clojure.test]) )

(def google (client/get "http://www.google.com"))

(def ship_confirm "https://onlinetools.ups.com/ups.app/xml/ShipConfirm")
(def ship_accept "https://onlinetools.ups.com/ups.app/xml/ShipAccept")

;; Examples
;(client/get "https://wwwcie.ups.com/ups.app/xml/ShipConfirm")
;{:trace-redirects ["https://wwwcie.ups.com/ups.app/xml/ShipConfirm"], :request-time 513, :status 200, :headers {"date" "Fri, 17 Jan 2014 04:07:17 GMT", "server" "Apache", "x-frame-options" "SAMEORIGIN", "content-length" "242", "connection" "close", "content-type" "text/html; charset=ISO-8859-1"}, :body "<HTML>\r\n<HEAD><TITLE>UPS Online Tools ShipConfirm</TITLE></HEAD>\r\n<BODY><H2>\r\nService Name: ShipConfirm<br>\r\nRemote User: null<br>\r\nServer Port: 443<br>\r\nServer Name: wwwcie.ups.com<br>\r\nServlet Path: /ShipConfirm<br>\r\n</H2>\r\n</BODY></HTML>\r\n"}

; integration test
;
;<ShipmentConfirmResponse><Response>
;   <TransactionReference>
;     <CustomerContext>SistemiContextID-XX1122</CustomerContext>
;     <XpciVersion>1.0001</XpciVersion>
;   </TransactionReference>
;   <ResponseStatusCode>0</ResponseStatusCode>
;   <ResponseStatusDescription>Failure</ResponseStatusDescription>
;   <Error>
;     <ErrorSeverity>Hard</ErrorSeverity><ErrorCode>250005</ErrorCode>
;     <ErrorDescription>No Access and Authentication Credentials provided</ErrorDescription>
;   </Error>
; </Response></ShipmentConfirmResponse>}
;
;(deftest test-request-shipping_invalid_request
;  (sistemi.config/init!)
;  (pr c/config)
;  (let [ups (pr (c/conf :ups))
;        rsp (trans/request-shipping rqt/shipment-confirm-xml)]
;
;    (is (= "No Access and Authentication Credentials provided" (-> rsp :error_msg)))
;    ) )

(def xml x/sexp-as-element)

(def shipment-confirm-request
  (let [confirm_request {:txn_reference ct/txn-reference-data
                         :shipper ct/shipper-data
                         :ship_to ct/ship-to-data
                         :ship_service ct/service-data
                         :payment ct/payment-data
                         :packages (list pt/shipping-package-data-1)
                         :label ct/label-spec-data}]

    (sr/shipment-confirm-request confirm_request)
    ) )

(defn access-request
  "Pulled from encrypted config (reuse for all transactions).
  This returns 'header' information for confirmed access."
  [access_info]
  (let [access_data  { :user_id (access_info :user-id)
                       :password (access_info :password)
                       :license_number (access_info :access-key)
                       :lang_locale "en-US"}]

    (cmn/access-request-info access_data)
    ) )

(defn shipment-confirm-request-xml
  "Combine AccessRequest and ShipmentConfirmRequest xml"
  [access_info]
  (let [access (xml (access-request access_info))
        confirm (xml shipment-confirm-request)]

    (str (x/emit-str access) (x/emit-str confirm))
    ) )

(deftest test-shipment-confirm-request
  (sistemi.config/init!)
  ;(pr c/config)
  (let [ups_access (c/conf :ups)
        rsp (trans/request-shipping (shipment-confirm-request-xml ups_access))]

    (println rsp)
    ) )
