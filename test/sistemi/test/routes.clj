(ns sistemi.test.routes
  (:use sistemi.routes
        clojure.test
        locale.core
        ring.mock.request))

(deftest routes-test
  (binding [default-locale "en"
            locales #{"en" "es"}]
    (are [args result] (= result
                          (let [m (routes (apply request args))
                                h :headers
                                hm (select-keys (m h) ["Location"])]
                            (assoc (select-keys m [:status h]) h hm)))
       [:get "/"]     {:status 302, :headers {"Location" "http://localhost/en"}}
       [:get "/foo"]  {:status 302, :headers {"Location" "http://localhost/en/foo"}}
       [:get "?abc"]  {:status 302, :headers {"Location" "http://localhost/en?abc"}}
       [:post "/foo"] {:status 302, :headers {"Location" "http://localhost/en"}}
       )
    (are [path status] (= (:status (routes (request :get path))) status)
       "/.test"    200
       "/en/.test" 200
       "/en/foo"   404
   )))
