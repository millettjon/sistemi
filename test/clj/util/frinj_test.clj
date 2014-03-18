(ns util.frinj-test
  (:require [util.frinj :as f]
            [clojure.edn :as edn])
  (:use clojure.test))

(deftest reader
  (let [val (f/fj-eur 1.00M)
        s (pr-str val)]
    (is (= val (edn/read-string {:readers f/readers} s)))))
