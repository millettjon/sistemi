(ns app.run-level
  (:use (util environment except)))

;; ===== VARS =====

(def run-levels
  "Set of valid run levels."
  nil)

(def run-level
  "Runtime level of the process."  
  nil)

;; ===== FUNCTIONS =====

(defn- make-predicates
  "Generates predicate functions for testing the run-level."
  []
  (doseq [level run-levels]
    (let [fn-sym (symbol (str (name level) "?"))
          fn-var (intern 'app.run-level fn-sym (fn [] (= run-level level)))]
      (alter-meta! fn-var assoc :doc (str "Returns true if the run level is " level ".")))))

(defn init-run-level!
  "Initializes the current run level and the allowed set of run levels.

   Set of Run Levels
   The set of allowed run levels can be passed in a vector of keywords.
   If not specified, it defaults to #{:development :staging :production}.

   Run Level
   The active run level can be passed as a keyword. If not passed,
   it is set from the environment variable RUN_LEVEL or to :development if
   RUN_LEVEL is not defined.

   Predicate Functions
   For each run level, a predicate function is generated which returns
   true if that run level is the current one. E.g., the predicate
   development? will be generated from the run level :development.

   EXAMPLES
   ;; SIMPLE
   ;; Initialize run-levels to #{:development :staging :production} and
   ;; run-level to the value of the RUN_LEVEL environment variable.
   (init-run-level!)

   ;; SET RUN LEVEL
   ;; Initialize run-level to :production and run-levels to #{:development :staging :production}.
   (init-run-level! :production)

   ;; SET CUSTOM RUN LEVELS
   ;; Initialize run-levels to #{:tinkering :sales-demo :testing :production} and
   ;; run-level to the value of the RUN_LEVEL environment variable.
   (init-run-level! #{:tinkering :sales-demo :testing :production})

   ;; Set CUSTOM RUN LEVELS
   ;; Initialize run-levels to #{:tinkering :sales-demo :testing :production} and
   ;; run-level to :sales-demo.
   (init-run-level! :sales-demo #{:tinkering :sales-demo :testing :production})
"
  ([] (init-run-level! nil))
  ([arg] (if (keyword? arg)
           (init-run-level! arg nil)
           (init-run-level! nil arg)))
  ([level levels]
     (let [levels (or levels #{:development :staging :production})
           level (or level (keyword (environment "RUN_LEVEL")) :development)]
       (alter-var-root #'run-levels (constantly (set (check levels set?))))
       (alter-var-root #'run-level (constantly (check level levels))))
     (make-predicates)))

(init-run-level!)
