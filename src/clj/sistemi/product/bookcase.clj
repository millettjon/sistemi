(ns sistemi.product.bookcase
  "Modular bookcase."
  (:use sistemi.product
        frinj.ops))

(defmethod from-params :bookcase
  [params]
  (merge params {:height (fj (:height params) :cm)
                 :width (fj (:width params) :cm)
                 :depth (fj (:depth params) :cm)
                 :cutout (keyword (:cutout params))
                 :finish (keyword (:finish params))}))

;; TODO refactor this as it is common with shelf
(defmethod to-params :bookcase
  [shelving]
  (reduce (fn [m [k v]]
            (let [v (if (instance? frinj.core.fjv v)
                      (-> (to v :cm) :v str)
                      v)]
              (assoc m k v)))
          {}
          (select-keys shelving [:id :height :width :depth :cutout :color :finish])))
