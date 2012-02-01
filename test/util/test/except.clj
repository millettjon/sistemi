(ns util.test.except
  (:use util.except
        clojure.test))

(deftest test-safely
  (are [x y] (= y (safely x :failed))
       :worked              :worked
       (throw (Exception.)) :failed)
  (are [a b] (= (safely a) b)
       42 42
       nil nil
       (throw (Exception.)) nil))

(deftest test-swallow
  (is (= :worked (swallow :worked)))
  (let [x (swallow (Exception. "fubar"))]
    (is (instance? Exception x))
    (is (= "fubar" (. x getMessage)))))

(deftest test-die
  (is (thrown-with-msg? RuntimeException #"bad news" (die "bad news")))
  (is (thrown-with-msg? RuntimeException #"really bad news" (die "~A bad news" "really"))))

(deftest test-check
  (testing "one arg"
    (is (= true (check true)))
    (is (= "abc" (check "abc")))
    (is (thrown-with-msg? RuntimeException #"Check failed:" (check false))))
  (testing "custom validator"
    (is (= 1 (check 1 pos?)))
    (is (thrown-with-msg? RuntimeException #"Check failed:" (check 0 pos?))))
  (testing "custom validator and message"
    (is (= :de (check :de #{:en :es :de :fr} "I don't know that language.")))
    (is (thrown-with-msg? RuntimeException #"I don't know that language." (check :zz #{:en :es :de :fr} "I don't know that language.")))
    (is (thrown-with-msg? RuntimeException #"I don't speak ':zz'." (check :zz #{:en :es :de :fr} "I don't speak '~A'. Are you asleep?")))
    (let [langs #{:en :es :de :fr}]
      (is (thrown-with-msg? RuntimeException #"I don't speak ':zz'. I do speak :en, :es, :de, :fr." (check :zz #{:en :es :de :fr} "I don't speak '~A'. I do speak ~{~a~^, ~}." langs)))
      ))
  (testing "custom message"
    (is (= 42 (check 42 "Apparently that is not the answer to life the universe and everything.")))
    (is (thrown-with-msg? RuntimeException #"I am the eggman." (check nil "I am the eggman.")))
    (is (thrown-with-msg? RuntimeException #"Am I the eggman\? false\."
          (check false "Am I the eggman? ~A.")))
    (is (thrown-with-msg? RuntimeException #"Am I the eggman\? false \(goo goo g'joob\)\."
          (check false "Am I the eggman? ~A (~A)." "goo goo g'joob")))))
