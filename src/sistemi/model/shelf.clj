(ns sistemi.model.shelf
  "Single shelf model."
  (:require clojure.walk
            [clojure.tools.logging :as log])
  (:use sistemi.model
        sistemi.model.format
        frinj.calc))

;; Keep this here to prevent a weird interaction between frinj and clojure-test-mode.
;; TODO: figure out where this needs to go!
(frinj-init!)

;; --------- CONSTANTS ---------------

(def ^:private prices
  "Pricing data."
  {:material
   {:mdf-ecological (fj 16.00 :EUR :per :m :per :m)
    :plywood-fsc    (fj 37.25 :EUR :per :m :per :m)}
   :cut {:perimeter (fj  6.80 :EUR :per :m :per :m)}
   :finish {:laquer-matte  (fj 50.00 :EUR :per :m :per :m)}})

(def ^:private margin
  0.2)

;; ----------- FUNDAMENTAL COSTS ---------------------
(defmacro add-cost
  "Generate fns to add cost components. For example, to add cost component foo: (add-foo cmp quantity)."
  [type & ks]
  `(defn ~(symbol (str "add-" (name type)))
     [cmp# qty#]
     (add-child cmp# (make ~type :quantity qty# :unit-price (get-in prices '~ks)))))

;;        type         price keys
(add-cost ::material   :material :mdf-ecological)
(add-cost ::finish     :finish :laquer-matte)
(add-cost ::perimeter  :cut :perimeter)

;; ----------- RECTANGULAR COMPONENTS ---------------------
(defn init-rect
  "Initializes a rectangular component."
  [cmp]
  (let [a (area cmp)]
    (-> cmp
        (assoc :area a)
        (add-material a)
        (add-perimeter a)
        (add-finish (fj* 2 a)))))

(defn add-horizontal
  "Initializes a horizontal component."
  [shelf]
  (let [length (:width shelf)]
    (add-child shelf (-> (make ::horizontal
                               :length (:width shelf)
                               :width (:depth shelf)
                               :quantity 1)
                         init-rect))))

;; ----------- SHELF ---------------------
(defmethod explode :shelf
  [cmp]
  (add-horizontal cmp))

;; ---------- HIERARCHY ----------
;; TODO Use a local hierarchy? Will there be conflicts between products?
;; TODO Factor out common code.

(doseq [type [::horizontal]] (derive type ::rect))
(doseq [type [::material ::perimeter ::finish]] (derive type ::area-cost))


;; ----------- ROLLUP ---------------------
;; TODO: Factor out since these are shared by shelf and shelving.
(defmethod rollup ::area-cost
  [cmp]
  (assoc cmp :rollup {:price (str (format-eur (:price cmp))
                                  (format-quantity (format-cost-per-area (:unit-price cmp))))}))

(defmethod rollup ::cost
  [cmp]
  (assoc cmp :rollup {:price (str (format-eur (:price cmp))
                                  (format-quantity (format-eur (:unit-price cmp))))}))

(defmethod rollup ::rect
  [cmp]
  (let [children (reduce #(assoc % (:type %2) (get-in %2 [:rollup :price]))
                         {} (:components cmp))]
    (assoc cmp :rollup (merge children
                              {:area (format-area (:area cmp) :meter)
                               :dimensions (format-dimensions :cm (:length cmp) (:width cmp))
                               :price (str (format-eur (:price cmp)) (format-quantity (:quantity cmp) (format-eur (:unit-price cmp))))}))))

(defmethod rollup :shelf
  [cmp]
  ;; row per child
  (let [children (reduce #(assoc % (:type %2) (:rollup %2)) {} (:components cmp))]
    (assoc cmp :rollup children)))

;; ----------- ROLLUP COLUMNS TO GET TOTAL ---------------------
;; TODO: Factor out since shared by both shelf and shelving.
(defmethod rollup-total ::rect
  [cmp]
  (assoc cmp :totals (reduce #(assoc % (:type %2) (fj* (:quantity cmp) (:price %2))) {} (:components cmp))))

(defmethod rollup-total :shelf
  [cmp]
  (assoc-in cmp [:rollup :total]
            (->> (reduce (fn [m cmp]
                          (reduce (fn [m [k v]] (assoc m k (fj+ (m k (fj 0 :EUR)) v)))         ;; column totals
                                  (assoc m :price (fj+ (m :price (fj 0 :EUR)) (cmp :price)))  ;; total total
                                  (:totals cmp)))
                        {}
                        (:components cmp))
                 ;; format prices
                 (reduce (fn [m [k v]] (assoc m k (format-eur v))) {}))))

;; ----------- FORM HELPERS ---------------------

;; Displayed in cart_htm
(defmethod from-params :shelf
  [params]
  (merge params {:width (fj (:width params) :cm)
                 :depth (fj (:depth params) :cm)
                 :finish (keyword (:finish params))
                 :material :mdf-ecological}))

#_ (price (from-params {:type :shelf :width 120, :depth 30, :color "#00FF00"}))
#_ (price-report (from-params {:type :shelf :width 120, :depth 30, :color "#00FF00"}))
#_ (-> (from-params {:type :shelf :width 120, :depth 30, :color "#00FF00"})
       explode
       (walk total)
       (walk rollup)
;;       (walk rollup-total)
;;       :rollup
;;       clojure.walk/stringify-keys
       clojure.pprint/pprint
    )

(defmethod to-params :shelf
  [shelf]
  (reduce (fn [m [k v]]
            (let [v (if (isa? (class v) frinj.core.fjv)
                      (-> (to v :cm) :v str)
                      v)]
              (assoc m k v)))
          {}
          (select-keys shelf [:id :width :depth :color :finish])))

;; ----------- PRICE BREAKDOWN REPORT ---------------------

(defmethod html-price-report :shelf
  [shelf]
  (->> shelf
      price-report
      (html-table [:item :dimensions :area :material :perimeter :finish :price]
                  [:horizontal :total])))
