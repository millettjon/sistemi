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
  [{:keys [finish width depth quantity]}]
  (let [workbook "shelf/shelf-chain-france.xls"
        m (sheet/get (str "opt/costs/products/" workbook))

        ;; It is not clear if locking is needed (the interface may be
        ;; functional) but including it just in case.
        prices (locking m
                 (-> m
                     ;; set inputs
                     (assoc "C3" (cm->mm width))
                     (assoc "C4" (cm->mm depth))
                     (assoc "C5" (convert-finish finish))
                     (assoc "C6" quantity)

                     ;; read outputs
                     (select-keys ["C13" "C16" "C19" "C22"])

                     ;; return a normal hash
                     (->> (into {}))))

        ;; Add units.
        prices (->> prices
                    (map (fn [[k v]] [k (fj-eur v)]))
                    (into {}))
        total (prices "C22")]

    {:workbook workbook
     :total total
     :unit (fj_ total quantity)
     :parts {:fabrication-stephane (prices "C13")
             :finishing-marques (prices "C16")
             :packaging-box (prices "C19")}}))

#_ (let [shelf {:type :shelf
                :color {:rgb "#C51D34", :type :ral, :code 3027},
                :quantity 20
                :finish :laquer-matte
                :width 120
                :depth 30
                }]
     (get-price shelf))
