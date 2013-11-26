(ns sistemi.model.shelf
  "Single shelf model."
  (:require clojure.walk
            [clojure.tools.logging :as log])
  (:use sistemi.model
        sistemi.model.format
        frinj.ops))

(defmethod from-params :shelf
  [params]
  (merge params {:width (fj (:width params) :cm)
                 :depth (fj (:depth params) :cm)
                 :finish (keyword (:finish params))}))

(defmethod to-params :shelf
  [shelf]
  (reduce (fn [m [k v]]
            (let [v (if (isa? (class v) frinj.core.fjv)
                      (-> (to v :cm) :v str)
                      v)]
              (assoc m k v)))
          {}
          (select-keys shelf [:id :width :depth :color :finish])))
