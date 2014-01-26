(ns www.middleware
  (:require [taoensso.timbre :as log]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [www.id :as id]
            [www.request :as req])
  (:use [clojure.contrib.def :only (defnk)] 
        [clj-logging-config.log4j :only (with-logging-context)]
        [slingshot.slingshot :only [try+]]
        [ns-tracker.core :only (ns-tracker)]))

(defn wrap-request-id
  "Generates a unique request id and saves it in the logging context."
  [app]
  (fn [req]
    (let [id (id/next!)]
      (app (assoc req :request-id id)))))

(defn wrap-ping
  "Checks for the url \"/ping\" and returns a simple \"pong\".
   Otherwise, delegates to app."
  [app]
  (fn [req]
    (if (= (:uri req) "/ping")
      (ring/response "pong")
      (app req))))

(defn wrap-request
  "Binds the dynamic var www/*req* to the current request."
  [app]
  (fn [req]
    (binding [req/*req* req]
      (app req))))

(defn wrap-exception-response
  "Ring wrapper that catches response maps thrown as exceptions from further down the handler stack."
  [app]
  (fn [req]
    (try+
      (app req)
      (catch map? m m))))

(defn wrap-render
  "Middleware that realizez (i.e., renders) the response content sequence.
   Useful for rendering templates while dynamic vars are still in scope."
  [app]
  (fn [req]
    (let [response (app req)
          body (:body response)]
      (if (seq? body)
        (assoc response :body (doall body))
        response))))

(defnk spy
  "Spies on a request logging it."
  [app :label "_" :req-keys [] :resp-keys nil]
  (fn [req]
    (if req-keys
      (log/info (assoc (if (empty? req-keys) req (select-keys req req-keys))
                  :event (keyword "spy.request" label))))
    (let [response (app req)]
      (if resp-keys
        (log/info (assoc (if (empty? resp-keys) response (select-keys response resp-keys))
                    :event (keyword "spy.response" label))))
      response)))

(defn wrap-request-log
  "Logs a short one line summary of info for each request."
  [app]
  (fn [req]
    (let [started (System/currentTimeMillis)
          response (app req)
          elapsed (- (System/currentTimeMillis) started)]
      (log/info (str/join " "
                  [(str/upper-case (name (req :request-method)))
                   (str (req :uri)
                        (if-let [qs (req :query-string)]
                          (str "?" qs)
                          ""))
                   (response :status)
                   (str "(" elapsed ")")]))
      response)))

(defn wrap-reload
  "Like ring's wrap-reload but with an additional option :callback
   which specifies a function to call when a namespace is reloaded."
  [handler & [options]]
  (let [source-dirs (:dirs options ["src"])
        callback-fn (:callback options identity)
        modified-namespaces (ns-tracker source-dirs)]
    (fn [request]
      (doseq [ns-sym (modified-namespaces)]
        (require ns-sym :reload)
        (callback-fn ns-sym))
      (handler request))))
