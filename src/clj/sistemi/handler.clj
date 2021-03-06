(ns sistemi.handler
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log])
  (:use ring.util.response
        [slingshot.slingshot :only [throw+]]))

(defn make-403
  "Returns a custom 403 (forbidden) response."
  [req message]
  (->
   (response (if (= :head (:request-method req))
               nil
               (str "Sorry, the request was refused for the following reason: " message)))
   (status 403)))

;; TODO: Make this a template.
(defn make-404
  "Returns a custom 404 (not found) response."
  [req]
  (->
   (response (str "Sorry, the page \"" (:uri req "??") "\" could not be found."))
   (status 404)))

(defn make-405
  "Returns a custom 405 (method not allowed) response."
  [req method]
  (-> (response (str "Sorry, the request method \"" (name (:request-method req)) "\" is not allowed for resource \""
                     (:uri req "??") "\"."))
      (status 405)
      (header "Allow" (str/upper-case (name method)))))

(defn throw-403
  "Throws a 403 response."
  [req message]
  (throw+ (make-403 req message)))

(defn assert-method
  "Asserts that the request uses the given method. Otherwise, throws a 405 response."
  [req method]
  (when (not= (:request-method req) method)
    (throw+ (make-405 req method))))
