(ns sistemi.site.order.confirmation-htm
  (:require [ring.util.response :refer [response]]
            [www.form :as f]
            [www.cart :as cart]
            [sistemi.site.order :as order]
            [sistemi.form :as sf]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]))

(def names {})

(defn body
  []
  [:div {:style {:margin "30px 0px 0px 30px"}}
   "Order Complete"
   ])

(defn handle
  [req]
  (response (layout/standard-page nil (body) 0)))
