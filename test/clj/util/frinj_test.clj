(ns util.frinj-test
  (:require [util.frinj :as f]
            [clojure.edn :as edn])
  (:use clojure.test
        [frinj.ops :only [fj]]))

(deftest reader
  (let [val1 (f/fj-eur 1.00M)
        s (pr-str val1)
        val2 (edn/read-string {:readers f/readers} s)
        ]
    ;; (prn s)
    ;; (prn val2)
    ;; (prn (type val2))
    (is (= val1 val2 ))))

(deftest fj-max-test
  (is (nil? (f/fj-max)))
  (is (= (fj 1 :m) (f/fj-max (fj 1 :m))))
  (is (= (fj 2 :m) (f/fj-max (fj 1 :m) (fj 2 :m))))
  (is (= (fj 2 :m) (f/fj-max (fj 2 :m) (fj 1 :m))))
  (is (= (fj 3 :m) (f/fj-max (fj 1 :m) (fj 3 :m) (fj 2 :m)))))

(deftest fj-sort-test
  (is (= [(fj 1 :m) (fj 2 :m) (fj 3 :m)]
         (f/fj-sort [(fj 2 :m) (fj 3 :m) (fj 1 :m)]))))
