(ns pack
  "Calculates the number, dimensions, and weight of each box for an order."
  (:require sistemi.init
            [util.frinj :as fu])
  (:use [frinj.ops]
        [clojure.pprint]))

;; DECISION: Package each item separately (for now).

;; TODO: shipping cost needs to be passed in to spreadsheet in order for total to be calculated...
;; TODO: Figure out box weight.
;; TODO: Confirm box width and height.
;; TODO: Confirm box length.
;; TODO:
;; - test shelves
;; - test bookcases

(def materials
  {:mdf {:thickness (fj 0.019 :m)
         :density (fj 725 :kg :per :m :per :m :per :m)}})

(defn mass
  "Returns the mass of an object."
  [x y z density]
  (fj* x y z density))

(defn add-mass-mdf
  "Adds thickness and mass to an mdf part."
  [{:keys [x y] :as item}]
  (let [{{z :thickness :keys [density]} :mdf} materials]
    (assoc item
      :z z
      :mass (mass x y z density))))

;; --------------------------------------------------
;; CALCULATE PART DIMENSIONS
;; --------------------------------------------------

(defmulti
  parts
  "Calculate the dimensions and mass of all parts of a product."
  :type)

;; ---------- SHELF ----------

(defmethod parts :shelf
  [{:keys [width depth quantity]}]
  (let [x (fj width :cm)
        y (fj depth :cm)]
    (repeat quantity (add-mass-mdf {:x x :y y}))))

;; --------- BOOKCASE ----------

(def bookcase-lateral-width
  (fj 9 :cm))

(defn bookcase-num-verticals
  "Returns the number of vertical boards in a bookcase."
  [{:keys [width]}]
  (let [width (fj width :cm)]
    (cond
     (fj< width (fj 1.21 :m)) 2
     (fj< width (fj 1.96 :m)) 3
     :else 4)))

(defn bookcase-num-horizontals
  "Returns the number of vertical boards in a bookcase."
  [{:keys [height]}]
  (let [height (fj height :cm)]
    (cond
     (fj< height (fj 0.71 :m)) 2
     (fj< height (fj 0.94 :m)) 3
     (fj< height (fj 1.31 :m)) 4
     (fj< height (fj 1.68 :m)) 5
     (fj< height (fj 2.04 :m)) 6
     :else 7)))

(defmethod parts :bookcase
  [{:keys [width depth height quantity] :as bookcase}]
  (let [width (fj width :cm)
        depth (fj depth :cm)
        height (fj height :cm)
        num-laterals 2
        num-horizontals (bookcase-num-horizontals bookcase)
        num-verticals (bookcase-num-verticals bookcase)]
    (concat
     (repeat (* quantity num-laterals) (add-mass-mdf {:x bookcase-lateral-width :y width :type :lateral}))
     (repeat (* quantity num-verticals) (add-mass-mdf {:x height :y depth :type :vertical}))
     (repeat (* quantity num-horizontals) (add-mass-mdf {:x width :y depth :type :horizontal})))))

;; --------------------------------------------------
;; BOXES
;; --------------------------------------------------

(def max-parts-per-box 5)
(def box-width (fj 390 :mm))
(def box-height (fj 105 :mm))

(defn box-mass
  "The total mass of a list of parts in a box."
  [box]
  (->> box
       (map :mass)
       (apply fj+)))

(defn part-max-dimension
  "The max dimension of a part."
  [{:keys [x y z]}]
  (fu/fj-max x y z))

(defn box-max-part-length
  "The length of the longest part in a box."
  [box]
  (->> box
       (map (fn [{:keys [x y z]}] (fu/fj-max x y z)))
       (apply fu/fj-max)))

;; Use a naive fn for now.
(defn make-boxes
  "Partitions a list of parts into boxes."
  [parts]
  [parts]
  (->> parts
       (fu/fj-sort-by part-max-dimension)
       (partition max-parts-per-box max-parts-per-box [])))
