(ns sistemi.product.bookcase
  "Modular bookcase."
  (:require [sistemi.sheet :as sheet]
            [sistemi.product :as p]
            [sistemi.order :as o])
  (:use frinj.ops
        util.frinj))

(defmethod p/from-params :bookcase
  [params]
  (merge params {:height (fj (:height params) :cm)
                 :width (fj (:width params) :cm)
                 :depth (fj (:depth params) :cm)
                 :cutout (keyword (:cutout params))
                 :finish (keyword (:finish params))}))

(defmethod p/to-params :bookcase
  [bookcase]
  (p/fj-params-to-str(select-keys bookcase [:id :height :width :depth :cutout :color :finish])))

(defmethod o/get-price :bookcase
  [{:keys [finish width depth height cutout quantity]} {:keys [taxable?]}]
  (let [workbook "bookcase/bookcase-chain-france.xls"
        m (sheet/get (str "opt/costs/products/" workbook))

        ;; It is not clear if locking is needed (the interface may be
        ;; functional) but including it just in case.
        prices (locking m
                 (-> m
                     ;; set inputs
                     (assoc "order_length" (p/cm->mm width))
                     (assoc "order_width" (p/cm->mm depth))
                     (assoc "order_height" (p/cm->mm height))
                     (assoc "order_finish" (p/convert-finish finish))
                     (assoc "order_cutout" (p/convert-cutout cutout))
                     (assoc "order_quantity" quantity)

                     ;; read outputs
                     (select-keys ["fab_stephane_total"
                                   "order_finishing_marques"
                                   "packaging_box_total"
                                   "order_subtotal"
                                   "total_margin"
                                   "total_total"])

                     ;; return a normal hash
                     (->> (into {}))))

        ;; Add units.
        prices (->> prices
                    (map (fn [[k v]] [k (-> v fj-eur (fj-round 2))]))
                    (into {}))
        total (prices "total_total")]

    ;; TODO: Factor this out as it is shared with shelf.
    {:workbook workbook
     :total total
     :unit (fj-bd_ total quantity 2)
     :parts {:fabrication-stephane (prices "fab_stephane_total")
             :finishing-marques (prices "order_finishing_marques")
             :packaging-box (prices "packaging_box_total")
             :subtotal (prices "order_subtotal")
             :margin (prices "total_margin")}}))
