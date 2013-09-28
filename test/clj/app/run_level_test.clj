(ns app.run-level-test
  (:require [util.environment :as env])
  (:use clojure.test
        app.run-level))

(defn- resolve-predicate
  "Takes a level keyword and returns the var for its predicate function."
  [level]
  (ns-resolve 'app.run-level (symbol (str (name level) "?"))))

(deftest test-init-run-level!
  (testing "environment"
    (with-redefs [env/environment {"RUN_LEVEL" "staging"}]
      (init-run-level!)
      (is (= run-level :staging))))
  (testing "no args"
    (init-run-level!)
    (is (= run-level :development))
    (is ((resolve-predicate :development)))
    (is (not ((resolve-predicate :staging))))
    (is (not ((resolve-predicate :production)))))
  (testing "one arg: level"
    (init-run-level! :staging)
    (is (= run-level :staging))
    (is ((resolve-predicate :staging))))
  (testing "one arg: levels"
    (init-run-level! #{:development :office :demo :live})
    (is (= run-level :development))
    (is (not ((resolve-predicate :office)))))
  (testing "two args"
    (init-run-level! :bar #{:foo :bar :baz})
    (is (= run-level :bar))
    (is ((resolve-predicate :bar)))
    (is (thrown? RuntimeException #"Check failed" (init-run-level! :quux #{:foo :bar :baz})))
    (is (thrown? RuntimeException #"Check failed" (init-run-level! :a "a b c"))))
  (testing "predicates"
    (are [level] (re-find (re-pattern (str "run level is " level))
                          (:doc (meta (resolve-predicate level))))
         :foo
         :bar
         :baz)))
