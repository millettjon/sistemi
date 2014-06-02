(ns sistemi.product.shelf
  "Single shelf product."
  (:require [sistemi.sheet :as sheet]
            [sistemi.product :as p])
  (:use [sistemi.order]
        [frinj.ops]
        [util.frinj]))

(defmethod p/from-params :shelf
  [params]
  (merge params {:width (fj (:width params) :cm)
                 :depth (fj (:depth params) :cm)
                 :finish (keyword (:finish params))}))

(defmethod p/to-params :shelf
  [shelf]
  (p/fj-params-to-str (select-keys shelf [:id :width :depth :color :finish])))

(defmethod get-price :shelf
  [{:keys [finish width depth quantity]} {:keys [taxable?]}]
  (let [workbook "shelf/shelf-chain-france.xls"
        m (sheet/get (str "opt/costs/products/" workbook))

        ;; It is not clear if locking is needed (the interface may be
        ;; functional) but including it just in case.
        prices (locking m
                 (-> m
                     ;; set inputs
                     (assoc "order_length" (p/cm->mm width))
                     (assoc "order_width" (p/cm->mm depth))
                     (assoc "order_finish" (p/convert-finish finish))
                     (assoc "order_quantity" quantity)
                     (assoc "order_taxable" (if taxable? "yes" "no"))

                     ;; read outputs
                     (select-keys ["fab_stephane_total"
                                   "order_finishing_marques"
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
             :finishing-marques (prices "order_finishing_marques")
             :packaging-box (prices "packaging_box_total")
             :subtotal (prices "order_subtotal")
             :margin (prices "total_margin")
             :tax (prices "total_tax")
             :adjustment (prices "total_adjustment")}}))
