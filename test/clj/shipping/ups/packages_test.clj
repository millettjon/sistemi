(ns shipping.ups.packages_test
  (:require [shipping.ups.packages :as p])
  (:use [clojure.test]))

(deftest test-package-is-heavy
  (are [x y] (= x y)
    true (p/package-is-heavy? 26.5)
    false (p/package-is-heavy? 25)
    ) )

(deftest test-package-exceeds-max-weight
  (are [x y] (= x y)
    true (p/package-exceeds-max-weight? 70.1)
    false (p/package-exceeds-max-weight? 69.9)
    ) )

(deftest test-package-exceeds-max-length
  (are [x y] (= x y)
    true (p/package-exceeds-max-length? 271)
    false (p/package-exceeds-max-length? 269)
    ) )

(deftest test-package-calculate-girth
  (are [x y] (= x y)
    132 (p/package-calculate-girth 48 18)
    ) )

(deftest test-packate-calculate-ups-length
  (is (= 282 (p/package-calculate-ups-length 150 48 18))) )

; It seems that billing weight 26.0 kg is the max for normal pricing
(deftest test-package-calculate-billing-weight
  (is (> 26.0 (p/package-calculate-billing-weight 150 48 18))) )
