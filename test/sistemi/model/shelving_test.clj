(ns sistemi.model.shelving-test
  (:use clojure.test
        sistemi.model
        sistemi.model.shelving
        frinj.calc))

;; Note: There is some unknown interaction between frinj and
;; clojure-test-mode. Calling frinj-init! here and in the associated
;; source namespace fixes it.
(frinj-init!)
;;
;; This works fine and converts to meters before running tests.
#_ (fj 120 :cm)
;;
;; After running tests w/ C-c , it no longer normalizes to meters
;; and just leaves it in cm.

(def shelving
  {:height (fj 120 :cm)
   :width  (fj 120 :cm)
   :depth  (fj 39 :cm)
   :cutout :ovale
   :finish :laquer-matte
   :material :mdf-ecological
   :color 0xAB003B})

#_ (deftest test-price
  (is (price shelving)
      (fj 479.35M :EUR)))

#_ (deftest test-price-report
  (price-report shelving))

#_ (deftest test-html-price-report
  (html-price-report shelving))

#_ (deftest test-pprint-price-report
  (pprint-price-report shelving))
