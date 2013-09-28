(ns upu-test
  (:require [upu :as upu])
  (:use clojure.test))

(def GB-address
  {:name "Oliver Cromwell"
   :street "1 Main Terrace"
   :city "Wolverhampton"
   :county "West Midlands"
   :code "W12 4LQ"
   :country "GB"})

(deftest test-GB
  ;; with :county
  (is (= (upu/GB GB-address) ["Oliver Cromwell" "1 Main Terrace" "WOLVERHAMPTON"
                              "WEST MIDLANDS" "W12 4LQ" "UNITED KINGDOM"]))
  ;; without :county
  (is (= (upu/GB (dissoc GB-address :county)) ["Oliver Cromwell" "1 Main Terrace" "WOLVERHAMPTON"
                                               "W12 4LQ" "UNITED KINGDOM"])))

(deftest test-format
  (is (= (upu/format GB-address) ["Oliver Cromwell" "1 Main Terrace" "WOLVERHAMPTON"
                                  "WEST MIDLANDS" "W12 4LQ" "UNITED KINGDOM"])))
