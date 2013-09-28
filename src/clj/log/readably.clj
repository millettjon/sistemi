(ns log.readably
  "Log functions that print arguments in a readable format."
  (:require [clojure.tools.logging :as log]))

;; TODO: make into a macro
;; TODO: make a macro logr for it to call
;; TODO: make a patch for tools.logging?
(defn info
  [& args]
  (log/info (pr-str args)))
