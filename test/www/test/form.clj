(ns www.test.form
  (:require [clojure.tools.logging :as log])
  (:require [www.form :as f])
  (:require [sistemi.form :as sf])
  (:use [clojure.test]))


(def shelving
  {:cutout {:type :set :options [:semplice :ovale :quadro] :default :semplice}})

(def string-select
  {:foo {:type :set :options ["a" "b"] :default "a"}})

(def label-form
  {:foo {:type :set :options [:a {:label "A"} :b] :default :b}})

(def format-form
  {:foo {:type :set :options [:a :b] :default :b
         :format #(clojure.string/upper-case (name %))}})

(deftest test-set
  (are [cutout]
       (f/with-form shelving {:cutout (str cutout)}
         (is (not (f/errors?))))
       :semplice
       :ovale
       :quadro)
  (are [cutout]
       (f/with-form shelving {:cutout (str cutout)}
         (is (f/errors?)))
       :semplicez
       "bogus"
       ":nonexistant/foo"))

(deftest test-parse-keyword
  (are [s result] (= result (f/parse-keyword s))
       ":fubar" nil
       (str :semplice) :semplice
       ":foo" :foo
       ))

(deftest render-select
  ;; if a format fn is passed, format the text and put the key in the value.
  (f/with-form format-form {:foo :a}
    (let [[_ _ [[_ {value :value} text]]] (f/select :foo {})]
      (is (= value ":a"))
      (is (= text "A"))))

  ;; else if the key is a keyword, use it's name for the text
  (f/with-form shelving {:cutout :semplice}
    (let [[_ _ [[_ {value :value} text]]] (f/select :cutout {})]
      (is (= value ":semplice"))
      (is (= text "semplice"))))

  ;; else just use the key directly.
  (f/with-form string-select {:foo "a"}
    (let [[_ _ [[_ _ text]]] (f/select :foo {})]
      (is (= text "a")))))

#_ [:select {:name "cutout", :id "cutout"} ([:option {:value ":semplice", :selected true} "semplice"] [:option {:value ":ovale"} "ovale"] [:option {:value ":quadro"} "quadro"])]
