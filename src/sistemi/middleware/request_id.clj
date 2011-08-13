(ns sistemi.middleware.request-id
  "Generates a unique request id."
  (:use ring.middleware.file
        [clojure.tools.logging :only (debug)]
        [clj-logging-config.log4j :only (with-logging-context)]
        )
  (:require [sistemi.id :as id]))

(defn wrap-request-id
  "Generates a unique request id and saves it in the logging context."
  [app]
  (fn [req]
    (with-logging-context {:id (id/next!)}
      (debug "Created new request id.")
      (app req))))
