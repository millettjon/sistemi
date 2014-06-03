(ns sistemi.site.order.detail
  (:require [sistemi.translate :as tr]
            [sistemi.format :as fmt]))

;; Styles (for cart only)
;;   ".link_button {margin:0px; padding:0px; color:#0088CC; background-color:#000; border:0px;}"
;;   ".link_button:hover {text-decoration:underline; color:#00AAFF;}"
;;   ".link_button:focus {text-decoration:underline; color:#00AAFF;}"

(defn format-param
  ""
  [item param]
  [:tr
   [:td {:style {:text-transform "capitalize"}} (fmt/translate-param item param)]
   [:td.white {:style {:padding-left "10px" :text-transform "lowercase"}} (fmt/format-value item param)]])

(defn detail
  [order]
  (let [items (-> order :items vals)
        total (-> order :price :total fmt/eur-short)]
    [:table.order
     [:tr
      [:th {:style "text-align:left;"} (tr/translate :item)]
      [:th {:style "text-align:right"} (tr/translate :unit_price)]
      [:th {:style "text-align:right"} (tr/translate :quantity)]
      [:th {:style "text-align:right"} (tr/translate :price)]]

     (for [{:keys [type id quantity] :as item} (sort #(compare (:id %2) (:id %1)) items)]
       [:tr.item
        [:td
         [:div 
          ;; product name
          [:span {:style "font-size: 16px; color: white;"} (tr/translate "/product" type :name)]]

         ;; product image
         ;; - no shelf image yet
         (if-not (= type :shelf)
           [:div {:style {:width "64px" :height "64px" :float "left" :margin "8px 10px 0px 0px"}}
            [:img {:src (str "/img/products/" (name type) "-small.png")}]])

         ;; product specs
         ;; loop through parameters
         ;; translate labels and values
         [:table
          (for [param (type fmt/parameter-orders)]
            (format-param item param))]

         ;; unit price
         [:td {:style "text-align: right; padding-top: 13px;"}
          [:span {:id (str "price" id) :style "font-size: 16px;" } (-> item :price :unit fmt/eur-short)]]

         ;; quantity
         [:td 
          {:style "text-align:right;"}
          quantity]

         ;; price
         [:td {:style "text-align: right; padding-top: 13px;"}
          [:span.white {:style "font-size: 16px;" } (-> item :price :total fmt/eur-short)]]]])

     [:tr.total.double_rule
      [:td (tr/translate :subtotal)]
      [:td {:style {:text-align "right"} :colspan 3}
       ;; add tax back in to subtotal
       (fmt/eur-short (frinj.ops/fj+ (-> order :price :tax) (-> order :price :sub-total)))]]

     [:tr.total
      [:td (tr/translate :shipping)] [:td {:style {:text-align "right"} :colspan 3} (-> order :shipping :price :total fmt/eur-short)]]

     [:tr.total.white
      [:td (tr/translate :total)] [:td {:style {:text-align "right"} :colspan 3} total]]]))
