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
       ["a"]        ["a"]
       ["a/b"]      ["a" "b"]
       ["a/b" "c"]  ["a" "b" "c"]
       ))

(deftest test-parent
  (are [a b] (= (apply parent a) b)
       []           nil
       ["a"]        nil
       ["a/b"]      "a"
       ["a/b/c"]   "a/b"
       ))
