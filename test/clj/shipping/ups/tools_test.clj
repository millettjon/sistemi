(ns shipping.ups.tools_test
  (:require [shipping.ups.tools :as t])
  (:use [clojure.test]))

(deftest test-package-is-heavy
  (are [x y] (= x y)
    true (t/package-is-heavy? 26.5)
    false (t/package-is-heavy? 25)
    ) )

(deftest test-package-exceeds-max-weight
  (are [x y] (= x y)
    true (t/package-exceeds-max-weight? 70.1)
    false (t/package-exceeds-max-weight? 69.9)
    ) )

(deftest test-package-exceeds-max-length
  (are [x y] (= x y)
    true (t/package-exceeds-max-length? 271)
    false (t/package-exceeds-max-length? 269)
    ) )

(deftest test-package-calculate-girth
  (are [x y] (= x y)
    132 (t/package-calculate-girth 48 18)
    ) )

(deftest test-packate-calculate-ups-length
  (is (= 282 (t/package-calculate-ups-length 150 48 18))) )

; It seems that billing weight 26.0 kg is the max for normal pricing
(deftest test-package-calculate-billing-weight
  (is (= 25.92 (t/package-calculate-billing-weight 150 48 18))) )
