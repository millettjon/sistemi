(ns harpocrates.core-test
  (:use [clojure.test]
        harpocrates.core
        [util.pojometa :only [meta* with-meta*]]))

(deftest classify-test
  (is (secret? (:a (classify {:a "A"}))))
  (let [m (classify {:a "A" :b {:c "C"}})]
    (is (secret? (:a m)))
    (is (not (secret? (:b m))))
    (is (secret? (get-in m [:b :c])))
    ))

(deftest redact-test
  (are [a b] (= (redact a) b)
       {} {}
       {:password "xyzzy"} {:password "xyzzy"}
       (classify {:password "xyzzy"}) {:password "-redacted-"}
       (classify {:a "A" :b {:c "C"}}) {:a "-redacted-" :b {:c "-redacted-"}}
       ))

(deftest decrypt-test
  (let [dir "test/harpocrates/test/"
        m (-> (str dir "credentials.clj.gpg")
              (decrypt :passphrase "xyzzy" :home (str dir ".gnupg")))]
    (is (= m {:user "adventurer" :password "plover"}))
    (is (-> m :password secret?))
    (is (= "-redacted-" (-> m redact :password)))
    ))
