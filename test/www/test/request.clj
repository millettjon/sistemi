(ns www.test.request
  (:require [www.request :as request])
  (:use [clojure.test]))

(deftest self-refered-test
  (is (request/self-referred?
       {:scheme :http :server-name "localhost" :server-port 5000
        :headers {"referer" "http://localhost:5000/foo"}}))
  (is (not (request/self-referred?
       {:scheme :http :server-name "foo1"
        :headers {"referer" "http://foo2/foo"}}))))
