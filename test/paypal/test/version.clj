(ns paypal.test.version
  (:use clojure.test
        paypal.version
        paypal.conf))

(deftest check-versions
  (let [;;test-version (get-version :test)
        prod-version (get-version :production)
        version (last (re-find #"^([\d\.]+)-" prod-version))]
    (is (= api-version version) "A new version of the PayPal API is available.")))
