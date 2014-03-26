(ns shipping.ups.xml.insured_shipping_test
  (:require [clj-http.client :as client]
            [clojure.data.xml :as x]
            [sistemi.config]
            [app.config :as c]
            [shipping.ups.xml.util :as u]
            [shipping.ups.xml.tools :as t]
            [shipping.ups.xml.modules :as m]
            [shipping.ups.xml.ship :as ship]
            [shipping.ups.xml.request_data :as rd]
            [shipping.ups.xml.ship_test :as st])
  (:use [clojure.test]))

(def insured-package-data
  "Package dimensions pulled from google docs/frinj/etc"
  {:packages
   (list
     {:type_code "02"
      :dimension_data {:unit_code "CM" :length "22" :width "20" :height "18"}
      :weight_data {:weight "14.1" :unit_code "KGS"}
      ;; verbal confirmation may not work --
      :service_data {:insurance {:currency_code "EUR" :value "50.00"}
                     :verbal_conf {:name "Eric Romeo" :phone "123456777"}} }
     )}
  )

(defn insured-request-data
  "Meged data from 'sistemi-data', 'customer-data', and 'package-data'"
  [config_access_info]
  (merge (rd/sistemi-data config_access_info) rd/customer-data insured-package-data) )


;(deftest test-simple-insured-shipment
;  (sistemi.config/init!)
;  (let [ups_access (c/conf :ups)
;        access_data (t/access-data-from-config ups_access)
;        insured_basic_data (insured-request-data access_data)
;        ship_confirm_rsp (ship/shipping-trans-part1 insured_basic_data access_data st/ship_confirm_test_url)
;        ]
;
;    (println "insured_data:\n" insured_basic_data)
;    (println "ship_confirm_rsp:\n" ship_confirm_rsp)
;    ) )