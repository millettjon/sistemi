(ns util.except
  "Macros for handling and throwing exceptions."
  (:require [taoensso.timbre :as log])
  (:use [clojure.pprint :only (cl-format)]))

(defmacro safely
  "Evaluates an expression safely by catching any exceptions thrown.
   If an exception is thrown, the fallback value is returned."
  ([expr]
     `(safely ~expr nil))
  ([expr fallback]
     `(try ~expr (catch Exception x# ~fallback))))

(defmacro swallow
  "Evaluates an expression swallowing any exceptions. Returns the result of evaluating expr or the
   exception if one occurred."
  ([form]
     `(try ~form (catch Exception x# x#)))
  ([form f]
     `(try ~form (catch Exception x#
                   (~f x#)
                   x#))))

#_ (swallow (throw (Exception. "fubar")))
#_ (swallow (throw (Exception. "fubar"))
            prn)

(defn die
  "Throws a RuntimeException with the given message. When multiple arguments are passed, cl-format
  is used to build the message string."
  ([message]
     (throw (RuntimeException. ^String message)))
  ([format-string & format-args]
     (die (apply cl-format nil format-string format-args))))

(defmacro check
  "Evaluates expr and validates the result. The result is returned if validation succeeds. Otherwise, an
   exception is thrown.

   A custom validator function can be passed. If not passed, the function identity will be used to
   test for logical truth of the result.

   A custom error message format and optional format arguments may be passed. The error message is
   constructed using the cl-format function with the supplied format and args. During formatting,
   the offending result value will be inserted as the first format arg.

   EXAMPLES

   ;; Simple message.
   (swallow (check :zz #{:en :es :de :fr} \"I don't know that language.\"))
   => #<RuntimeException java.lang.RuntimeException: I don't know that language.>

   ;; Format string that includes the offending value.
   (swallow (check :zz #{:en :es :de :fr} \"I don't speak '~A'. Are you asleep?\"))
   => #<RuntimeException java.lang.RuntimeException: I don't speak ':zz'. Are you asleep?>

   ;; Format string with additional arguments. Note that the offending value is inserted as the
   ;; first format argument.
   (let [langs #{:en :es :de :fr}]
     (swallow (check :zz langs \"I don't speak '~A'. I do speak ~{~a~^, ~}.\" langs)))
   => #<RuntimeException java.lang.RuntimeException: I don't speak ':zz'. I do speak :en, :es, :de, :fr.>

   ;; If the validator function is omitted, logical truth will be tested.
   (swallow (check nil \"Am I the eggman.\"))
   => #<RuntimeException java.lang.RuntimeException: Am I the eggman.>

   (swallow (check false \"Am I the eggman? ~A.\"))
   => #<RuntimeException java.lang.RuntimeException: Am I the eggman? false.>

   (swallow (check false \"Am I the eggman? ~A (~A).\" \"goo goo g'joob\"))
   => #<RuntimeException java.lang.RuntimeException: Am I the eggman? false (goo goo g'joob).>
"
  ([expr]
     `(check ~expr identity))
  ([expr arg]
     (if (string? arg)
       `(check ~expr identity ~arg)
       `(check ~expr ~arg "Check failed: invalid value '~A' for validator '~A'." '~arg)
       ))
  ([expr validate-fn format-string & format-args]
     (if (string? validate-fn)
       `(check ~expr identity ~validate-fn ~format-string ~@format-args)
       `(let [result# ~expr]
          (if (~validate-fn result#)
            result#
            (die ~format-string result# ~@format-args))))))

(defn log
  "Logs an exception as an error."
  [event-type x]
  (let [data (if (instance? clojure.lang.ExceptionInfo x)
               (.data x)
               x)]
    (log/error {:event event-type :data data})))

;; Modified from: http://stackoverflow.com/questions/1879885/clojure-how-to-to-recur-upon-exception
(defn try-times*
  "Executes thunk. If an exception is thrown, will retry. At most n retries
  are done. If still some exception is thrown it is bubbled upwards in
  the call chain."
  ([n thunk]
     (try-times* n (constantly true) thunk))
  ([n pred thunk]
     (loop [n n]
       (if-let [result (try
                         [(thunk)]
                         (catch Exception e
                           (when (or (zero? n) (not (pred e)))
                             (throw e))))]
         (result 0)
         (recur (dec n))))))
