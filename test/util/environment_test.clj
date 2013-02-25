(ns util.environment-test
  (:use util.environment
        clojure.test))

(deftest test-environment
  (is (map? environment))
  (is (= (environment "USERNAME") (get (System/getenv) "USERNAME"))))
