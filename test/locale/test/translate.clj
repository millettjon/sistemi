(ns locale.test.translate
  (:require [util.path :as path])
  (:use [clojure.test]
        locale.translate))

(def lm
  "Localization path map for testing."
  {"es"
   {:name "es"
    "order" {:name "orden"
             "pay" {:name "pagar"}}}})
(def cm
  "Canonicalization paht map for testing."
  {"es"
   {"orden"
    {:name "order"
     "pagar"
     {:name "pay"}}}})

(def req
  {:locale "es" :localized-paths lm :canonical-paths cm})

(deftest translate-path-test
  (are [a b] (= (translate-path lm (path/new-path a)) (if b (path/new-path b)))
       "/es" "/es"
       "/es/order" "/es/orden"
       "/es/order/pay" "/es/orden/pagar"
       "/es/foo" nil
       "/es/foo/bar" nil))

(deftest localize-path-test
  (are [a b] (= (localize-path lm "es" (path/new-path a)) (if b (path/new-path b)))
       "/" "/es"
       "/order" "/es/orden"
       "/order/pay" "/es/orden/pagar"
       "/foo" nil
       "/foo/bar" nil))

(deftest canonicalize-path-test
  (are [a b] (= (canonicalize-path cm "es" (path/new-path a)) (if b (path/new-path b)))
       "/" "/"
       "/orden" "/order" 
       "/orden/pagar" "/order/pay"
       "/foo" nil
       "/foo/bar" nil))

(deftest localize-test
  (are [a b] (= (localize a req) b)
       "/foo" nil
       "/order" "/es/orden"
       "/order/pay" "/es/orden/pagar")
  (are [a b c] (= (apply localize a req b) c)
       "/foo" [] nil
       "/order" [:query {:foo "bar"}] "/es/orden?foo=bar"))

(deftest canonicalize-test
  (are [a b] (= (canonicalize a req) b)
       "/foo" nil
       "/es/orden" "/order"
       "/es/orden/pagar" "/order/pay"))
