(ns sistemi.model
  "Functions for working with models of nested components."
  (:require [sistemi.model.format :as format])
  (:use frinj.ops))

(defmulti from-params
  "Creates a component from form parameters."
  :type)

(defmulti to-params
  "Coerce a component into a map of form parameters."
  :type)
