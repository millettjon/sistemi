(ns paypal.test.address
  (:require [paypal.address :as address])
  (:use clojure.test))

(def GB-address
  {:shiptoname "Oliver Cromwell"
   :shiptostreet "1 Main Terrace"
   :shiptocity "Wolverhampton"
   :shiptostate "West Midlands"
   :shiptozip "W12 4LQ"
   :shiptocountrycode "GB"})

(deftest test-format
  ;; GB
  (is (= (address/format GB-address :shipto) ["Oliver Cromwell" "1 Main Terrace" "WOLVERHAMPTON"
                                          "WEST MIDLANDS" "W12 4LQ" "UNITED KINGDOM"])))


