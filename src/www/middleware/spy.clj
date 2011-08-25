(ns www.middleware.spy
  (:use [clojure.tools.logging :only (debug)])
  (:use [clojure.contrib.def :only (defnk)]))

(defnk spy
  "Spies on a request logging it."
  [app :prefix "spy" :keys nil :response nil]
  (fn [req]
    (debug (str prefix ".request: " (if keys (select-keys req keys) req)))
    (let [response (app req)]
      (if response (debug (str prefix ".response: " response)))
      response)))