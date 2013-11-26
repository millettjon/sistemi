(ns sistemi.cost
  "Calculate item costs using spreadsheets."
  (:require [clojure.core.cache :as cache]
            [clojure.tools.logging :as log])
  (:use [net.cgrand.spreadmap :only [spreadmap]]
        [frinj.ops]))

(def ^:private sheet-cache
  "Cache spreadsheet objects for 10 minutes."
  (atom (cache/ttl-cache-factory {} :ttl (* 10               ; min
                                            60               ; sec/min
                                            1000             ; ms/sec
                                            ))))

(defn- load-sheet
  "Loads a spreadsheet using spreadmap."
  [path]
  (log/info "Loaded spreadsheet" path)
  (spreadmap path))

(defn- get-spreadmap
  "Returns a cached spreadsheet object by path creating a new one if necessary."
  [path]
  (let [C @sheet-cache
        C (if (cache/has? C path)
            (cache/hit C path)
            (swap! sheet-cache #(cache/miss % path (load-sheet path))))]
    (get C path)))

(defmulti unit-price
  "Calculates the unit price for an item."
  :type)

;; TODO Remove this if possible and force the same set of options in the
;;      spreadsheet as the code model.
;; TODO How should valchromat oiled be priced?
(defn- convert-finish
  "Converts the item finish value to the format expected by the spreadsheet."
  [finish]
  (case finish
    (:laquer-matte :laquer-satin :laquer-glossy) "rubio monocoat"
    "sanded"))

(defn- cm->mm
  "Converts cm to mm."
  [n]
  (* n 10))

(defmethod unit-price :shelf
  [{:keys [finish width depth quantity]}]
  (let [m (get-spreadmap "opt/costs/products/shelf/shelf-cost-stephane.xls")]
    ;; It is not clear if locking is needed (the interface may be
    ;; functional) but including it just in case.
    (locking m
      (-> m
          (assoc "C4" (cm->mm depth))
          (assoc "C5" (cm->mm width))
          (assoc "C6" (convert-finish finish))
          (assoc "C7" quantity)
          (get "C10")
          (/ quantity)
          (fj :EUR)))))

(defn assoc-prices
  [{:keys [quantity] :as item}]
  (let [unit-price (unit-price item)]
    (assoc item
      :unit-price unit-price
      :price (fj* unit-price quantity)
      )))

#_ (let [shelf {:type :shelf
                :color {:rgb "#C51D34", :type :ral, :code 3027},
                :quantity 2
                :finish :laquer-matte
                :width 120
                :depth 30
                }]
     (assoc-unit-price shelf))
