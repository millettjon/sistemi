(ns sistemi.blog-test
  (:require [sistemi.blog :as b]
            [www.url :as u])
  (:use clojure.test))

(deftest url-from-blog-to-site
  (are [x y] (= (-> x b/url-from-blog-to-site str) y)
       "https://blog.sm1.in/en/blog?p=31"             "/en/blog?p=31"
       "https://blog.sm1.in/fr/blog/?p=31"            "/fr/blog?p=31"
       "https://blog.sm1.in/fr/blog/au-commencement"  "/fr/blog/au-commencement"))

(deftest url-site->blog
  (are [w x y z] (= (b/url-from-site-to-blog {:locale w :uri x :params y}) (u/new-URL z))
       "en" "/blog" {:p "31"}      "https://blog.sm1.in/en/blog?p=31"
       "fr" "/blog" {:p "31"}      "https://blog.sm1.in/fr/blog?p=31"
       "en" "/blog/3d-printing" nil "https://blog.sm1.in/en/blog/3d-printing"
       "fr" "/blog/au-commencement" nil "https://blog.sm1.in/fr/blog/au-commencement"))
