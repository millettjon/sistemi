(ns git-test
  (:require [git :as git])
  (:use clojure.test))

(deftest test-sha
  (git/sha))
