(ns sistemi.test.routes
  (:use sistemi.routes
        clojure.test
        ring.mock.request))

(deftest routes-test
  (are [args result] (= (routes (apply request args)) result)
       [:get "/"]     {:status 302, :headers {"Location" "http://localhost/en"}, :body ""}
       [:get "/foo"]  {:status 302, :headers {"Location" "http://localhost/en/foo"}, :body ""}
       [:get "?abc"]  {:status 302, :headers {"Location" "http://localhost/en?abc"}, :body ""}
       [:post "/foo"] {:status 302, :headers {"Location" "http://localhost/en"}, :body ""}
       [:get "/"]     {:status 302, :headers {"Location" "http://localhost/en"}, :body ""})
  (are [path status] (= (:status (routes (request :get path))) status)
       "/.test"    200
       "/en/.test" 200
       "/en/foo"   404
   ))
