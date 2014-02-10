(ns sistemi.site.order.contact-htm
  (:require [ring.util.response :refer [response]]
            [www.form :as f]
            [www.cart :as cart]
            [sistemi.form :as sf]
            [sistemi.site.order :as order]
            [util.frinj :as fu]
            [sistemi.format :as fmt]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]))

(def names {})

(defn head
  [{:keys [locale] :as req}]
  (seq
   [[:script {:src "/jquery-validation/1.11.1/jquery.validate.min.js" :type "text/javascript"}]
    [:script {:src (format "/jquery-validation/1.11.1/messages_%s.js" locale) :type "text/javascript"}]
    [:script {:type "text/javascript"}
     "jQuery(document).ready(function() {
    // Focus the first form element.
    $('#name').focus();

    // Validate form on keyup and submit
    $('#payment-form').validate({
        rules: {
            'name': 'required',
            'email': {
                required: true,
                email: true
            }
        }
    });

});"
     ]]))

(defn body
  [cart]
  [:div {:style {:margin "30px 0px 0px 30px"}}
   (wiz/checkout-wizard :contact)

   [:form#payment-form.payment.form-horizontal {:method "post" :action "contact"}
    [:table {:style {:width "100%"
                     :position "relative" ; note: hack do allow absolute positioning in td on firefox (as it doesn't support position on the td itself).
                     }}

     [:tr
      [:td {:style {:width "50%" :vertical-align "top"}}
       [:fieldset

        [:p.form-header "Contact Information"]

        [:div.control-group
         [:label.control-label {:for "name"} "Name"]
         [:div.controls (f/text :name {:tabindex 1})]]

        [:div.control-group
         [:label.control-label {:for "email"} "Email Address"]
         [:div.controls (f/text :email {:tabindex 1})]]

        [:div.control-group
         [:label.control-label {:for "phone"} "Phone"]
         [:div.controls (f/text :phone {:placeholder "(optional)" :tabindex 1})]]

        ]]

      [:td {:style {:width "50%" :vertical-align "bottom"}}
      
       ;; Have to use absolute positioning here since there is no way
       ;; to have a div fill a table cell with css alone (unless
       ;; specifing an fixed table size) and this needs to be top
       ;; aligned.
       [:div {:style {:position "absolute" :top "0px"
                      :width "50%" ; 50% of table since the absolute positioning is relative to that
                      :text-align "right"}}
        (order/summary cart)
        ]

       ;; grey next
       [:div {:style {:text-align "right" :margin-top "150px"}}
        [:button#submit.btn.btn-inverse.btn-large {:type "submit" :tabindex 1} "Next"]]
       ]]

     ]]
])

(defn handle
  [req]
  (let [s (:session req)
        params (s :contact)]
    (response (layout/standard-page (head req) (f/with-form sf/order-contact params (body (cart/get req))) 0))))
