(ns util.test.fs
  (:use util.fs
        clojure.test))

(deftest test-fs
  (are [a b] (= (apply fs a) b)
       []          nil
       [nil]       nil
       ["a"]       "a"
       ["a" "b"]   "a/b"
       ["a////b"]  "a/b"
       ["a/" "/b"] "a/b"
       ["/"]       "/"
       ["a/"]      "a"
       ["a/b/"]    "a/b"
       ))

(deftest test-ffs
  (are [a b] (= (apply ffs a) b)
       []            nil
       [nil]         nil
       ["a"]        "/a"
       ["/a"]        "/a"
       ["a//b" "c"]  "/a/b/c"
       ["/a/b" "c"]  "/a/b/c"
       ))
(ffs "a")

(deftest test-fs-seq
  (are [a b] (= (apply fs-seq a) b)
       []           nil
       [nil]        nil
       [""]         nil
       ["/"]        nil
       ["/a"]       ["a"]
       ["a"]        ["a"]
       ["a/b"]      ["a" "b"]
       ["a/b" "c"]  ["a" "b" "c"]
       ))

(deftest test-parent
  (are [a b] (= (apply parent a) b)
       []           nil
       [""]         nil
       ["/a"]       "/"
       ["a"]        nil
       ["a/b"]      "a"
       ["a/b/c"]   "a/b"
       ))

(deftest test-relative?
  (are [a b] (= (relative? a) b)
       ""        true
       "abc"     true
       "abc/def" true
       "/a"      false
       "/a/b"    false
       "/"       false
       )
  (is (thrown? NullPointerException (relative? nil))))

(deftest test-fs-rest
  (are [a b] (= (fs-rest a) b)
       nil     nil
       ""      nil
       "/"     nil
       "/a"    nil
       "a"     nil
       "/a/b"  "/b"
       "/a/b/" "/b"
       "a/b"   "b"
       )
  )