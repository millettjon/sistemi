(ns sistemi.site.contact-htm
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

(def head
  [:link {:rel "stylesheet" :href "css/blog.css" :type "text/css"}])

(defn body
  [req]
  [:p "Hello"])

(defn handle
  [req]
  (response (standard-page nil (body req))))
