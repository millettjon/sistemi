(ns www.request-test
  (:require [www.request :as request])
  (:use [clojure.test]))

(deftest self-refered-test
  (is (request/self-referred?
       {:scheme :http :server-name "localhost" :server-port 5000
        :headers {"referer" "http://localhost:5000/foo"}}))

  (is (request/self-referred?
       {:scheme :http :server-name "sharp-river-7467.herokuapp.com" :server-port 80
        :headers {"referer" "http://sharp-river-7467.herokuapp.com/en/modern-shelving.htm"}}))  
  
  (is (not (request/self-referred?
            {:scheme :http :server-name "foo1"
             :headers {"referer" "http://foo2/foo"}}))))
