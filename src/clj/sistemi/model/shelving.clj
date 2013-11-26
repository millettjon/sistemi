(ns sistemi.model.shelving
  "Modular shelving model."
  (:require clojure.walk
            [clojure.tools.logging :as log])
  (:use sistemi.model
        sistemi.model.format
        frinj.ops))

(defmethod from-params :shelving
  [params]
  (merge params {:height (fj (:height params) :cm)
                 :width (fj (:width params) :cm)
                 :depth (fj (:depth params) :cm)
                 :cutout (keyword (:cutout params))
                 :finish (keyword (:finish params))}))

;; TODO refactor this as it is common with shelf
(defmethod to-params :shelving
  [shelving]
  (reduce (fn [m [k v]]
            (let [v (if (instance? frinj.core.fjv v)
                      (-> (to v :cm) :v str)
                      v)]
              (assoc m k v)))
          {}
          (select-keys shelving [:id :height :width :depth :cutout :color :finish])))
