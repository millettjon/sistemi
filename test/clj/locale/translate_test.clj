(ns locale.translate-test
  (:require [util.path :as path])
  (:use [clojure.test]
        [locale.translate :except (strings)]))

(def lm
  "Localization path map for testing."
  {"order" {:en "order"
            :es "orden"
            "create.htm" {:en "create.htm"
                          :es "crear.htm"}}
   "vision.htm" {:en "vision.htm",
                 :es "vis%C3%ADon.htm"}})

(def cm
  "Canonicalization path map for testing."
  {:es {"orden" {"crear.htm" {:name "create.htm"}
                 :name "order"}
        "vis%C3%ADon.htm" {:name "vision.htm"}}
   :en {"order" {"create.htm" {:name "create.htm"}
                 :name "order"}
        "vision.htm" {:name "vision.htm"}}})

(def req
  {:locale "es" :uri "/order/foo"})

(deftest localize-path-test
  (are [a b c] (= (localize-path a lm b) (if c (path/new-path c)))
       "/" :es "/es"
       "/order" :es "/es/orden"
       "/order" :en "/en/order"
       "/order/create.htm" :en "/en/order/create.htm"
       "/order/create.htm" :es "/es/orden/crear.htm"
       "/foo" :es nil
       "/foo/bar" :es nil
       "/vision.htm" :en "/en/vision.htm"
       "/vision.htm" :es "/es/vis%C3%ADon.htm"))

(deftest canonicalize-path-test
  (are [a b c] (= (canonicalize-path a cm b) (if c (path/new-path c)))
       "/" :en "/"
       "/" :es "/"
       "/" :foo nil
       "/order" :foo nil
       "/orden" :es "/order" 
       "/order" :en "/order" 
       "/foo" :en nil
       "/foo/bar" :es nil
       "/vision.htm" :en "/vision.htm"
       "/vis%C3%ADon.htm" :es "/vision.htm"
       "/orden/crear.htm" :es "/order/create.htm"))

(deftest localize-test
  (are [a b] (= (localize a lm req) b)
       "/foo" "/foo"
       "/order" "/es/orden"
       "/order/create.htm" "/es/orden/crear.htm"
       "/order/create.htm" "/es/orden/crear.htm"
       "create.htm" "crear.htm"
       "create.htm?foo=bar" "crear.htm?foo=bar"
       "/" "/es"
       "" ""
       "#" "#"
       "bad" "bad")
 (are [a b c] (= (apply localize a lm req b) c)
       "/foo" [] "/foo"
       "/order" [:query {:foo "bar"}] "/es/orden?foo=bar"
       "/order" {:query {:foo "bar"}} "/es/orden?foo=bar"
       ))

(deftest canonicalize-test
  (are [a b] (= (canonicalize a cm req) b)
       "/foo" nil
       "/es/foo" nil
       "/es/orden" "/order"
       "/es/orden/crear.htm" "/order/create.htm"))

;; path keys are strings
;; under each key is a map
;; each map has a keyword key for each locale
(def strings
  {:en
   {:foo "FOO"
    :this "/"}
  "bar"
   {:en
    {:bar "BAR"
     :this "/bar"
     :baz {:_ "BAZ" :qux "QUX"}}}})

(deftest translate-test
  (are [a b] (= (apply translate strings :en a) b)
       ["/" :foo]         "FOO"
       ["" :foo]          "FOO"
       ["/" :bad]         "(TODO translate / (:bad))"     ; not found
       ["/bad" :bad]      "(TODO translate /bad (:bad))"  ; not found
       ["/bar" :bar]      "BAR"       ; path
       ["bar" :bar]       "BAR"       ; relative path
       ["/bar" :foo]      "FOO"       ; inheritance
       ["/" :this]        "/"         ; overide
       ["/bar" :this]     "/bar"      ; overide
       ["/bar" :baz]      "BAZ"       ; nested, self
       ["/bar" :baz :qux] "QUX"       ; nested
       ["/bar" :baz :bad] "(TODO translate /bar (:baz :bad))" ; nested not found
       ))

