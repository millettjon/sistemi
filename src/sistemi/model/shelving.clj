(ns sistemi.model.shelving
  "Modulare shelving model."
  (:require clojure.walk
            [clojure.tools.logging :as log])
  (:use sistemi.model
        sistemi.model.format
        frinj.calc))

;; Keep this here to prevent a weird interaction between frinj and clojure-test-mode.
(frinj-init!)

;; --------- CONSTANTS ---------------

(def ^:private prices
  "Pricing data."
  {:material
   {:mdf-ecological (fj 16.00 :EUR :per :m :per :m)
    :plywood-fsc    (fj 37.25 :EUR :per :m :per :m)}
   :cut {:slot      (fj  3.60 :EUR)
         :cutout    (fj  3.60 :EUR)
         :perimeter (fj  6.80 :EUR :per :m :per :m)}
   :finish {:matte  (fj 50.00 :EUR :per :m :per :m)}})

(def ^:private margin
  0.2)

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
        cmp (if (not= (:cutout shelving) :sistemi.form/semplice)
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
      :price
      (fj-round 2)))

;; ----------- FORM HELPERS ---------------------

;; TODO: Add margin calculation.
;; TODO: Round to customer friendly amounts (nearest euro? nearest 5 euro?).
;; TODO: Display price report as easter egg.

;; TODO: Integrate frinj units with forms and form validation.
;; TODO: Find an idomatic representation for color.
;; TODO: Handle prices w/ currencies (frinj units).
(defn from-params
  [params]
  {:height (fj (:height params) :cm)
   :width (fj (:width params) :cm)
   :depth (fj (:depth params) :cm)
   :cutout (keyword (:cutout params))
   :color (:color params)
   :finish :matte
   :material :mdf-ecological})

;; ----------- PRICE BREAKDOWN REPORT ---------------------

(defn unqualify-keys
  "Recursively transforms all map keys from qualified keywords to unqualified ones."
  {:added "1.1"}
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(keyword (name k)) v] [k v]))]
    ;; only apply to maps
    (clojure.walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn price-report
  [shelving]
  (-> shelving
      explode-shelving
      (walk total)
      (walk rollup)
      (walk rollup-total)
      :rollup
      ;;clojure.walk/stringify-keys
      unqualify-keys
      ))

(defn html-table
  "header - selects keys to display for each item
   row-keys - selects ordering of rows   
   "
  [header row-keys data]
  [:table.table.table-condensed.table-striped
   [:thead
    [:tr
     (map (fn [k] [:th (name k)]) header)]]
   [:tbody
    (map (fn [rk]
           (let [row (rk data)]
             [:tr
              [:td (name rk)] ;; 1st column
              (map (fn [k] [:td (k row)]) (rest header))
              ]))
         row-keys)]])

(defn html-price-report
  [shelving]
  (->> shelving
      price-report
      (html-table [:item :dimensions :area :material :perimeter :finish :cutout :slot :price]
                  [:horizontal :lateral :vertical :total]
                  )))

(use 'clojure.pprint)
(defn pprint-price-report
  [shelving]
  (-> shelving
      price-report
      ;; insert item key and convert to array
      (->> (reduce (fn [a [k v]] (conj a (assoc v :item k))) []))
      (->> (print-table [:item  :dimensions :area :material :perimeter :finish :cutout :slot :price]))))
