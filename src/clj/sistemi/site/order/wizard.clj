(ns sistemi.site.order.wizard
  (:require [sistemi.translate :as tr])
  (:use [www.wizard :only [wizard]]))

;; todo: what about 'cart' in site.clj, this is not a page, so will it have access to site/strings?
(def strings
  {:en {:cart " Cart"
        :contact " Contact"
        :shipping " Shipping"
        :payment " Payment"}
   :fr {:cart " PANIER"
        :contact " CONTACT"
        :shipping " LIVRAISON"
        :payment " PAIEMENT"}
   :it {:cart " "
        :contact " "
        :shipping " "
        :payment " "}
   :es {}
   })

(defn- a-attr
  "Anchor attributes."
  [url]
  {:href (tr/localize url) :tabindex "-1"})

(defn checkout-wizard
  "Multi step checkout wizard."
  [step]
  (wizard [[:cart (a-attr "/cart.htm") [:i.fa.fa-shopping-cart.fa-lg] (tr/translate :cart)]
           [:contact (a-attr "/order/contact.htm") [:i.fa.fa-user.fa-lg] (tr/translate :contact)]
           [:shipping (a-attr "/order/shipping.htm") [:i.fa.fa-truck.fa-lg] (tr/translate :shipping)]
           [:payment (a-attr "/order/payment.htm") [:i.fa.fa-credit-card.fa-lg] (tr/translate :payment)]]
          step))
