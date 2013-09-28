(ns util.string-test
  (:use clojure.test
        util.string))

(deftest triml-lines-test
  (are [a b] (= (triml-lines a) b)
       "a" "a"
       "a\nb" "a\nb"
       "  a\n    b" "a\nb"))

(deftest join-lines-test
  (are [a b] (= (join-lines a) b)
       "a" "a"
       "a\nb" "a b"
       "  a\n    b" "a b"))
