(ns sistemi.product.shelf
  "Single shelf product."
  (:require [sistemi.sheet :as sheet])
  (:use [sistemi.product]
        [sistemi.order]
        [frinj.ops]
        [util.frinj]))

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

(defn- cm->mm
  "Converts cm to mm."
  [n]
  (* n 10))

;; TODO Remove this if possible and force the same set of options in the
;;      spreadsheet as the code model.
(defn- convert-finish
  "Converts the item finish value to the format expected by the spreadsheet."
  [finish]
  (case finish
    (:laquer-matte :laquer-satin :laquer-glossy) "laquer"
    :valchromat-oiled "oil"
    :valchromat-raw "raw"))

(defmethod get-price :shelf
  [{:keys [finish width depth quantity]} {:keys [taxable]}]
  (let [workbook "shelf/shelf-chain-france.xls"
        m (sheet/get (str "opt/costs/products/" workbook))

        ;; It is not clear if locking is needed (the interface may be
        ;; functional) but including it just in case.
        prices (locking m
                 (-> m
                     ;; set inputs
                     (assoc "order_length" (cm->mm width))
                     (assoc "order_width" (cm->mm depth))
                     (assoc "order_finish" (convert-finish finish))
                     (assoc "order_quantity" quantity)
                     (assoc "order_taxable" (if taxable "yes" "no"))

                     ;; read outputs
                     (select-keys ["fab_stephane_total"
                                   "C17" ; finishing cost
                                   "packaging_box_total"
                                   "order_subtotal"
                                   "total_margin"
                                   "total_tax"
                                   "total_adjustment"
                                   "total_total"])

                     ;; return a normal hash
                     (->> (into {}))))

        ;; Add units.
        prices (->> prices
                    (map (fn [[k v]] [k (-> v fj-eur (fj-round 2))]))
                    (into {}))
        total (prices "total_total")]

    {:workbook workbook
     :total total
     ;;:unit (fj_ (-> total :v .doubleValue) quantity)
     :unit (fj-bd_ total quantity 2)
     :parts {:fabrication-stephane (prices "fab_stephane_total")
             :finishing-marques (prices "C17")
             :packaging-box (prices "packaging_box_total")
             :subtotal (prices "order_subtotal")
             :margin (prices "total_margin")
             :tax (prices "total_tax")
             :adjustment (prices "total_adjustment")}}))

#_ (let [shelf {:type :shelf
                :color {:rgb "#C51D34", :type :ral, :code 3027},
                :quantity 3
                :finish :laquer-matte
                :width 120
                :depth 30
                :taxable true
                }]
     (get-price shelf))
