(ns sistemi.test.base62
  (:require [sistemi.base62 :as b62])
  (:use [clojure.test]))

(deftest encode
  (doseq [[s n] [["0" 0]
                 ["1" 1]
                 ["z" 61]
                 ["10" 62]]]
    (is (= s (b62/encode n)))))

(deftest rand_
  (doseq [n '(0 1 4)]
    (is (= n (count (b62/rand n))))))
