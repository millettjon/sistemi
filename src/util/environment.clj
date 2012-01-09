(ns util.environment
  "Utilities for working with environment variables.")

(def environment
  "Coerce the environment map into a clojure map for easier use and to
   support rebinding in unit tests."
  (into {} (System/getenv)))
