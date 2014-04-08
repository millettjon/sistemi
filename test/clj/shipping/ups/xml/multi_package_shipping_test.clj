(ns shipping.ups.xml.multi_package_shipping_test
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

;; Within zone
;; 1 package 150 x 48 x 18:                   EUR 44.15
;; 2 packages 150 x 48 x 18, 120 x 48 x 18:   EUR 54.01
;; 3 packages 150 x 48 x 18, 120 x 48 x 18,
;;   135 x 48 x 18:                           EUR 67.96
;; 4 pakcages 150 x 48 x 18, 120 x 48 x 18,
;;            135 x 48 x 18, 135 x 48 x 18    EUR 78.32

(def package2
  { :type_code "02"
    :dimension_data {:unit_code "CM" :length "120" :width "48" :height "18"}
    :weight_data {:weight "20.0" :unit_code "KGS"}
    :service_options '()
    :service_data {:insurance {:currency_code "EUR" :value "50.00"}
                   :verbal_conf {:name "Eric Romeo" :phone "123456777"}}

    })

(def package3
  { :type_code "02"
    :dimension_data {:unit_code "CM" :length "135" :width "48" :height "18"}
    :weight_data {:weight "20.0" :unit_code "KGS"}
    :service_options '()
    :service_data {:insurance {:currency_code "EUR" :value "50.00"}
                   :verbal_conf {:name "Eric Romeo" :phone "123456777"}}

    })

;(deftest test-packages
;  (sistemi.config/init!)
;  (let [ups_access (c/conf :ups)
;        access_data (t/access-data-from-config ups_access)
;        basic_data (rd/simple-insured-request-data access_data)
;        package1 (first (basic_data :packages))
;        ;packages_data basic_data
;        packages_data (assoc-in basic_data [:packages] (list package1 package2))
;        ;packages_data (assoc-in basic_data [:packages] (list package1 package2 package3))
;        ;packages_data (assoc-in basic_data [:packages] (list package1 package2 package3 package3))
;        ship_confirm_rsp (ship/shipping-trans-part1 packages_data access_data st/ship_confirm_test_url)
;        ;ship_accept_rsp (ship/shipping-trans-part2 ship_confirm_rsp access_data ship/ship_accept_test_url)
;        ]
;
;    (println "ship_confirm_rsp:\n" ship_confirm_rsp)
;    ;(println "ship_accept_resp:\n" ship_accept_rsp)
;    ) )

