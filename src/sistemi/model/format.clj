(ns sistemi.model.format
  (:use [frinj core calc]))

(defn format-area
  [v unit]
  (format "%.2fm2"
          (-> v
              (to unit unit)
              :v
              double)))       ;; coerce to double
#_ (str (fj 3.2 :meter :meter))
#_ (format-area (fj 30000 :cm :cm) :meter)
#_ (format-area (fj 32000 :cm :cm) :meter)

(defn format-dimensions
  "Returns a format string for a length, area, or volume in the specified units."
  [unit & args]
  (str (apply str (interpose "x" (map  #(:v (to % unit)) args)))
       (name unit)))
#_ (format-dimensions :cm (fj 120 :cm) (fj 34 :cm))

(defn fj-round
  [number places]
  (let [v (:v number)]
    (assoc number :v (.setScale (bigdec v) places BigDecimal/ROUND_HALF_EVEN))))
#_ (fj-round (fj 50.1234 :EUR) 2)

(defn format-eur
  [v]
  (format "%.2f€"
          (-> v
              (to :EUR)
              :v
              bigdec)))
#_ (format-eur (fj 50 :EUR))
#_ (str (fj 50 :EUR))
#_ (to (fj 30 :EUR) :EUR)
#_ (format-eur #frinj.core.fjv{:v 57600.0, :u {"EUR" 1, "m" -2}})

(defn format-quantity
  ([price]
     (format-quantity 1 price))
  ([quantity price]
     (let [quantity (if (= 1 quantity) "" quantity)]
       (str "(" quantity "@" price ")"))))
#_ (format-quantity 4 (format-eur (fj 50 :EUR)))

(defn format-cost-per-area
  [v]
  (format "%.2f€/m2"
          (-> v
              (to :EUR :per :meter :per :meter)
              :v
              double)))
#_ "16.00E/m2"
#_ (str (fj 16 :EUR :per :meter :per :meter))
#_ (format-cost-per-area (fj 16 :EUR :per :meter :per :meter))

(defn html-table
  "header - selects keys to display for each item
   row-keys - selects ordering of rows"
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

