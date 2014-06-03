(ns sistemi.order
  "Work with orders."
  (:require [sistemi.format :as fmt]
            [util.id :as id]
            [util.edn :as edn]
            [datomic.api :as d]
            [sistemi.datomic :as sd]
            [taoensso.timbre :as log]
            [frinj.ops :as u]
            [schema.core :as s]
            [util.calendar :as cal]
            [ship])
  (:use [clojure.pprint :only [pprint]]))

(def france-tax-rate (u/fj 0.20))

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

        ;; Recalculate order total (not including shipping).
        total (apply u/fj+ (map #(-> % second :price :total) items))

        ;; Subtotal - sub-total = total / (1 + tax-rate).
        sub-total (if taxable?
                    (u/fj_ total (u/fj+ 1 france-tax-rate)) 
                    total)

        ;; Sales tax.
        tax (if taxable?
              (u/fj* sub-total france-tax-rate)
              (u/fj 0))

        ;; Grand total, include shipping.
        total (if ship-map
                (u/fj+ total (get-in ship-map [:shipping :price :total]))
                total)]

    (-> order
        (merge ship-map)
        (assoc
          :items items
          :price {:sub-total sub-total
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

(def ^:private test-order-id "LUB9ZR")

;; Check if an order exists.
#_ (let [order-id "LAKTLK"]
     (->> (sd/get-conn)
          d/db
          (d/q '[:find ?e
                 :in ?order-id $
                 :where [?e :order/id ?order-id]]
               order-id
               )))

