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
        [util.frinj :only [fj-eur fj-round fj-bd_]])
  (:import [java.lang IllegalStateException]
           [java.util.concurrent ExecutionException]
           ))

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

        ;; Calculate subtotal of all items.
        subtotal (or (apply f/fj+ (map #(-> % second :price :total) items))
                     (fj-eur 0))

        ;; Shipping cost of whole order.
        ship-map (if shipping
                   {:shipping (merge shipping (ship/estimate order))})
        shipping (or (get-in ship-map [:shipping :price :total])
                     (fj-eur 0))

        ;; Pretax total.
        pretax-total (f/fj+ subtotal shipping)

        ;; Sales tax.
        tax (if taxable?
              (-> (f/fj* pretax-total france-tax-rate)
                  (fj-round 2))
              (fj-eur 0))

        ;; Grand total, including tax and shipping.
        total (f/fj+ pretax-total tax)

        ;; Adjust to nearest euro.
        ;; Round it, calculate adjustment (to margin), recalc tax and total.
        ;; A dataflow model is looking better!
        ]

;; subtotal    (subtract adjustment from here when displaying)
;; shipping
;; adjustment  (not displayed)  (add this)
;; pretax-total (not displayed) (add this)
;; tax
;; total

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
;;   - use uppercase letters only to avoid confusing letters with numbers
;;
;; 26^6   308,915,776
;; 36^6 2,176,782,336
;;
;; TODO: use a database function to detect (and resolve?) collisions.
;;   ? can the id be generated in the database?
;;   ? how to make sure collision events are logged?
;;
(defn gen-id
  "Generates a new random order id."
  []
  (id/rand-26 6))

;; TODO: Define a schema for an order.
(defn create
  "Creates a new order from the session."
  [{:keys [cart] :as session} {:keys [locale payment-txn] :as args}]
  (let [order (-> cart
                  (dissoc :counter)
                  (assoc :status :purchased
                         :locale locale
                         :purchase-date (java.util.Date.)
                         :estimated-delivery-date (-> (delivery-date) .toDate)
                         :payment {:transaction payment-txn}))]
    (sd/until-unique #(let [id (gen-id)
                            order (assoc order :id id)]
                        (sd/create order :order)
                        order))))

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
