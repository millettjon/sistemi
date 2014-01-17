(ns sistemi.site.order.wizard
  (:use [www.wizard :only [wizard]]))

(defn checkout-wizard
  "Multi step checkout wizard."
  [step]
  (wizard [[:cart [:i.fa.fa-shopping-cart.fa-lg] " Cart"]
           [:contact [:i.fa.fa-user.fa-lg] " Contact"]
           ;;[:delivery [:i.fa.fa-truck.fa-lg] " Delivery"]
           [:payment [:i.fa.fa-credit-card.fa-lg] " Payment"]
           [:confirm [:i.fa.fa-thumbs-up.fa-lg] " Confirm"]
           ]
          step))


;; ? review?
;; if checkout overlay is used, there is no review page
;; how about a receipt page?
