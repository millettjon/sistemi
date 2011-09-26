(ns www.middleware.spy
  (:use [clojure.tools.logging :only (info)]
        [clojure.contrib.def :only (defnk)]
        [clojure.string :only (join upper-case)]))

(defnk spy
  "Spies on a request logging it."
  [app :prefix "spy" :req-keys [] :resp-keys nil]
  (fn [req]
    (if req-keys
      (info (str prefix ".request: " (if (empty? req-keys) req (select-keys req req-keys)))))
    (let [response (app req)]
      (if resp-keys
        (info (str prefix ".response: " (if (empty? resp-keys) response (select-keys response resp-keys)))))
      response)))

(defn wrap-request-log
  "Logs a short one line summary of info for each request."
  [app]
  (fn [req]
    (let [started (System/currentTimeMillis)
          response (app req)
          elapsed (- (System/currentTimeMillis) started)]
      (info (join " "
                  [(upper-case (name (req :request-method)))
                   (str (req :uri)
                        (if-let [qs (req :query-string)]
                          (str "?" qs)
                          ""))
                   (response :status)
                   (str "(" elapsed ")")]))
      response)))


