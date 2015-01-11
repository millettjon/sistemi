(ns sistemi.site.order
  (:require [sistemi.format :as fmt]
            [sistemi.translate :as tr]
            [util.frinj :as fu]))

(def names
  {})

(def strings
  {:en {:order "order"
        :payment "payment"
        :summary "Order Summary"
        :sub-total "Subtotal"
        :total "Total"}
   :fr {:order "commande"
        :summary "Récapitulatif de Commande"
        :sub-total "Sous-total"
        :total "Total"}
   :it {}
   :es {}})

(defn summary
  "Returns html for the order summary block."
  [cart]
  (let [shipping (-> cart :shipping :price :total)
        sub-total (-> cart :price :sub-total)
        tax (-> cart :price :tax)
        total (-> cart :price :total)]
    [:div {:style {:display "inline-block"
                   :padding "5px"
                   :border "2px solid #888"
                   :-webkit-border-radius "5px"
                   :-moz-border-radius "5px"
                   :border-radius "5px"}}
     [:p.form-header (tr/translate :summary)]

     [:table {:style {:float "right" :font-size "14px" :width "100%"}}
      [:tr [:td {:style {:text-align "left" :padding-right "10px" :text-transform "capitalize"}} (tr/translate :sub-total)] [:td (-> sub-total fmt/eur-short)]]
      [:tr [:td {:style {:text-align "left" :text-transform "capitalize"}} (tr/translate :shipping)]
       [:td (if shipping
              (-> shipping fmt/eur-short)
              (tr/translate :tbd))]]
      [:tr [:td {:style {:text-align "left" :text-transform "capitalize"}} (tr/translate :tax)]
       [:td (if shipping
              (-> tax fmt/eur-short)
              (tr/translate :tbd))]]
      [:tr {:style {:color "#ddd" :border-top "2px solid #5a5a5a"}}
       [:td {:style {:text-align "left" :text-transform "capitalize"}} (tr/translate :total) "&nbsp;" (fmt/tax-msg cart)] [:td (-> total fmt/eur-short)]]
      ]]))
