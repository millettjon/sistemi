(ns sistemi.shelving
  "Modulare shelving model."
  (:use sistemi.model
        sistemi.model.format
        [frinj core calc]))

;; --------- CONSTANTS ---------------

(def ^:private prices
  "Pricing data."
  {:material
   {:mdf-ecological (fj 16.00 :EUR :per :meter :per :meter)
    :plywood-fsc    (fj 37.25 :EUR :per :meter :per :meter)}
   :cut {:slot      (fj  3.60 :EUR)
         :cutout    (fj  3.60 :EUR)
         :perimeter (fj  6.80 :EUR :per :meter :per :meter)}
   :finish {:matte  (fj 50.00 :EUR :per :meter :per :meter)}})

(def ^:private num-laterals
  "Number of lateral members in a shelving unit."
  2)

(def ^:private lateral-width
  "Fixed width of each lateral member."
  (fj 9 :cm))

;; ---------- COUNT TRANSITIONS FOR VERTICAL AND HORIZONTAL COMPONENTS ----------

(def horizontal-buckets
  "Horizontal length bucket boundaries."
  (map #(fj % :cm) [60 71 94 131 168 204 241]))

(defn num-horizontals
  "Returns the number of shelves given the height of the shelving unit."
  [height]
  (+ 2 (bucket-index horizontal-buckets height)))

(def vertical-buckets
  "Vertical length bucket boundaries."
  (map #(fj % :cm) [60 121 196 241]))

(defn num-verticals
  "Returns the number of verticals given the width of a shelving unit."
  [width]
  (+ 2 (bucket-index vertical-buckets width)))

;; ----------- FUNDAMENTAL COSTS ---------------------
(defmacro add-cost
  "Generate fns to add cost components. For example, to add cost component foo: (add-foo cmp quantity)."
  [type & ks]
  `(defn ~(symbol (str "add-" (name type)))
     [cmp# qty#]
     (add-child cmp# (make ~type :quantity qty# :unit-price (get-in prices '~ks)))))

;;        type         price keys
(add-cost ::material   :material :mdf-ecological)
(add-cost ::finish     :finish :matte)
(add-cost ::perimeter  :cut :perimeter)
(add-cost ::slot       :cut :slot)
(add-cost ::cutout     :cut :cutout)

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
  [shelving]
  (let [length (:width shelving)]
    (add-child shelving (-> (make ::horizontal
                                  :length (:width shelving)
                                  :width (:depth shelving)
                                  :quantity (num-horizontals (:height shelving)))
                            init-rect
                            (add-slot (num-verticals length))))))

(defn add-vertical
  "Initializes a vertical component."
  [shelving]
  (let [num-horizontals (num-horizontals (:height shelving))
        cmp (-> (make ::vertical
                   :length (:height shelving)
                   :width (:depth shelving)
                   :quantity (num-verticals (:width shelving)))
                init-rect
                (add-slot (fj+ num-horizontals num-laterals)))
        cmp (if (:cutout shelving)
              (add-cutout cmp (dec num-horizontals))
              cmp)]
    (add-child shelving cmp)))

(defn add-lateral
  [shelving]
  (let [length (:width shelving)]
    (add-child shelving (-> (make ::lateral
                                  :length length
                                  :width lateral-width
                                  :quantity num-laterals)
                            init-rect
                            (add-slot (num-verticals length))))))

;; ----------- SHELVING ---------------------

(defn explode-shelving
  [cmp]
  (-> (make ::shelving cmp)
      add-horizontal
      add-vertical
      add-lateral))

;; ---------- HIERARCHY ----------
;; TODO use a local hierarchy?

(doseq [type [::lateral ::horizontal ::vertical]] (derive type ::rect))
(doseq [type [::material ::perimeter ::finish]] (derive type ::area-cost))
(doseq [type [::slot ::cutout]] (derive type ::cost))

;; ----------- ROLLUP ---------------------

(defmulti rollup :type)
(defmethod rollup :default [cmp] cmp)


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

(defmethod rollup ::shelving
  [cmp]
  ;; row per child
  (let [children (reduce #(assoc % (:type %2) (:rollup %2)) {} (:components cmp))]
    (assoc cmp :rollup children)))

;; ----------- ROLLUP COLUMNS TO GET TOTAL ---------------------

(defmulti rollup-total :type)
(defmethod rollup-total :default [cmp] cmp)

(defmethod rollup-total ::rect
  [cmp]
  (assoc cmp :totals (reduce #(assoc % (:type %2) (fj* (:quantity cmp) (:price %2))) {} (:components cmp))))

(defmethod rollup-total ::shelving
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

(defn price
  "Calcuates the total price of a shelving unit."
  [shelving]
  (-> shelving
      explode-shelving
      (walk total)
      :price))

;; ----------- TESTS ---------------------

(def shelving
  {:height (fj 120 :cm)
   :width  (fj 120 :cm)
   :depth  (fj 39 :cm)
   :cutout :ovale
   :finish :matte
   :material :mdf-ecological
   :color 0xAB003B})

#_ (fj+ (fj 2 :EUR) 0) ;; should this work as a special case?

(require 'clojure.walk)

(use 'clojure.pprint)
#_ (-> shelving
       explode-shelving
       (walk total)
       (walk rollup)
       (walk rollup-total)
       :rollup
       clojure.walk/stringify-keys
       ;; insert item key and convert to array
       (->> (reduce (fn [a [k v]] (conj a (assoc v "item" k))) []))
       (->> (print-table ["item"  "dimensions" "area" "material" "perimeter" "finish" "cutout" "slot" "price"]))
       )

#_ (price shelving)

;; 479.34
