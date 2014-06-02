(ns sistemi.site.order
  (:require [sistemi.format :as fmt]
            [util.frinj :as fu]))

(def names
  {:es "orden"})

(defn summary
  "Returns html for the order summary block."
  [cart]
  (let [shipping (or (-> cart :shipping :price :total) (fu/fj-eur 0))
        sub-total (-> cart :price :sub-total)
        tax (-> cart :price :tax)
        total (-> cart :price :total)]
    [:div {:style {:display "inline-block"
                   :padding "5px"
                   :border "2px solid #888"
                   :-webkit-border-radius "5px"
                   :-moz-border-radius "5px"
                   :border-radius "5px"}}
     [:p.form-header "Order Summary"]

     [:table {:style {:float "right" :font-size "14px" :width "100%"}}
      [:tr [:td {:style {:text-align "left" :padding-right "10px"}} "Subtotal"] [:td (-> sub-total fmt/eur-short)]]
      [:tr [:td {:style {:text-align "left"}} "Shipping"] [:td (-> shipping fmt/eur-short)]]
      [:tr [:td {:style {:text-align "left"}} "Tax"] [:td (-> tax fmt/eur-short)]]
      [:tr {:style {:color "#ddd" :border-top "2px solid #5a5a5a"}}
       [:td {:style {:text-align "left" }} "Total"] [:td (-> total fmt/eur-short)]]
      ]]))
