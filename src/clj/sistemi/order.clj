(ns sistemi.order
  "Work with orders."
  (:require [sistemi.format :as fmt]
            [util.id :as id]
            [util.edn :as edn]
            [datomic.api :as d]
            [sistemi.datomic :as sd]
            [taoensso.timbre :as log]
            [util.map :as m]
            [frinj.ops :as u]
            [schema.core :as s]))

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
  [{:keys [items] :as order}]
  (let [;; Recalculate prices of each line item.
        items (reduce (fn [items [i item]] (assoc-in items [i :price] (get-price item order)))
                      items
                      items)

        ;; Recalculate order total.        
        total (apply u/fj+ (map #(-> % second :price :total) items))]
    (assoc order
      :items items
      :price {:total total})))

(defn total-items
  "Total number of items in an order."
  [order]
  (->> (:items order)
       (map (fn [[_ m]] (:quantity m)))
       (reduce +)))


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

(defn create
  "Creates a new order from the session."
  [{:keys [cart shipping contact] :as session}]
  (let [conn (sd/get-conn)
        id (gen-id)]
    (log/info {:event :order/create :id id})
    (d/transact conn [{:db/id #db/id[:main -1]
                       :order/id id
                       :order/items (-> cart :items pr-str) ; coerce to string
                       :order/total (-> cart :price :total pr-str) ; coerce to string
                       :order/contact #db/id[:main -2]
                       :order/shipping-address #db/id[:main -3]
                       :order/status :purchased}

                      ;; contact info
                      (merge {:db/id #db/id[:main -2]}
                             (m/qualify-keys session :contact))

                      ;; shipping address
                      (-> {:db/id #db/id[:main -3]
                           :contact/name (shipping :name)}
                          (merge (m/qualify-keys shipping :address))
                          (dissoc :address/name))])
    id))

(defn lookup
  "Retrieve an order and all components by order id."
  [order-id]
  (let [db (d/db (sd/get-conn))
        eid (d/q '[:find ?e
                   :in $ ?order-id
                   :where [?e :order/id ?order-id]]
                 db order-id)
        ent (->> eid
                 ffirst
                 (d/entity db)
                 d/touch)]

    ;; hmm, can't assoc an ent directly
    (-> (into {} ent)
        (update-in [:order/total] edn/read-string)
        (update-in [:order/items] edn/read-string))))

#_ (let [order-id "SG2ZY5"
         db (d/db (sd/get-conn))
         eid (d/q '[:find ?e
                    :in $ ?order-id
                    :where [?e :order/id ?order-id]]
                  db order-id)
         ent (d/entity db (ffirst eid))]
     (-> ent d/touch))

;; List all orders ids.

;; Check if an order exists.
#_ (let [order-id "2Z1HPK"]
     (->> (sd/get-conn)
          d/db
          (d/q '[:find ?e
                 :in ?order-id $
                 :where [?e :order/id ?order-id]]
               order-id
               )))

;; Retrieve an order and all components.
#_ (let [order-id "SG2ZY5"
         db (d/db (sd/get-conn))
         eid (d/q '[:find ?e
                    :in $ ?order-id
                    :where [?e :order/id ?order-id]]
                  db order-id)
         ent (d/entity db (ffirst eid))]
     (-> ent d/touch))

