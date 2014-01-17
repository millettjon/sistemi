(ns sistemi.cli
  (:require [app.run-level]
            [app.config]
            [sistemi.config]
            [sistemi.datomic :as d]))

(defn init-db
  [& args]
  (app.run-level/init-run-level!)
  (sistemi.config/init!)
  (prn "Initializing the datomic schema.")
  (-> (d/get-uri) d/init-db)
  (datomic.api/shutdown true))
