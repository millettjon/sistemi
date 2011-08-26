(ns www.test.url
  (:use [clojure.test]
        www.url))

(deftest canonicalize-test
  (let [dreq {:scheme :http :server-name "foo.com" :server-port 80}]
    (are [req uri result] (= (canonicalize (merge dreq req) uri) result)
         ;; request map                     uri    result
         {}                                 ""     "http://foo.com"
         {:server-port 8080}                ""     "http://foo.com:8080"
         {:scheme :https :server-port 443}  ""     "https://foo.com"
         {:scheme :https  :server-port 444} ""     "https://foo.com:444"
         {}                                 "/fr"  "http://foo.com/fr"
         {:scheme :https :server-port 444}  "/en"  "https://foo.com:444/en"
     )))
