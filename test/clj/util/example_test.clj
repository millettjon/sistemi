(ns util.example-test
  (:use clojure.test))

(deftest test-defexample
  (remove-ns 'util.example-test.plus)
  (require 'util.example-test.plus :reload)

  (testing "add an example to a function"
    (is (= '{example1 ["Add 2 and 3." 5 (plus 2 3)]}
           ((meta (find-ns 'util.example-test.plus)) :examples))))

  (remove-ns 'util.example-test.plus-test)
  (require 'util.example-test.plus-test :reload)

  (is (contains? (meta (ns-resolve 'util.example-test.plus-test 'example1)) :test)))

;; test adding an example to a function
;; test adding an example to a macro
;; test adding an example to a namespace
;; test adding two examples
;; test that defining the same example twice doesn't create two copies
;; test bad parameters
;; target must exist and be a function/macro/namespace
;; heading is optional
;; name must be a valid unqualified symbol
;; when importing examples as tests
;;   - the namespace must exist
;;   - at least one example must be defined
;;   - test that a good test passes
;;   - test that a bad test fails
