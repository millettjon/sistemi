(ns shipping.ups.transact_test
  (:require [clj-http.client :as client]
            [shipping.ups.transact :as trans])
  (:use [clojure.test]
        [shipping.ups.request_test]))

(def google (client/get "http://www.google.com"))

(def ship_confirm "https://onlinetools.ups.com/ups.app/xml/ShipConfirm")
(def ship_accept "https://onlinetools.ups.com/ups.app/xml/ShipAccept")

;; Examples
;(client/get "https://wwwcie.ups.com/ups.app/xml/ShipConfirm")
;{:trace-redirects ["https://wwwcie.ups.com/ups.app/xml/ShipConfirm"], :request-time 513, :status 200, :headers {"date" "Fri, 17 Jan 2014 04:07:17 GMT", "server" "Apache", "x-frame-options" "SAMEORIGIN", "content-length" "242", "connection" "close", "content-type" "text/html; charset=ISO-8859-1"}, :body "<HTML>\r\n<HEAD><TITLE>UPS Online Tools ShipConfirm</TITLE></HEAD>\r\n<BODY><H2>\r\nService Name: ShipConfirm<br>\r\nRemote User: null<br>\r\nServer Port: 443<br>\r\nServer Name: wwwcie.ups.com<br>\r\nServlet Path: /ShipConfirm<br>\r\n</H2>\r\n</BODY></HTML>\r\n"}

(deftest test-request-shipping
  (let [rsp (trans/request-shipping shipment-confirm-xml)]

    (println rsp)
    ) )