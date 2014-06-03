(ns sistemi.site.order.status-htm
  (:require [ring.util.response :refer [response]]
            [sistemi.translate :as tr]
            [sistemi.order :as o]
            [sistemi.site.order.detail :as od]
            [sistemi.layout :as layout]
            [util.calendar :as cal]))

(defn body
  [{{:keys [id]} :params}]
  (let [order (o/lookup id)]
    [:div {:style {:margin "30px 0px 0px 30px"}}

     [:h1 "Order Status"]

     [:p "Your order has been sent to the factory for fabrication."]

     [:p "Order Date: " (-> order :purchase-date cal/format-date-M)]
     [:p "Delivery Date: " (-> order :estimated-delivery-date cal/format-date-M)]

     [:h1 "Order Details"]

     (od/detail order)

     [:br]

     ]))

(defn handle
  [req]
  (response (layout/standard-page nil (body req) 0)))
