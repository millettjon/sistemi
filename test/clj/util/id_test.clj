(ns util.id-test
  (:require [util.id :as id])
  (:use clojure.test))

(deftest encode-test
  (doseq [[s n] [["0" 0]
                 ["1" 1]
                 ["z" 61]
                 ["10" 62]]]
    (is (= s (id/encode-62 n)))))

(deftest rand-test
  (doseq [n '(0 1 4)]
    (is (= n (count (id/rand-62 n))))))
