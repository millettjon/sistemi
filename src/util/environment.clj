(ns util.environment
  "Utilities for working with environment variables."
  (:refer-clojure :exclude [map eval]))

(def map
  "Coerce the environment map into a clojure map for easier use and to
   support rebinding in unit tests."
  (into {} (System/getenv)))

(defn eval
  "Gets the value of the environment variable key and reads and evals it."
  [key]
  (if-let [val (get map key)]
    (clojure.core/eval (read-string val))))
