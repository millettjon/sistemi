(ns sistemi.test.translate
  (:use clojure.test
        sistemi.translate))

(deftest mangle-text-to-kw-test
  (are [a b] (= (mangle-text-to-kw a) b)
       "abc"   :abc
       "a b c" :a-b-c
       "AB C"  :ab-c))
