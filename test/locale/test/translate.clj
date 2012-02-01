(ns locale.test.translate
  (:require [util.path :as path])
  (:use [clojure.test]
        [locale.translate :except (strings)]))

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
  {:locale "es" :localized-paths lm :canonical-paths cm :uri "/order/foo"})

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
       "/foo" "/foo"
       "/order" "/es/orden"
       "/order/pay" "/es/orden/pagar"
       "pay" "pagar"
       "pay?foo=bar" "pagar?foo=bar"
       "/" "/es"
       "" ""
       "#" "#"
       "bad" "bad"
       )
  (are [a b c] (= (apply localize a req b) c)
       "/foo" [] "/foo"
       "/order" [:query {:foo "bar"}] "/es/orden?foo=bar"))

(deftest canonicalize-test
  (are [a b] (= (canonicalize a req) b)
       "/foo" nil
       "/es/orden" "/order"
       "/es/orden/pagar" "/order/pay"))

(def strings
  {:en
   {:foo "FOO"
    :this "/"}})

(require 'locale.test.translate.bar :reload)
(deftest translate-test
  (are [a b] (= (apply translate "locale.test.translate" :en a) b)
       ["/" :foo]         "FOO"
       ["" :foo]          "FOO"
       ["/" :bad]         "(bad)"     ; not found
       ["/bad" :bad]      "(bad)"     ; not found (no ns)
       ["/bar" :bar]      "BAR"       ; path
       ["bar" :bar]       "BAR"       ; relative path
       ["/bar" :foo]      "FOO"       ; inheritance
       ["/" :this]        "/"         ; overide
       ["/bar" :this]     "/bar"      ; overide
       ["/bar" :baz :qux] "QUX"       ; nested
       ["/bar" :baz :bad] "(baz-bad)" ; nested not found
       ))

(deftest mangle-path-to-ns-test
  (are [a b] (= (mangle-path-to-ns a) b)
       "foo" "foo"
       "foo.htm" "foo-htm"))
