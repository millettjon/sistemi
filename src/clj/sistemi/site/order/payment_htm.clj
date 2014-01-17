(ns sistemi.site.order.payment-htm
  (:require [ring.util.response :refer [response]]
            [www.form :as f]
            [sistemi.form :as sf]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]))

(def names {})

(defn body
  []
  [:div 
   (wiz/checkout-wizard :payment)

   [:form.form-horizontal {:method "post" :action "payment"}
    [:fieldset
     [:h2 "Credit Card"]

     [:div.control-group
      [:label.control-label {:for "first-name"} "First Name"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :first-name {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "last-name"} "Last Name"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :last-name {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "cc-number"} "Card Number"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :cc-number {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "cc-number"} "Card Number"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :cc-number {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "cc-month"} "Month"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :cc-month {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "cc-year"} "Year"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :cc-year {:style "width: 145px" :tabindex 1})]]

     [:div.control-group
      [:label.control-label {:for "cc-cvc"} "Cvc"]
      [:div.controls {:style {:margin-left "80px"}}
       (f/text :cc-cvc {:style "width: 145px" :tabindex 1})]]

     [:h2 "Billing Address"]

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

#_     [:div {:style "text-align: center"}
        [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} "Submit"]]

     ]

   ;; stripe
   ;; TODO: insert test key from configuration
   ;; TODO: set the locale
   [:script {:src "https://checkout.stripe.com/checkout.js"
             :class "stripe-button"
             :data-key "pk_test_usNfjQ2YCwhsleYh3JFROP5Q"
             :data-amount "2000"
             :data-name "Demo Site"
             :data-description "2 widgets ($20.00)"
             :data-image "/128x128.png"
             }]
    ]


   ;; TODO add supporting text
   ;; TODO add back buttons w/ wizard
   ;; TODO ? Does it load the values from the session?
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
  (response (layout/standard-page nil (f/with-form sf/order-payment (:params req) (body)) 0)))
