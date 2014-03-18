(ns sistemi.order
  "Work with orders."
  (:require [sistemi.format :as fmt]
            [util.id :as id]
            [util.edn :as edn]
            [datomic.api :as d]
            [sistemi.datomic :as sd]
            [taoensso.timbre :as log]
            [frinj.ops :as u]
            [schema.core :as s]))

;; What does the data look like?
;; - should preserve order of addtion (for sorting on cart page)
{:items [{:id -1                ; unique id for line item within the cart
          :item ::shelf         ; item type (must be a valid product id)
          :quantity 1

          ;; item customization attributes
          :attributes {:width 120 ; should this be a frinj #?
                       :depth 30
                       :finish :laquer-matte
                       :color "#AB003B"}}]

 ;; counter that increments for each line item added to the cart
 :count 1
 }

;; ? what are the possible order states?
;;   - cart (kept in the session for now)
;;   - purchased
;;   - building
;;   - shipping
;;   - delivered

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

;; prices - per item
;;   :total
;;   :unit
;;   :parts [...]
;;
;; prices - per order
;;   :subtotal
;;   :discount (if any)
;;   :shipping
;;   :total

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


;; ---------- DATOMIC ----------
;; ? How to persist the order in Datomic?
;; 
;; - create schema to hold an order
;; - create an order
;;
;; order
;;   items (store item details as blob) type, details
;;   price (total)
;;     unit  :eur
;;     value BigDecimal
;;   contact info
;;     name
;;     email
;;     phone
;;   shipping info
;;     address
;;
;; {:order {:price {:total #frinj.core.fjv{:v 157.00M, :u {:EUR 1}}},
;;          :items #ordered/map ([0 {:price {:workbook "shelf/shelf-chain-france.xls", :total #frinj.core.fjv{:v 157.00M, :u {:EUR 1}}, :unit #frinj.core.fjv{:v 157.00M, :u {:EUR 1}}, :parts {:fabrication-stephane #frinj.core.fjv{:v 47.86M, :u {:EUR 1}}, :finishing-marques #frinj.core.fjv{:v 45.36M, :u {:EUR 1}}, :packaging-box #frinj.core.fjv{:v 16.32M, :u {:EUR 1}}, :subtotal #frinj.core.fjv{:v 109.54M, :u {:EUR 1}}, :margin #frinj.core.fjv{:v 21.91M, :u {:EUR 1}}, :tax #frinj.core.fjv{:v 25.76M, :u {:EUR 1}}, :adjustment #frinj.core.fjv{:v -0.21M, :u {:EUR 1}}}},
;;                                   :id 0, :type :shelf, :color {:rgb "#C51D34", :type :ral, :code 3027}, :quantity 1, :finish :laquer-matte, :width 120, :depth 30}])
;;
;; - ups - get shipping estimate
;;   - give priority
;; - bookcase - add cart button
;; - shelf - remove bookcase image and move it to the cart
;; - review final pricing
;; - order notification
;;   - via jarvis
;;   - via stripe
;; - setup stripe
;; - email confirmation - 
;;   - could this be sent manually
;; - code
;;   - see if dave wants to work on it
;; - dave get him setup with tasks for next week
;; - insurance program
;;   - 2 or 3 tiered package; right to return on reception; return in 7 days


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

;; ? How do I build a cart for testing?
;;

#_ (def order-schema
  "Schema for creating an order."
  {:cart {:price {:total 1}}
   :shipping {}
   :contact {}})

;; (def cart-data
;;   "Sample cart data to convert to an order."
;;   {:cart {:price {:total #frinj.core.fjv{:v 157.00M, :u {:EUR 1}}},
;;           :items #ordered/map ([0 {:price {:workbook "shelf/shelf-chain-france.xls", :total #frinj.core.fjv{:v 157.00M, :u {:EUR 1}},
;;                                            :unit #frinj.core.fjv{:v 157.00M, :u {:EUR 1}},
;;                                            :parts {:fabrication-stephane #frinj.core.fjv{:v 47.86M, :u {:EUR 1}},
;;                                                    :finishing-marques #frinj.core.fjv{:v 45.36M, :u {:EUR 1}},
;;                                                    :packaging-box #frinj.core.fjv{:v 16.32M, :u {:EUR 1}},
;;                                                    :subtotal #frinj.core.fjv{:v 109.54M, :u {:EUR 1}},
;;                                                    :margin #frinj.core.fjv{:v 21.91M, :u {:EUR 1}},
;;                                                    :tax #frinj.core.fjv{:v 25.76M, :u {:EUR 1}},
;;                                                    :adjustment #frinj.core.fjv{:v -0.21M, :u {:EUR 1}}}},
;;                                    :id 0,
;;                                    :type :shelf,
;;                                    :color {:rgb "#C51D34", :type :ral, :code 3027},
;;                                    :quantity 1, :finish :laquer-matte, :width 120, :depth 30}]), :counter 0, :status :cart, :taxable true}
;;    :shipping {:region "MI", :code "49091", :city "Sturgis", :address2 "", :address1 "23950 Butternut", :name "Jonathan Millett", :country "USA"},
;;    :contact {:email "jon@millett.net", :phone "7862068250", :name "Jonathan Millett"}})


;; TODO: Move ths somewhere else. util.map?
(defn qualify-keys
  "Qualifies bare keywords in map m with a namespace ns. If they key
ns exists in m and points to a submap, the submap is used instead."
  [m ns]
  (let [m (if (map? (m ns))
            (m ns)
            m)]
    (reduce (fn [m [k v]] (assoc m (keyword (name ns) (name k)) v))
            {} m)))

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
                             (qualify-keys session :contact))

                      ;; shipping address
                      (-> {:db/id #db/id[:main -3]
                           :contact/name (shipping :name)}
                          (merge (qualify-keys shipping :address))
                          (dissoc :address/name))])
    id))

#_ (create {})

;; Create a new order.
#_ (let [conn (get-conn)
         id (gen-id)]
     (d/transact conn [{:db/id (d/tempid partition)
                        :order/id id}]))

;; ? How to convert the price back into a frinj object?
;; ? Should the price just be persisted in the db?
;; - It would be nice to do price calculations.
;;   - it could either be stored as string or component
;; ? How should it work for other units?
;;   - need to store actual units used by customer
;;   - note, units is a map with units as keys and powers as values
;;   - seems like too much pain to represent natively in db
;; can always use read-string to get it out
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

    ;; WTF: can't assoc an ent
    (-> (into {} ent)
        (update-in [:order/total] edn/read-string)
        (update-in [:order/items] edn/read-string))
;    ent
))

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

;; Update the total price for an order.

;; Hmm, TDD using in memory database?

;; TODO: get database browser working
