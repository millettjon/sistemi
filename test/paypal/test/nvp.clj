(ns paypal.test.nvp
  (:use clojure.test
        paypal.nvp))

(deftest test-santize
  (doseq [s (vals (sanitize {:user "foouser", :pwd "xyzzy", :signature "alsjfas;jfsa;fja;sdfasdfdsafs"}))]
    (is (= s "-redacted-")))
  (is (= "FOO" (:foo (sanitize {:foo "FOO"})))))
