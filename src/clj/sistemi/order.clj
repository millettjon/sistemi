(ns sistemi.order
  "Work with orders."
  (:require [sistemi.format :as fmt]
            [util.id :as id]
            [util.edn :as edn]
            [datomic.api :as d]
            [sistemi.datomic :as sd]
            [taoensso.timbre :as log]
            [frinj.ops :as f]
            [schema.core :as s]
            [util.calendar :as cal]
            [ship])
  (:use [clojure.pprint :only [pprint]]
        [util.frinj :only [fj-eur fj-round fj-bd_]]))

(def france-tax-rate 0.20)

(defmulti get-price
  "Calculates the price components for an item."
  :type)

(defn detail-report
  "Creates price detail report."
  [m]
  [:div (:workbook m)
   [:table
    (for [[k v] (:parts m)]
      [:tr [:td k] [:td (fmt/eur v)]])
    [:tr [:td "unit"]  [:td (-> m :unit fmt/eur)]]
    [:tr [:td "total"] [:td (-> m :total fmt/eur)]]]])

;; Hmm, maybe not worthwhile as it doesn't take shipping into account.
;; - changes if ups shipping price includes taxes
;; (subtotal + adjustment) * (1 + tax-rate) = total
;; (subtotal + adjustment) = total / (1 + tax-rate)
;; adjustment = (total / (1 + tax-rate)) - subtotal
(defn fudge
  "Calculate fudge adjustment to make a total come out to given whole number."
  [total subtotal tax-rate]
  (-> total
      (fj-bd_ (+ 1 tax-rate) 2)
      (f/fj- subtotal)
      (fj-round 2)))

(defn recalc
  "Recalculates prices for an order."
  [{:keys [items shipping taxable?] :as order}]
  (let [;; Recalculate prices of each line item using spreadsheets.
        items (reduce (fn [items [i item]] (assoc-in items [i :price] (get-price item order)))
                      items
                      items)

        ;; Shipping cost of whole order.
        ship-map (if shipping
                   {:shipping (merge shipping (ship/estimate order))})
        shipping (or (get-in ship-map [:shipping :price :total])
                     (fj-eur 0))

        ;; Calculate subtotal of all items.
        subtotal (apply f/fj+ (map #(-> % second :price :total) items))

        ;; Sales tax.
        tax (if taxable?
              (-> (f/fj* subtotal france-tax-rate)
                  (fj-round 2))
              (fj-eur 0))

        ;; Grand total, including tax and shipping.
        total (f/fj+ subtotal tax shipping)]

    (-> order
        (merge ship-map)
        (assoc
          :items items
          :price {:sub-total subtotal
                  :tax tax
                  :total total}))))

(defn total-items
  "Total number of items in an order."
  [order]
  (->> (:items order)
       (map (fn [[_ m]] (:quantity m)))
       (reduce +)))

(defn delivery-date
  []
  (-> 15 cal/business-days))
#_ (delivery-date)

;; 6 char base 36 order id.
;; - random so not easily guessable
;; - long enough to avoid collisions and be hard to guess
;; - users may need to type in, write down, or reference on phone so
;;   - keep short enough to be human friendly
;;   - don't use lower case letters (base 36 instead of base 62)
(defn- gen-id
  "Generates a new random order id."
  []
  (id/rand-36 6))

;; TODO: Define a schema for an order.

(defn create
  "Creates a new order from the session."
  [{:keys [cart] :as session} payment-txn]
  (let [id (gen-id)
        _ (log/info {:event :order/create :id id})
        result (-> cart
                   (dissoc :counter)
                   (assoc :id id
                          :status :purchased
                          :purchase-date (java.util.Date.)
                          :estimated-delivery-date (-> (delivery-date) .toDate)
                          :payment {:transaction payment-txn})
                   (sd/create :order))]
    (prn "TXN RESULT" result)
    id))

(defn lookup
  "Lookup an order by id."
  [order-id]
  (sd/lookup '[:find ?e
                :in $ ?order-id
                :where [?e :order/id ?order-id]]
              order-id))

;; Check if an order exists.
#_ (let [order-id "LAKTLK"]
     (->> (sd/get-conn)
          d/db
          (d/q '[:find ?e
                 :in ?order-id $
                 :where [?e :order/id ?order-id]]
               order-id
               )))

