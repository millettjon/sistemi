(ns sistemi.site.order.payment-htm
  (:require [ring.util.response :refer [response]]
            [app.config :as cf]
            [www.form :as f]
            [www.cart :as cart]
            [sistemi.translate :as tr]
            [sistemi.site.order :as order]
            [sistemi.form :as sf]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]))

(def strings
  {:en {:cc "Credit Card"
        :name "Name"
        :number "Number"
        :cvn "CVC"
        :expiration "Expiration"
        :continue "Next"
        :billing-address "Billing Address"
        :address1 "Address 1"
        :address2 "Address 2"
        :city "City"
        :region "Region"
        :postal "Postal Code"
        :country "Country"}
   :fr {:cc "CARTE DE CREDIT"
        :name "Nom"
        :number "Numéro de carte"
        :cvn "Cryptogramme visuel"
        :expiration "Date d’expiration"
        :continue "SUIVANT"
        :billing-address "ADRESSE DE FACTURATION"
        :address1 "Adresse"
        :address2 "Adresse suite"
        :city "Ville"
        :region "remove"
        :postal "Code postal"
        :country "Pays"}
   :it {}
   :es {}})

(def names {})

(defn head
  [{:keys [locale] :as req}]
  (seq
   [[:script {:src "/jquery-validation/1.11.1/jquery.validate.min.js" :type "text/javascript"}]
    [:script {:src (format "/jquery-validation/1.11.1/messages_%s.js" locale) :type "text/javascript"}]

    [:script {:type "text/javascript" :src "https://js.stripe.com/v2/"}]
    ;;[:script {:type "text/javascript" :src "https://js.stripe.com/v2/stripe-debug.js"}]
                                            
    [:script {:type "text/javascript" :src "/js/jquery.payment.js"}]
    [:script {:type "text/javascript"}
     (str "Stripe.setPublishableKey('" (cf/conf :stripe :publishable-key) "');")]
    [:script {:type "text/javascript" :src "/js/order/payment.js"}]]))

(defn body
  [cart]
  [:div {:style {:margin "30px 0px 0px 30px"}}
   (wiz/checkout-wizard :payment)

   ;; placeholder to display payment error messages
   [:p#message.error {:style {:display "none"}} "This is a message"]

   ;; Used for cc fields. Stripe pulls fields labeled with stripe-data
   ;; attribute. These fields are not submitted to the server.
   [:form#cc-form.payment.form-horizontal {:novalidate "true" :autocomplete "on"}
    [:table {:style {:width "100%"
                     :position "relative" ; note: hack do allow absolute positioning in td on firefox (as it doesn't support position on the td itself).
                     }}
     [:tr
      [:td {:style {:width "50%" :vertical-align "top"}}
       [:fieldset

        [:p.form-header (tr/translate :cc)]

        [:span.payment-errors]
        
        [:div.control-group
         [:label.control-label {:for "name"} (tr/translate :name)]
         [:div.controls
          (f/text :name {:data-stripe "name"})
          ]]

        [:div.control-group
         [:label.control-label {:for "number"} (tr/translate :number)]
         [:div.controls [:input#number {:type "text"
                                        :size "20"
                                        :data-stripe "number"
                                        :pattern_ "[\\d ]*"
                                        :x-autocompletetype "cc-number"
                                        :required "true"
                                        :class "cc-number validation"
                                        }]]
         [:span.validation " "]]
        ;; TODO: is :required necessary
        ;; TODO: is span.validation necessary

        [:div.control-group
         [:label.control-label {:for "expiration"} (tr/translate :expiration)]
         [:div.controls
          ;; Note: The expiration input is not submitted. Rather the month and year are parsed from it into hidden fields.
          [:input.cc-exp {:type "text"
                          :pattern "\\d{2} / \\d{2}(\\d{2})?" :x-autocompletetype "cc-exp" :placeholder "MM/YY" :required "true" :maxlength "9"}]]

         [:input.cc-exp-month {:type "hidden" :data-stripe "exp-month"}]
         [:input.cc-exp-year {:type "hidden" :data-stripe "exp-year"}]]

        [:div.control-group
         [:label.control-label {:for "CVC"} (tr/translate :cvn)]
         [:div.controls
          [:input.cc-cvc {:type "text" :data-stripe "cvc"
                          :pattern "\\d*" :x-autocompletetype "cc-csc"
                          :required "true" :autocomplete "off" :maxlength "4"}]]]

        [:p.form-header (tr/translate :billing-address)]

        ;; [:input#sync-address {:type "checkbox" :checked "true"} " Same as shipping address."]

        [:div.control-group
         [:label.control-label {:for "address1"} (tr/translate :address1)]
         [:div.controls (f/text :address1 {:data-stripe "address-line1"})]]

        [:div.control-group
         [:label.control-label {:for "address2"} (tr/translate :address2)]
         [:div.controls (f/text :address2 {:data-stripe "address-line2" :placeholder "(optional)"})]]

        [:div.control-group
         [:label.control-label {:for "city"} (tr/translate :city)]
         [:div.controls (f/text :city {:data-stripe "address_city"})]]

        ;; todo: commented out per Antoine
;        [:div.control-group
;         [:label.control-label {:for "region"} "Region"]
;         [:div.controls (f/text :region {:data-stripe "address-state" :placeholder "(optional)"})]]

        [:div.control-group
         [:label.control-label {:for "code"} (tr/translate :postal)]
         [:div.controls (f/text :code {:data-stripe "address-zip"})]]

        [:div.control-group {:style {:margin-bottom "0px"}}
         [:label.control-label {:for "country"} (tr/translate :country)]
         ;; [:div.controls (f/text :country {:data-stripe "address-country"})]
         [:div.controls (f/select :country {:data-stripe "address-country"})]]

        ]]

      ;; TODO: factor this out
      [:td {:style {:width "50%" :vertical-align "bottom"}}
      
       ;; Have to use absolute positioning here since there is no way
       ;; to have a div fill a table cell with css alone (unless
       ;; specifing an fixed table size) and this needs to be top
       ;; aligned.
       [:div {:style {:position "absolute" :top "0px"
                      :width "50%" ; 50% of table since the absolute positioning is relative to that
                      :text-align "right"}}
        (order/summary cart)]

       ;; grey next
       [:div {:style {:text-align "right" :margin-top "150px"}}
        [:button.btn.btn-inverse.btn-large {:type "submit"} (tr/translate :continue)]]
       ]]

     ]]
])

(defn handle
  [{ {:keys [payment] {:keys [contact shipping]} :cart} :session :as req}]
  (let [params (merge
                ;; default to shipping address (all or nothing)
                (if payment {} (:address shipping))

                ;; default to name from contact data
                (select-keys contact [:name])

                payment)]
    (response (layout/standard-page (head req) (f/with-form sf/order-payment params (body (cart/get req))) 0))))
