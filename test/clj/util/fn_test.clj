(ns util.fn-test
  (:require [util.fn :as fn])
  (:use clojure.test))

(deftest playback
  (are [x y] (= y (let [f (fn/playback x (constantly :baz))]
                     (for [i (range 3)]
                       (f))))
       []               [:baz :baz :baz]
       [:foo]           [:foo :baz :baz]
       [:foo :bar]      [:foo :bar :baz]
       [:foo :bar :qux] [:foo :bar :qux]))
