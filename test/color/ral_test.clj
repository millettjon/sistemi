(ns color.ral_test
  (:use clojure.test
        color
        color.ral))

  ;; int value 12500351
  (deftest test-to-rgb
    (is (= (0xBEBD7F (to-rgb {:type :ral , :value 1000} ))))
    )