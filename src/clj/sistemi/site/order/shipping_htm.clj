(ns sistemi.site.order.shipping-htm
  (:require [ring.util.response :refer [response]]
            [www.form :as f]
            [www.cart :as cart]
            [sistemi.translate :as tr]
            [sistemi.site.order :as order]
            [sistemi.form :as sf]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]))

(def strings
  {:en {:shipping "Shipping Address"
        :name "Name"
        :address1 "Address 1"
        :address2 "Address 2"
        :city "City"
        :region "Region"
        :postal "Postal Code"
        :country "Country"
        :continue "Next"}
   :fr {:shipping "ADRESSE DE LIVRAISON"
        :name "Prénom Nom"
        :address1 "Adresse"
        :address2 "Adresse suite"
        :city "Ville"
        :region "remove"
        :postal "Code postal"
        :country "Pays"
        :continue "SUIVANT"}
   :it {}
   :es {}
   })

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
            'address1': 'required',
            'city': 'required',
            'code': 'required',
            'country': 'required'
        }
    });

});"
     ]]))

(defn body
  [cart]
  [:div {:style {:margin "30px 0px 0px 30px"}}
   (wiz/checkout-wizard :shipping)

   [:form#payment-form.payment.form-horizontal {:method "post" :action "shipping"}
    [:table {:style {:width "100%"
                     :position "relative" ; note: hack do allow absolute positioning in td on firefox (as it doesn't support position on the td itself).
                     }}
     [:tr
      [:td {:style {:width "50%" :vertical-align "top"}}
       [:fieldset

        [:p.form-header (tr/translate :shipping)]

        [:div.control-group
         [:label.control-label {:for "name"} (tr/translate :name)]
         [:div.controls (f/text :name)]]

        [:div.control-group
         [:label.control-label {:for "address1"} (tr/translate :address1)]
         [:div.controls (f/text :address1)]]

        [:div.control-group
         [:label.control-label {:for "address2"} (tr/translate :address2)]
         [:div.controls (f/text :address2 {:placeholder "(optional)"})]]

        [:div.control-group
         [:label.control-label {:for "city"} (tr/translate :city)]
         [:div.controls (f/text :city)]]

        ;; todo: per Antoine's comments
;        [:div.control-group
;         [:label.control-label {:for "region"} (tr/translate :region)]
;         [:div.controls (f/text :region {:placeholder "(optional)"})]]

        [:div.control-group
         [:label.control-label {:for "code"} (tr/translate :postal)]
         [:div.controls (f/text :code)]]

        [:div.control-group {:style {:margin-bottom "0px"}}
         [:label.control-label {:for "country"} (tr/translate :country)]
         [:div.controls (f/select :country {})]
         ]

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
        [:button#submit.btn.btn-inverse.btn-large {:type "submit"} (tr/translate :continue)]]
       ]]

     ]]
])

(defn handle
  [{s :session :as req}]
  (let [address (-> s :cart :shipping :address)
        params (merge (select-keys (s :contact) [:name])
                      (-> address
                          (dissoc :contact)
                          (assoc :name (-> address :contact :name)))
                     (-> s :cart :shipping :address))]
    (response (layout/standard-page (head req) (f/with-form sf/order-shipping params (body (cart/get req))) 0))))
