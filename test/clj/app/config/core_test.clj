(ns app.config.core-test
  (:use clojure.test
        app.config.core))

(deftest environment-test
  (is (map? (environment)))
  (is (= (environment "USER") {:user (get (System/getenv) "USER")})))

;; TODO: lookup the location of this file...
(deftest file-map-test
  (is (= (file-map "test/clj/app/config/test/config.edn") {:foo "bar"})))

;; (defn foo
;;   []
;;   (prn "*file*=" *file*)
;;   (prn "*compile-path*=" *compile-path*)
;;   )

;(foo) ; only works during compile. shucks
;; "/home/jam.sm/sm/www/test/app/config/test/init.clj"
;; how to figure out wh
;; (println (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader))))

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
    (is (= (apply merge-configs maps) result))))
