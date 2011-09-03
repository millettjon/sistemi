(ns app.test.config
  (:use clojure.test
        app.config
        app.config.core))

(deftest test-conf
  (set-config! {:foo {:bar {:baz "BAZ"}} :quux "QUUX"})
  (is (= "QUUX" (conf :quux)))
  (is (= {:bar {:baz "BAZ"}} (conf :foo)))
  (is (= "BAZ" (conf :foo :bar :baz))))
