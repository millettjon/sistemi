(ns util.environment
  "Utilities for working with environment variables."
  (:use [clojure.contrib.def :only (defvar)]))

(defvar environment
  (into {} (System/getenv))
  "Coerce the environment map into a clojure map for easier use and to
   support rebinding in unit tests.")
