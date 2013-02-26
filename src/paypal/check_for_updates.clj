(ns paypal.check-for-updates
  (:use paypal
        app.config))

(defn- get-updates
  "Returns a seq of updates available for a paypal sandbox and production sites."
  []
  (filter identity (for [site [:sandbox :production]]
                     (with-conf {:site site}
                       (let [expected (get-in paypal.config/site-conf [site :version])
                             actual (last (re-find #"^([\d\.]+)-" (get-version)))]
                         (when-not (= expected actual)
                           [site expected actual]))))))

(defn -main
  "Check that the live API versions for the sandbox and production sites match the configured ones."
  []
  (when-let [results (get-updates)]
    (with-bindings {#'*out* *err*}
      (doseq [[site expected actual] results]
        (println (str "Warning: The PayPal " (name site) " API is at version " actual " but the code is at version " expected "."))))
    (System/exit 1)))
