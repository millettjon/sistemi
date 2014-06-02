(ns sistemi.cli
  (:require [app.config]
            [sistemi.config]
            [sistemi.datomic :as d]))

(defn init-db
  [& args]
  (sistemi.config/init!)
  (prn "Initializing the datomic schema.")
  (d/init-db)
  (datomic.api/shutdown true))
