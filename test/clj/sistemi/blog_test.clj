(ns sistemi.blog-test
  (:require [sistemi.blog :as b]
            [www.url :as u])
  (:use clojure.test))

(deftest url-blog->site
  (are [x y] (= (-> x b/url-blog->site str) y)
       "https://blog.sm1.in/?p=31"                    "/en/blog?p=31"
       "https://blog.sm1.in/?p=31&lang=fr"            "/fr/blog?p=31"
       "https://blog.sm1.in/au-commencement/?lang=fr" "/fr/blog/au-commencement"))

(deftest url-site->blog
  (are [w x y z] (= (-> {:locale w :uri x :params y} b/url-site->blog) (u/new-URL z))
       :en "/blog" {:p "31"}      "https://blog.sm1.in?lang=en&p=31"
       :fr "/blog" {:p "31"}      "https://blog.sm1.in?lang=fr&p=31"
       :en "/blog/3d-printing" {} "https://blog.sm1.in/3d-printing?lang=en"
       :fr "/blog/au-commencement" {} "https://blog.sm1.in/au-commencement?lang=fr"))