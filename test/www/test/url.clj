(ns www.test.url
  (:require [www.url :as url])
  (:use [clojure.test]))

(deftest encode-path-segment-test
  (are [s result] (= (url/encode-path-segment s) result)
       ""           ""
       "estantería" "estanter%C3%ADa"))

(deftest encode-path-test
  (are [s result] (= (url/encode-path s) result)
       "estantería" "estanter%C3%ADa"
       "/" "/"
       "" nil
       "abc/def" "abc/def"
       "/abc/def" "/abc/def"))

(deftest encode-query-segment-test
  (are [s result] (= (url/encode-query-segment s) result)
       ""           ""
       "estantería" "estanter%C3%ADa"))

(deftest encode-query-test
  (are [s result] (= (url/encode-query s) result)
       {:a23 "bí"} "a23=b%C3%AD"))

(deftest new-URL-string-test
  (are [s result] (= (url/new-URL s) (url/new-URL result))
       ""  {:path "/"}
       "/" {:path "/"}
       "abc" {:path "abc"}
       "/foo" {:path "/foo"}
       "http://foo:8080/bar" {:scheme :http :host "foo" :port 8080 :path "/bar"}
       "http://foo/bar" {:scheme :http :host "foo" :path "/bar"}
       "https://foo/bar/baz" {:scheme :https :host "foo" :path "/bar/baz"}
       "https://foo/bar?baz=qux" {:scheme :https :host "foo" :path "/bar" :query {:baz "qux"}}))

(deftest new-URL-ring-map-test
  (are [m result] (= (url/new-URL m) (url/new-URL result))
       {:server-name "foo"} "//foo"
       {:scheme :https :server-name "foo"} "https://foo"
       {:scheme :https :server-name "foo" :server-port 8118} "https://foo:8118"
       {:scheme :https :server-name "foo" :server-port 8118 :uri "/bar"} "https://foo:8118/bar"
       {:scheme :https :server-name "foo" :server-port 8118 :uri "/bar" :query-params {:baz "qux"}} "https://foo:8118/bar?baz=qux"))
 
(deftest to-string-test
  (are [s] (= (str (url/new-URL s)) s)
       "/"
       "abc"
       "/foo"
       "//foo"
       "http://foo:8080/bar"
       "http://foo/bar"
       "https://foo/bar/baz"
       "https://foo/bar?baz=qux"
       )
  (are [s result] (= (str (url/new-URL s)) result)
       "" "/"
       "http://foo:80/bar" "http://foo/bar"
       "http://fubar.com/a/b/estantería?target=/estantería-moderna.htm" "http://fubar.com/a/b/estanter%C3%ADa?target=/estanter%C3%ADa-moderna.htm"
       ))

(deftest qualify-test
  (are [a b c] (= (str (url/qualify a b)) c)
       "bar" "http://foo" "http://foo/bar"
       "bar/baz?qux=42" "http://foo" "http://foo/bar/baz?qux=42"
       "baz" "http://foo/bar" "http://foo/bar/baz"
       "baz" "https://foo:8118/bar" "https://foo:8118/bar/baz"
       ))

(deftest localize-test
  (let [req {:luri #(str "/en" %)}]
    (are [a b c] (= (str (url/localize a (merge req b))) c)
         "bar" {:uri "/foo"} "/en/bar"
         "baz" {:uri "/foo/bar"} "/en/foo/baz"
         "/baz" {:uri "/foo/bar"} "/en/baz"
         "baz" {:uri "/"} "/en/baz"
         "view.htm?id=abc" {:uri "/order/pay.htm"} "/en/order/view.htm?id=abc")))
