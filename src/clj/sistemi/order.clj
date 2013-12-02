(ns sistemi.order
  "Work with orders."
  (:require [sistemi.format :as fmt]))

(defmulti get-price
  "Calculates the price components for an item."
  :type)

(defn assoc-price
  "Assoc's an item's prices into its map."
  [item]
  (assoc item :price (get-price item)))

(defn detail-report
  "Creates price detail report."
  [m]
  [:div (:workbook m)
   [:table
    (for [[k v] (:parts m)]
      [:tr [:td k] [:td (fmt/format-eur v)]])
    [:tr [:td "unit"]  [:td (-> m :unit fmt/format-eur)]]
    [:tr [:td "total"] [:td (-> m :total fmt/format-eur)]]]])
