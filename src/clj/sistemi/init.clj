(ns sistemi.init
  "Initializes critical resources to bootstrap the system."
  (:require [frinj.jvm :as f]
            [sistemi
             [config :as c]
             [datomic :as d]]))

;; Require this from packages that depend on the minimum core resources to be initialized.
;; Keep the following system startup cases in mind.
;; - -main (start whole system)
;; - cli (run a task and exit)
;; - test runner
;; - repl

;; Keep these in the top level and rely on the semantics of require to only run it once.
(c/init!)
(f/frinj-init!)
(d/init-db)

(defn stop
  "Stop system components to allow a clean shutdown."
  []
  ;; Stop datomic background threads.
  (datomic.api/shutdown true))
