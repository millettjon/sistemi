(ns sistemi.handlers
  (:require [clojure.string :as str])
  (:use ring.util.response
        [clojure.contrib.condition :only (raise handler-case *condition*)]))

(defn make-403
  "Returns a custom 403 response."
  [req message]
  (->
   (response (if (= :head (:request-method req))
               nil
               (str "Sorry, the request was refused for the following reason: " message)))
   (status 403)))

(defn make-404
  "Returns a custom 404 response."
  [req]
  (->
   (response (str "Sorry, the page \"" (:uri req "??") "\" could not be found."))
   (status 404)))

(defn make-405
  "Returns a custom 405 response."
  [req method]
  (-> (response (str "Sorry, the request method \"" (name (:request-method req)) "\" is not allowed for resource \""
                     (:uri req "??") "\"."))
      (status 405)
      (header "Allow" (str/upper-case (name method)))))

(defn raise-http
  "Raises an http condition storing the response in the condition map."
  [resp]
  (raise :type :http :response resp))

(defn raise-403
  "Shorthand for (raise-http (make-403 req message))."
  [req message]
  (raise-http (make-403 req message)))

(defn assert-method
  "Asserts that the request uses the given method. Otherwise, raises a condition with an http 405 ring response."
  [req method]
  (when (not= (:request-method req) method)
    (raise-http (make-405 req method))))

(defn wrap-condition
  "Ring wrapper that handles http error conditions."
  [app]
  (fn [req]
    (handler-case :type
      (app req)
      (handle :http
        (:response *condition*)))))
