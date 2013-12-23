(ns sistemi.site.blog-htm
  (:require [sistemi.translate :as tr])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {})

(def strings
  {:en {}
   :es {}
   :it {}
   :fr {}
   })

(defn body
  []
  [:p "hello"])

(defn handle
  [req]
  (response (standard-page nil (body) 544)))
