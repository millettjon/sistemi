(ns util.path-test
  (:use util.path
        clojure.test)
  (:import (java.io File))
  (:refer-clojure :exclude [first last rest name]))

(deftest test-new-path
  (are [a] (= a (str (new-path a)) (str (new-path (File. a))))
       ""
       "/"
       "/a"
       "/abc/def"
       "abc"
       "abc/def"))

(deftest test-split
  (are [a b] (= (split a) b)
       "" []
       "a" ["a"]
       "a/b" ["a" "b"]
       "/a/b/c" ["a" "b" "c"]))

(deftest test-join
  (are [a b] (= (str (apply join a)) b)
       []          ""
       [""]        ""
       ["a"]       "a"
       ["a" "b"]   "a/b"
       ["a////b"]  "a/b"
       ["a/" "/b"] "a/b"
       ["/"]       "/"
       ["a/"]      "a"
       ["a/b/"]    "a/b"))

(deftest extension-test
  (are [a b] (= (extension a) b)
       "" nil
       "foo" nil
       "foo.clj" "clj"
       "a/b" nil
       "a/b.c" "c"))

(deftest basename-test
  (are [a b] (= (basename a) b)
       "" nil
       "foo" "foo"
       "foo.htm" "foo"
       "foo.bar.htm" "foo.bar"))

(deftest test-parent
  (are [a b] (= (str (parent a)) b)
       ""         ""
       "/"        "/"
       "/a"       "/"
       "/a/b"     "/a"
       "a"        ""
       "a/b"      "a"
       "a/b/c"   "a/b"))

(deftest test-first
  (are [a b] (= (first a) b)
       "" nil
       "/" nil
       "/abc" "abc"
       "abc" "abc"
       "ab/cd/ef" "ab"
       "/ab/cd/ef" "ab"))

(deftest test-last
  (are [a b] (= (last a) b)
       "" nil
       "/" nil
       "/abc" "abc"
       "abc" "abc"
       "ab/cd/ef" "ef"
       "/ab/cd/ef" "ef"))

(deftest test-rest
  (are [a b] (= (rest a) (and b (new-path b)))
       "" "" ; this is a consequence of the fact that: (rest '()) --> '()
       "/" ""
       "/abc" ""
       "abc" ""
       "ab/cd/ef" "cd/ef"
       "/ab/cd/ef" "cd/ef"))

(deftest test-relative?
  (are [a b] (= (relative? a) b)
       ""        true
       "abc"     true
       "abc/def" true
       "/a"      false
       "/a/b"    false
       "/"       false))

(deftest test-qualify
  (are [a b] (= (str (apply qualify a)) b)
       ["/a"] "/a"
       ["a"] "/a"
       ["abc" "def"] "def/abc"
       ["abc" "/def"] "/def/abc"
       ["/abc" "def"] "/abc"
       ["a/b" "/c"] "/c/a/b"
       ["a/b" "/c/"] "/c/a/b"))

(deftest test-unqualify
  (are [a b] (= (str (apply unqualify a)) b)
       [""] ""
       ["/"] ""
       ["a"] "a"
       ["/a"] "a"
       ["/foo/bar" "/foo"] "bar"
       ["/foo/bar/baz" "/foo/bar/baz"] ""
       ["/foo/bar/baz/qux" "/foo/bar"] "baz/qux"
       ["/foo/bar/baz" "/foo/bar/qux"] "baz"))

(deftest test-unqualify
  (are [a b c] (= (str (sibling a b)) c)
       "a" "b"        "b"
       "/a" "b"       "/b"
       "a/b" "c"      "a/c"
       "/a/b" "c"     "/a/c"
       "/a/b" "c/d"   "/a/c/d"))
