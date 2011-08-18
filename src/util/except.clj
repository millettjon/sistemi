(ns util.except
  "Macros for handling and throwing exceptions."
  (:use clojure.contrib.strint))

(defmacro safely
  "Evaluates an expression safely by catching any exceptions thrown.
   If an exception is thrown, the fallback value is returned."
  [expr fallback]
  `(try ~expr (catch Exception x# ~fallback)))

(defmacro die
  "Logs an error message and throws an exception. String interpolation is applied to the error message."
  [message]
  `(let [s# (<< ~message)]
     (log/error s#)
     (throw (RuntimeException. s#))))

(defmacro affirm
  "Executes text-expr and returns the result if true. Otherwise calls die with the given message."
  [test-expr message]
  `(or ~test-expr (die ~message)))
