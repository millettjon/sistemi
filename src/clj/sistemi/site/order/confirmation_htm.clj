(ns sistemi.site.order.confirmation-htm
  (:require [ring.util.response :refer [response]]
            [www.form :as f]
            [www.cart :as cart]
            [sistemi.translate :as tr]
            [sistemi.site.order :as order]
            [sistemi.form :as sf]
            [sistemi.format :as fmt]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]))

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

(defn body
  [{{:keys [order]} :session}]
  [:div {:style {:margin "30px 0px 0px 30px"}}

   [:p "Thank you for making your first CODE A NUMBER HERE purchase with
Sistemi Moderni.  Your personalized order has been immediately sent to
the fabricator closest to your product's final destination.  Feel free
to contact us with any questions that may arise while you wait for
your order's speedy delivery.  Below is a summary of your purchase for
your records."]

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
          [:div {:style {:width "64px" :height "64px" :float "left" :margin "8px 10px 0px 0px"}}
           [:img {:src (str "/img/products/" (name type) "-small.png")}]]

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

   [:p "Over the next few weeks, we will be preparing and delivering your
order to you.  If you would like to know your order's fabrication or
delivery status, do not hesitate to send us a note or even call.  We
are working on new systems to automate status reports in real time."]

   [:p "As a way to say thank you, please accept this personal customer
code. code here The code entitles you to a 10 euro discount on your
next purchase.  If you give your code out to a friend or colleague and
they make a purchase, we will add another 10euros towards your next
purchase.  So please spread the code as this will make it easy for us
to repay you for your kindness."]

   [:p "All the best,</br>
E. M. Romeo - President, Sistemi Moderni, SAS "]
   ])

(defn handle
  [req]
  (response (layout/standard-page (head) (body req) 0)))
