(ns paypal-test
  (:use clojure.test
        paypal
        app.config))

;; TODO: Make this a monitor rather than a test.
(deftest check-versions
  "Check that the live API versions for the sandbox and production sites match what is expected."
  (doseq [site [:sandbox :production]]
    (with-conf {:site site}
      (let [expected (get-in paypal.config/site-conf [site :version])
            actual (last (re-find #"^([\d\.]+)-" (get-version)))]
        (is (= expected actual) (str "A new version of the " (name site) " PayPal API is available."))))))

