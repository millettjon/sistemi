(ns app.config-test
  (:use clojure.test
        app.config))

(deftest test-conf
  (set-config! {:foo {:bar {:baz "BAZ"}} :quux "QUUX"})
  (is (= "QUUX" (conf :quux)))
  (is (= {:bar {:baz "BAZ"}} (conf :foo)))
  (is (= "BAZ" (conf :foo :bar :baz))))


;; TODO: lookup the location of this file...
(deftest handle-conf-test
  (is (= (get-in (#'app.config/handle-conf {:files ["test/clj/app/data/config.edn"]} "test/clj/app/data/config.edn") [:conf :config]) {:foo "bar"})))

;; TODO: Use are
(deftest merge-configs-test
  (doseq [[maps result]
          (partition 2 [
            ;; test args                                                    expected result
            [{} {}]                                                         {}
            [{:a "a"} {:b "B"}]                                             {:a "a" :b "B"}
            [{:a "a" } {:a "A"}]                                            {:a "A"}
            [{:a "a" :c "c"} {:b "B" :c "C"}]                               {:a "a" :b "B" :c "C"}
            [{} {:a {}}]                                                    {:a {}}
            [{:a {}} {:a {}}]                                               {:a {}}
            [{:a {:b "b"}} {:a {:b "B" :c "C"}}]                            {:a {:b "B" :c "C"}}
            [{:a {:a "a" :b "b"}} {:a {:b "B" :c "C"}}]                     {:a {:a "a" :b "B" :c "C"}}
            [{:a {:a "a" :b "b" :d {}}} {:a {:b "B" :c "C" :d {:d1 "D1"}}}] {:a {:a "a" :b "B" :c "C" :d {:d1 "D1"}}}
            [{:a {:a "a" :b "b" :d {}}} {:a {:b "B" :c "C" :d :D}}]         {:a {:a "a" :b "B" :c "C" :d :D}}
            [{:a {:a "a" :b "b" :d {}}} {:a 1}]                             {:a 1}
            ])]
    (is (= (apply #'app.config/merge-configs maps) result))))
