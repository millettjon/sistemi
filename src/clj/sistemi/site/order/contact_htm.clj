(ns sistemi.site.order.contact-htm
  (:require [ring.util.response :refer [response]]
            [www.form :as f]
            [sistemi.form :as sf]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]))

(def names {})

(defn body
  []
  [:div 
   (wiz/checkout-wizard :contact)

   [:form.form-horizontal {:method "post" :action "contact"}
    [:fieldset
     [:h2 "Contact Information"]

     [:div.control-group
      [:label.control-label {:for "first-name"} "First Name"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :first-name {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "last-name"} "Last Name"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :last-name {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "email"} "Email Address"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :email {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "phone"} "Phone"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :phone {:style "width: 145px" :tabindex 1})]]

     [:h2 "Shipping Information"]

     [:div.control-group
      [:label.control-label {:for "address1"} "Address 1"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :address1 {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "address2"} "Address 2"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :address2 {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "city"} "City"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :city {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "region"} "Region"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :region {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "code"} "Postal Code"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :code {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "country"} "Country"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :country {:style "width: 145px" :tabindex 1})]]

     [:div {:style "text-align: center"}
      [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} "Submit"]]

     ]]

   ;; TODO fix layout
   ;; TODO fix formatting
   ;; TODO fine grained types
   ;; TODO components
   ;; TODO client validations

   ;; submit button
   ;; TODO server validations
])

(defn handle
  [req]
  (let [s (:session req)
        params (merge (s :contact) (s :shipping))]
    (prn params)
    (response (layout/standard-page nil (f/with-form sf/order-contact params (body)) 0))))
