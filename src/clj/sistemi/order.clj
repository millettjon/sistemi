(ns sistemi.order
  "Work with orders."
  (:require [sistemi.format :as fmt]
            [frinj.ops :as u]))

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
;;   - cart
;;   - purchased
;;   - fabrication
;;   - shipped
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
