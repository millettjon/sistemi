(ns sistemi.site.blog-htm
  (:require [sistemi.translate :as tr]
            [sistemi.blog :as b])
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

(defn handle
  [req]
  (response (standard-page head (b/body req) (b/sidebar req))))
