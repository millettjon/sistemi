(ns shipping.ups.xml.insured_ground_shipping_test
  (:require [clj-http.client :as client]
            [clojure.data.xml :as x]
            [sistemi.config]
            [app.config :as c]
            [shipping.ups.xml.util :as u]
            [shipping.ups.tools :as t]
            [shipping.ups.xml.modules :as m]
            [shipping.ups.xml.ship :as ship]
            [shipping.ups.xml.request_data :as rd]
            [shipping.ups.xml.ship_test :as st])
  (:use [clojure.test]))


(defn update-ship-service-code
  "Update the shipping service to UPS Ground"
  [request_data]
  ; "03" invalid for these 2 locations
  ; "07" works with insurance, but is almost 2 x "11"
  ; "08" invalid for these 2 locations
  ; ["11" cheapest option with insurance]
  ; "12" invalid for these 2 locations
  ; ["65" more than "11", but less than "07"]
  (assoc-in request_data [:ship_service] {:Code "11" :Description "Ground"}))

(deftest test-simple-insured-shipment
  (sistemi.config/init!)
  (let [ups_access (c/conf :ups)
        access_data (t/access-data-from-config ups_access)
        basic_data (rd/simple-insured-request-data access_data)
        insured_basic_data (update-ship-service-code basic_data)
        ship_confirm_rsp (ship/shipping-trans-part1 insured_basic_data access_data st/ship_confirm_test_url)
        ;ship_accept_rsp (ship/shipping-trans-part2 ship_confirm_rsp access_data ship/ship_accept_test_url)
        ]

    ;; (println "ups_access:\n" ups_access "\n")
    ;; (println "access_data:\n" access_data "\n")
    ;; (println "basic_data:\n" basic_data "\n")

    ;(println "ship_confirm_rsp:\n" ship_confirm_rsp)
    ;(println "ship_accept_resp:\n" ship_accept_rsp)
    ) )
