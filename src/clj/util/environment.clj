(ns util.environment
  "Utilities for working with environment variables.")

(def environment
  "Coerce the environment map into a clojure map for easier use and to
   support rebinding in unit tests."
  (into {} (System/getenv)))

(defn get-eval
  "Gets and evals an environment variable."
  [key]
  (if-let [val (get environment key)]
    (clojure.core/eval (read-string val))))
