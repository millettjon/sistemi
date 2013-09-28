(ns util.core-test
  (:use clojure.test
        util.core))

(deftest test-contains-in?
  (are [coll ks result] (= result (contains-in? coll ks))
       {} [:a] false
       {:a "a"} [:a] true
       {:a {:b nil}} [:a] true
       {:a {:b nil}} [:a :b :c :d] false
       {:a {:b nil}} [:a :b] true)
  (is (thrown? IllegalArgumentException (contains-in? {} []))))
