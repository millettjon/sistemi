(ns www.id
  (:require [clojure.tools.logging :as log]
            [util.id :as id]))

;; Generate a human readable reasonable unique request identifer for use in log messages.
;;
;; REQUIREMENTS
;; - identifies a web request all requests and jvm instances within the last 30 days
;; - identifies the jvm instance where the request arrived
;; - human readable
;; - short
;; - collisions can happen but should be rare
;;   - manage collisions by checking:
;;     a) the timestamp on the log message
;;     b) the foreman id (e.g., web(1))
;;
;; IMPLEMENTATION
;; - Generate a boot identifier (4 chars base62 random string) at jvm startup to identify the jvm
;;   instance.
;; - Increment a per jvm request counter to identify each request within a jvm instance.
;; - Generate a request id of the form bbbb.rrr where bbbb is the boot
;;   identifier and rrr is the request sequence in base62.

(def boot-id nil)

(defn init!
  "Generate a unique boot identifier."
  []
  (alter-var-root #'boot-id (constantly (id/rand-62 4))))

;; Count requests from zero.
(def request-count (atom 0))

(defn next!
  "Generates and returns a semi-unique request id."
  []
  (str boot-id "." (id/encode-62 (swap! request-count inc))))
