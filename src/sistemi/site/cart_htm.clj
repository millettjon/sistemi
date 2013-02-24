(ns sistemi.site.cart-htm
  (:require [sistemi.site.product :as p]
            [sistemi.form :as sf]
            [sistemi.model :as model]
            [sistemi.model.format :as fmt]
            [www.form :as f]
            [www.cart :as cart]
            [sistemi.translate :as tr]
            [hiccup.core :as h]
            [clojure.tools.logging :as log]
            )
  (:use [ring.util.response :only (response)]
        [sistemi layout]))

(def names
  {})

(def strings
  {:en {:title "SistemiModerni: Cart"}
   :es {}
   :fr {}})

(defn format-param
  ""
  [item param]
  [:tr
   [:td {:style "text-transform: capitalize;"} (fmt/translate-param item param)]
   [:td.white {:style "padding-left: 10px;"} (fmt/format-value item param)]])

(defn total
  "Calculates the total price of all cart items."
  [items]
  (apply frinj.calc/fj+ (map :price items)))

(defn head
  []
  (seq [;; jquery tooltips for pricing
        [:link {:rel "stylesheet" :href "/jquery-tooltip/jquery.tooltip.css"}]
        [:script {:type "text/javascript" :src "/jquery-tooltip/jquery.tooltip.pack.js"}]
        [:script {:type "text/javascript" :src "/js/cart.js"}]

        [:style
         "table.cart {width:100%; margin-top: 15px;}"
         "tr.item {border-top: 2px solid #383838;}"
         "tr.item > td {padding-top:10px; padding-bottom:10px; vertical-align:top;}"
         "tr.total {border-top: 6px double #383838; font-size: 16px; color: white}"
         ".link_button {margin:0px; padding:0px; color:#0088CC; background-color:#000; border:0px;}"
         ".link_button:hover {text-decoration:underline; color:#00AAFF;}"
         ".link_button:focus {text-decoration:underline; color:#00AAFF;}"
         ]]))

(defn body-empty
  []
  [:div.text_content "Your shopping cart is empty."])

(defn body
  [cart]
  ;; Calculate item prices.
  (let [items (map #(-> % model/from-params model/calc-price) (vals (:items cart)))
        total (fmt/format-eur (total items))]
    [:div.text_content

     [:p "Your shopping cart contains the following items."]

     [:table.cart
      [:tr
       [:th {:style "text-align:left;"} "Item"]
       [:th {:style "text-align:right"} "Unit Price"]
       [:th {:style "text-align:right"} "Quantity"]
       [:th {:style "text-align:right"} "Price"]]
      (for [{:keys [type id quantity] :as item} (sort #(compare (:id %2) (:id %1)) items)]
        [:tr.item
         [:td
          ;; product name
          [:span {:style "font-size: 16px; color: white;"} (tr/translate "/product" type :name)]

          ;; edit button
          ;; - quantity should be included, but not type
          [:form {:method "get" :action (tr/localize (p/urls type)) :style "display:inline;"}
           (f/hidden (assoc (model/to-params item) :quantity quantity))
           [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1 :style "margin-left: 20px;"} "Edit"]]
          
          ;; copy button (should this only be for customizable items?)
          ;; - clear the id
          ;; - add cart params back in
          ;; where does finish come from? :matte instead of ::matte
          [:form {:method "post" :action (tr/localize "/cart/add") :style "display:inline;"}
           (f/hidden (assoc (model/to-params item) :type type :quantity quantity :id -1))
           [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1 :style "margin-left: 10px;"} "Copy"]]

          ;; delete button
          [:form {:method "post" :action (tr/localize "/cart/delete") :style "display: inline;"}
           (f/hidden (select-keys item [:id]))
           [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1 :style "margin-left: 10px;"} "Delete"]]

          ;; product specs
          ;; ? macro with-type?
          ;; loop through parameters
          ;; translate labels and values
          [:table
           (for [param (type fmt/parameter-orders)]
             (format-param item param))]

          ;; unit price
          [:td {:style "text-align: right; padding-top: 13px;"}
           [:span {:id (str "price" id) :style "font-size: 16px;" } (-> item :unit-price fmt/format-eur)]]

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
           [:span.white {:style "font-size: 16px;" } (-> item :price fmt/format-eur)]]]])

      [:tr.total
       [:td {:style "padding-top: 10px;" :colspan "3"} "Subtotal"] [:td {:style "text-align:right; padding-top:10px;"} total]]]

     [:script {:type "text/javascript"}
      "jQuery(document).ready(function() {"
      (for [[idx {:keys [id quantity] :as item}] (:items cart)]
        (let [detail (model/html-price-report (model/from-params item))]
          (str
           "sm.cart.quantities['" id  "'] = " quantity ";"
           "$('#quantity" id "').keypress(sm.cart.quantity_keypress);"
           "$('#quantity" id "').keyup(sm.cart.quantity_keyup);"
           "$('#price" id "').tooltip({
              bodyHandler: function() {
                return '" (h/html detail) "'
              },
              delay: 3000,
              left: -400,
              showURL: false
          });")))
      "});"]
     ]))

;; ajax quanity update
;; [item-id, quantity] -> cart/quantity -> returns [updated subtotal for cart]

;; TODO: thumbnail rendered image
;; TODO: delivery date (est)
;; ajax operations require client side calculatons
;; - quantity update -> recalculate subtotal
;; - delete -> remove section of display
;;          -> recalculate subtotal

;; actions edit, delete, increment/decrement/edit quantity
;;   ? does there need to be an update button for editing quantity?
;; calculate: price
;;   ? how is the price calculated for each line item?
;;     - fn saved in item meta-data? price-fn model.shelving/price or model.shelf/price
;;       ? use lookup table?
;;       ? use multi-method with dispatch on :type? (how does that scale?)
;;
;; ? how to build action links?
;;   - edit item-id     ? how to get edit page link? (/shelf.htm, /shelving.htm)
;;                        ? save edit url in cart as meta-data?
;;                          ? what if they change languages?
;;                            store canonical url and translate
;;                          ? use lookup table?
;;                        ? how to build query string?
;;                          - include all design parameters
;;                          - include item-id
;;                            - if item id is present, design page should "update" instead of "add" to cart.
;;   - delete item-id   /cart/delete?id=0
;;   - update quantity  6 +- [update]
;;     - find +/- icons
;;     - make a quantity control
;;
;; - A lookup table seems better than storing price fn and design url in cart.
;;   ? how well do multi-methods scale?
;;
;; TODO totals
;; TODO delivery date
;; TODO shipping cost
;; TODO VAT

(defn handle
  [req]
  (let [cart (cart/get req)
        body    (if (cart/empty? cart)
                  (body-empty) 
                  (body cart))]
    (response (standard-page (head) body 544))))
