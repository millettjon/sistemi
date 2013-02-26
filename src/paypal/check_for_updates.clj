(ns paypal.check-for-updates
  (:use paypal
        app.config))

#_(defn -main
  "Check that the live API versions for the sandbox and production sites match the configured ones."
  []
  (doseq [site [:sandbox :production]]
    (with-conf {:site site}
      (let [expected (get-in paypal.config/site-conf [site :version])
            actual (last (re-find #"^([\d\.]+)-" (get-version)))]
        (when-not (= expected actual)
          (with-bindings {#'*out* *err*}
            (println (str "Warning: The PayPal " (name site) " API is at version " actual " but the code is at version " expected ".")))
          (System/exit 1))))))

(defn -main
  "Check that the live API versions for the sandbox and production sites match the configured ones."
  []
  (let [results (for [site [:sandbox :production]]
                  (with-conf {:site site}
                    (let [expected (get-in paypal.config/site-conf [site :version])
                          actual (last (re-find #"^([\d\.]+)-" (get-version)))]
                      (when-not (= expected actual)
                        [site expected actual]))))
        results (filter identity results)]
    (when-not (empty? results)
      (with-bindings {#'*out* *err*}
        (doseq [[site expected actual] results]
          (println (str "Warning: The PayPal " (name site) " API is at version " actual " but the code is at version " expected "."))))
      (System/exit 1))))
