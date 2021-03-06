(ns sistemi.site.cart-htm
  (:require [sistemi.site.product :as p]
            [sistemi.form :as sf]
            [sistemi.product :as product]
            [sistemi.format :as fmt]
            [www.form :as f]
            [www.cart :as cart]
            [sistemi.translate :as tr]
            [hiccup.core :as h]
            [clojure.tools.logging :as log]
            [sistemi.order :as order])
  (:use [ring.util.response :only (response)]
        [sistemi layout]
        [www.wizard :only [wizard]]))

(def names
  {})

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
        :delete "Delete"
        :checkout "Checkout"}
   :es {:title ""}
   :fr {:title ""
        :cart_contents "Votre panier contient les articles suivants"
        :total "Total"
        :subtotal "Sous-total"
        :item "Article"
        :quantity "Quantité"
        :price "Prix"
        :unit_price "Prix Unitaire"
        :copy "Copier"
        :edit "Modifier"
        :delete "Supprimer"
        :checkout "Passer la commande"
        }
   :it {:title ""
        :cart_contents "Il suo carrello contiene gli articoli seguenti"
        :total "Totale"
        :subtotal "Totale parziale"
        :item "Articolo"
        :quantity "Quantità"
        :price "Prezzo"
        :unit_price "Prezzo Unitario"
        :edit "Modifica"
        :copy "Copia"
        :delete "Annulla"
        :checkout "Acquista"}
   })

(defn format-param
  ""
  [item param]
  [:tr
   [:td {:style "text-transform: capitalize;"} (fmt/translate-param item param)]
   [:td.white {:style "padding-left: 10px;"} (fmt/format-value item param)]])

(defn head
  []
  (seq [;; jquery tooltips for pricing
        [:link {:rel "stylesheet" :href "/jquery-tooltip/jquery.tooltip.css"}]
        [:script {:type "text/javascript" :src "/jquery-tooltip/jquery.tooltip.pack.js"}]
        [:script {:type "text/javascript" :src "/js/cart.js"}]

        [:style
         ".link_button {margin:0px; padding:0px; color:#0088CC; background-color:#000; border:0px;}"
         ".link_button:hover {text-decoration:underline; color:#00AAFF;}"
         ".link_button:focus {text-decoration:underline; color:#00AAFF;}"]]))

(defn body-empty
  []
  [:div.text_content
      "Your shopping cart is empty."])

(defn body
  [cart]
  ;; Calculate item prices.
  ;; TODO: format prices based on locale
  (let [items (-> cart :items vals)
        total (-> cart :price :total fmt/eur-short)]
    [:div.text_content

     [:p (tr/translate :cart_contents)]

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
          [:span {:style "font-size: 16px; color: white;"} (tr/translate "/product" type :name)]

          ;; edit button
          ;; - quantity should be included, but not type
          [:form {:method "get" :action (tr/localize (p/urls type)) :style "display:inline;"}
           (f/hidden (assoc (product/to-params item) :quantity quantity))
           [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1 :style "margin-left: 20px;"} (tr/translate :edit) ]]
          
          ;; copy button (should this only be for customizable items?)
          ;; - clear the id
          ;; - add cart params back in
          ;; Commenting out for now:
          #_ [:form {:method "post" :action (tr/localize "/cart/add") :style "display:inline;"}
              (f/hidden (assoc (product/to-params item) :type type :quantity quantity :id -1))
              [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1 :style "margin-left: 10px;"} (tr/translate :copy)]]

          ;; delete button
          [:form {:method "post" :action (tr/localize "/cart/delete") :style "display: inline;"}
           (f/hidden (select-keys item [:id]))
           [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1 :style "margin-left: 10px;"} (tr/translate :delete) ]]]

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
           (f/with-form sf/cart-item-quantity item
             [:form {:method "post" :action (tr/localize "/cart/update")}
              (f/text :quantity {:id (str "quantity" id) :autocomplete "off" :tabindex 1 :style "width: 25px; font-size: 14px; border-color:#383838; border-width:2px; background-color:black; color:#999999; text-align:right; height: 14px; margin:0px;"})
              (f/hidden (select-keys item [:id]))
              [:div
               [:button.link_button {:id (str "quantity_action" id) :type "submit" :tabindex 1 :style "display:none;"} "Delete"]]])]

          ;; price
          [:td {:style "text-align: right; padding-top: 13px;"}
           [:span.white {:style "font-size: 16px;" } (-> item :price :total fmt/eur-short)]]]])

      ;; If there is a shipping address, display subtotal, shipping, and taxes.
      (if (:shipping cart)
        (list [:tr.total.double_rule
               [:td (tr/translate :subtotal)]
               [:td {:style {:text-align "right"} :colspan 3} (-> cart :price :sub-total fmt/eur-short)]]
              [:tr.total
               [:td (tr/translate :tax)]
               [:td {:style {:text-align "right"} :colspan 3} (-> cart :price :tax fmt/eur-short)]]
              [:tr.total
               [:td (tr/translate :shipping)]
               [:td {:style {:text-align "right"} :colspan 3} (-> cart :shipping :price :total fmt/eur-short)]]))

      [(if (:shipping cart)
         :tr.total.white
         :tr.total.double_rule.white) 
       [:td {:style "padding-top: 10px;"} (tr/translate :total) "&nbsp;" (fmt/tax-msg cart)]
       [:td {:style {:text-align "right" :padding-top "10px"} :colspan 3} total]]]

     [:div {:style {:text-align "right" :margin-top "20px" :margin-bottom "20px"}}
      [:a {:href "order/contact.htm"}
       [:button.btn.btn-inverse.btn-large {:type "submit" :tabindex 1} (tr/translate :checkout)]]]

     [:script {:type "text/javascript"}
      "jQuery(document).ready(function() {"
      (
       ;;for [[idx {:keys [id quantity] :as item}] (:items cart)]
       for [{:keys [id quantity] :as item} items]
        (let [detail (-> item :price order/detail-report) #_ (product/html-price-report (product/from-params item))]
          (str
           "sm.cart.quantities['" id  "'] = " quantity ";"
           "$('#quantity" id "').keypress(sm.cart.quantity_keypress);"
           "$('#quantity" id "').keyup(sm.cart.quantity_keyup);"
           "$('#price" id "').tooltip({
              bodyHandler: function() {
                return '" (h/html detail) "'
              },
              delay: 3000,
              left: -50,
              showURL: false
          });")))
      "});"]

     ]))

(defn handle
  [req]
  (let [cart (cart/get req)
        body    (if (cart/empty? cart)
                  (body-empty) 
                  (body cart))]
    (response (standard-page (head) body 544))))
