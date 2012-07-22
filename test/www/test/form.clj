(ns www.test.form
  (:require [clojure.tools.logging :as log])
  (:require [www.form :as f])
  (:require [sistemi.form :as sf])
  (:use [clojure.test]))


(def shelving
  {:cutout {:type :set :options [::semplice ::ovale ::quadro] :default ::semplice}})

(def params
  {:cutout ::semplice})

(deftest test-set
  (are [cutout]
       (f/with-form shelving {:cutout (str cutout)}
         (is (not (f/errors?))))
       ::semplice
       ::ovale
       ::quadro)
  (are [cutout]
       (f/with-form shelving {:cutout (str cutout)}
         (is (f/errors?)))
       ::semplicez
       "bogus"
       ":nonexistant/foo"))

(deftest test-parse-keyword
  (are [s result] (= result (f/parse-keyword s))
       ":fubar" nil
       (str ::semplice) ::semplice
       ":foo" :foo
       ))
