(ns sistemi.routes-test
  (:use clojure.test
        sistemi.routes
        locale.core
        ring.mock.request))

(deftest routes-test
  (with-redefs [default-locale "en"
              locales #{"en" "es"}]
    (let [routes (build-routes)]
      (are [args result] (= result
                            (let [m (routes (apply request args))
                                  h :headers
                                  hm (select-keys (m h) ["Location"])]
                              (assoc (select-keys m [:status h]) h hm)))
           [:get "/"]     {:status 302, :headers {"Location" "http://localhost/en"}}
           [:get "/foo"]  {:status 404, :headers {}}
           [:post "/foo"] {:status 404, :headers {}}
           [:get "?abc"]  {:status 302, :headers {"Location" "http://localhost/en"}}
           )
      (are [path status] (= (:status (routes (request :get path))) status)
           "/.test"    200
           "/en/.test" 200
           "/en/foo"   404
      ))))
