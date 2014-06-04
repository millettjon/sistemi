(ns sistemi.site.order.contact
  "Receives posted contact information."
  (:require [sistemi.translate :as tr]
            [ring.util.response :as resp]
            [www.session :as sess]
            [sistemi.form :as sf]
            [www.cart :as cart]
            [www.form :as f]))

(defn update-contact
  [cart contact]
  (-> cart
      (assoc :contact contact)))


(defn map-filter-empty
  "Filters empty values from a map."
  [m]
  (->> m
       (remove (comp empty? second))
       (into {})))
#_ (map-filter-empty {})
#_ (map-filter-empty {:a "A"})
#_ (map-filter-empty {:a "A" :b nil :c "" :d "D"})

(defn handle
  [req]
  (f/with-valid-form sf/order-contact (:params req)
    (let [values (map-filter-empty (f/values))]
      (-> (tr/localize "shipping.htm")
          resp/redirect
          (assoc :session
            (assoc (:session req) :contact values))
          (cart/swap req update-contact values)))))
