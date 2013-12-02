(ns sistemi.product
  "Functions for working with sistemi products."
  (:use frinj.ops))

(defmulti from-params
  "Creates a component from form parameters."
  :type)

(defmulti to-params
  "Coerce a component into a map of form parameters."
  :type)
