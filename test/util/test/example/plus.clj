(ns util.test.example.plus
  (:use util.example))

(defn plus
  "Adds numbers."
  [& numbers]
  (apply + numbers))

(defexample example1 plus
  "Add 2 and 3."
  5
  (plus 2 3))
