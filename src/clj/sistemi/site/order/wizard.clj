(ns sistemi.site.order.wizard
  (:require [sistemi.translate :as tr])
  (:use [www.wizard :only [wizard]]))

(defn- a-attr
  "Anchor attributes."
  [url]
  {:href (tr/localize url) :tabindex "-1"})

(defn checkout-wizard
  "Multi step checkout wizard."
  [step]
  (wizard [[:cart (a-attr "/cart.htm") [:i.fa.fa-shopping-cart.fa-lg] " Cart"]
           [:contact (a-attr "/order/contact.htm") [:i.fa.fa-user.fa-lg] " Contact"]
           [:shipping (a-attr "/order/shipping.htm") [:i.fa.fa-truck.fa-lg] " Shipping"]
           [:payment (a-attr "/order/payment.htm") [:i.fa.fa-credit-card.fa-lg] " Payment"]]
          step))
