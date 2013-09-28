(ns color.ral_test
  (:require [color.ral :as ral])
  (:use clojure.test
        color))

  ;; int value 12500351
  (deftest test-to-rgb
    (is (= 0xBEBD7F (color/to-rgb {:type :ral :value 1000})))
    )