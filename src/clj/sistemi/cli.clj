(ns sistemi.cli
  (:require [sistemi.init :as i]))

(defn init-db
  [& args]
  (i/stop))
