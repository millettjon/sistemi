(ns util.example
  "Run code examples as unit tests and auto update docstrings.

   GOALS
   Allow example code to be both documentation and a test.
   Automatically update function documentation to include examples.
   Documentation should be seen by autodoc.
   Automatically define tests based on the examples.
   The test namespace should be allowed to be anything.
   Allow multiple examples per function.
   Allow examples to be attached to functions, macros, and namespaces.

   ISSUES
   Line level debugging doesn't work.
"
  (:use clojure.test
        clojure.contrib.pprint))

;; TODO: Can example document itself?


(defn- append-doc
  "Appends a string to the documentation for item"
  [f s]
  (alter-meta! (resolve f) (fn [md] (assoc md :doc (str (:doc md) s)))))

(defn- assoc-example
  "Associates a code example with a namespace's :examples metadata key."
  [ns target item]
  (alter-meta!
   ns
   (fn [md]
     (assoc md :examples (assoc (or (:examples md) {}) target item)))))

(defmacro defexample
  "Define example code associated with a function."
  ([name target result example] `(defexample ~name "Example:" ~result ~example))
  ([name target heading result example]
     ;; TODO: Verify that target is a function, macro, or namespace.

     ;; Append the example to the target's documentation.
     (append-doc
      target
      (with-out-str
        (println)
        (println heading)
        (with-pprint-dispatch *code-dispatch* (pprint example))
        (println "=>" result)))
     
     ;; Append the example code to the namespace's metadata.
     (when *load-tests*
       (assoc-example *ns* name [heading result example]))
     nil))

(defmacro import-examples
  "Create tests from examples in the given namespace."
  [ns]
  ;; TODO: Verify that the namespace exists.
  ;; TODO: Verify that examples exist.
  ;;(prn (meta (find-ns ns)))
  (let [examples (:examples (meta (find-ns ns)))]
    `(do ~@(for [[name [heading result example]] examples]
             `(testing ~heading
                (deftest ~name
                  (is (= ~result ~example))))))))

;;(append-example (find-ns 'util.foo))
;;(meta (find-ns 'util.foo))

;; clear meta
;;(alter-meta! (find-ns 'util.foo) (fn [md] {}))
