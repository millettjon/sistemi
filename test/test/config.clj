(ns test.config
  (:use [clojure.test]
        config))

(deftest environment-map-test
  (is (map? (environment-map)))
  (is (= (environment-map "USERNAME") {:username (get (System/getenv) "USERNAME")})))

(deftest file-map-test
  (is (= (file-map "test/test/config.yaml") {:foo "bar"})))

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
    (is (= (apply merge-configs maps) result))))
