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

(defn format-eur
  [v]
  (format "%.2f€"
          (-> v
              (to :EUR)
              :v
              double)))
#_ (format-eur (fj 50 :EUR))
#_ (str (fj 50 :EUR))
#_ (to (fj 30 :EUR) :EUR)

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

