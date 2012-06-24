(ns sistemi.site.order.status-htm
  (:require [paypal.address :as address])
  (:use paypal
        app.config
        [ring.util.response :only (response)]
        [hiccup core]
        [sistemi layout]))

(def names
  {})

(def strings
  {})

(defn body
  [details]
  [:div.text_content
   [:p.title "Order Status"]
   [:p "Thank you for your order."]
   [:p "Your order details are summarized below."]

   [:table {:style "margin-top: 20px"}
    [:caption.white {:style "text-align: left;"} "Charges"]
    [:tr [:td "Shelving"] [:td {:style "text-align:right; padding-left: 20px;"} "&euro;" (:amt details)]]
    [:tr [:td "Shipping"] [:td {:style "text-align:right; padding-left: 20px;"} "&euro;" (:shippingamt details)]]
    [:tr [:td "Tax"] [:td {:style "text-align:right; padding-left: 20px;"} "&euro;" (:taxamt details)]]
    [:tr.white {:style "border-top: 1px solid white;"} [:td "Total"] [:td.white {:style "text-align:right; padding-left: 20px;"} "&euro;" (:amt details)]]]

   [:table {:style "margin-top: 20px" }
    [:caption.white {:style "text-align: left;"} "Shipping Address"
     (map (fn [line] [:tr [:td line]]) (address/format details :shipto))]]

   ;; old table
   #_ [:table.table (map (fn [k] [:tr [:td k] [:td (k details)]])
                      (sort (keys details)))]
   ])

(defn handle
  [req]
  (with-conf (conf :paypal)
    (let [qp (-> req :query-params normalize-keys)
          details (xc-details {:token (qp :id)})]
      (response (standard-page "" (body details) 544)))))

(sistemi.registry/register)
