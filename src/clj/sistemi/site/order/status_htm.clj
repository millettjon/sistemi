(ns sistemi.site.order.status-htm
  (:require [ring.util.response :refer [response]]
            [www.form :as f]
            [www.cart :as cart]
            [sistemi.translate :as tr]
            [sistemi.site.order :as order]
            [sistemi.order :as o]
            [sistemi.form :as sf]
            [sistemi.format :as fmt]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]
            [util.calendar :as cal]))

;; PLACES WHERE THE CART/ORDER GETS ENUMERATED
;; Cart
;; Order Confirmation Page
;; Order Status Page
;; Order Confirmation Mail
;;


;; TODO: factor most of these out (shared with cart_htm.clj)
(def strings
  {:en {:title "SistemiModerni: Cart"
        :cart_contents "Your shopping cart contains the following items."
        :total "Total"
        :subtotal "Subtotal"
        :item "Item"
        :quantity "Quantity"
        :price "Price"
        :unit_price "Unit Price"
        :copy "Copy"
        :edit "Edit"
        :delete "Delete"}
   :es {:title ""}
   :fr {:title ""
        :cart_contents "Votre panier contient les articles suivants"
        :subtotal "Sous-total"
        :item "Article"
        :quantity "Quantité"
        :price "Prix"
        :unit_price "Prix Unitaire"
        :copy "Copier"
        :edit "Modifier"
        :delete "Supprimer"
        }
   :it {:title ""
        :cart_contents "Il suo carrello contiene gli articoli seguenti"
        :subtotal "Totale parziale"
        :item "Articolo"
        :quantity "Quantità"
        :price "Prezzo"
        :unit_price "Prezzo Unitario"
        :edit "Modifica"
        :copy "Copia"
        :delete "Annulla"}
   })


(def names {})

;; TODO: factor this out; shared with cart_htm.clj
(defn format-param
  ""
  [item param]
  [:tr
   [:td {:style "text-transform: capitalize;"} (fmt/translate-param item param)]
   [:td.white {:style "padding-left: 10px;"} (fmt/format-value item param)]])

;; TODO: factor out; shared with cart_htm.clj
(defn head
  []
  [:style
   "table.cart {width:100%; margin-top: 15px;}"
   "tr.item {border-top: 2px solid #383838;}"
   "tr.item > td {padding-top:10px; padding-bottom:10px; vertical-align:top;}"
   "tr.total {border-top: 6px double #383838; font-size: 16px; color: white}"
   ".link_button {margin:0px; padding:0px; color:#0088CC; background-color:#000; border:0px;}"
   ".link_button:hover {text-decoration:underline; color:#00AAFF;}"
   ".link_button:focus {text-decoration:underline; color:#00AAFF;}"
   ]
  )

;; TODO: ?should lookup unqualify the keys?

(defn body
  [{{:keys [id]} :params}]
  (let [order (o/lookup id)]
    [:div {:style {:margin "30px 0px 0px 30px"}}

     [:h1 "Order Status"]

     [:p "Your order has been sent to the factory for fabrication."]

     [:p "Order Date: " (-> order :purchase-date cal/format-date-M)]
     [:p "Delivery Date: " (-> order :estimated-delivery-date cal/format-date-M)]

     [:h1 "Order Details"]

     ;; TODO: factor this out
     (let [items (-> order :items vals)
           total (-> order :price :total fmt/eur-short)]
       [:table.cart
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
            (if-not (= type :shelf)
              [:div {:style {:width "64px" :height "64px" :float "left" :margin "8px 10px 0px 0px"}}
               [:img {:src (str "/img/products/" (name type) "-small.png")}]])

            ;; product specs
            ;; ? macro with-type?
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

        [:tr.total
         [:td {:style "padding-top: 10px;"} (tr/translate :total)] [:td {:style {:text-align "right" :padding-top "10px"} :colspan 3} total]]]
       )

     [:br]

     ]))

(defn handle
  [req]
  (response (layout/standard-page (head) (body req) 0)))
