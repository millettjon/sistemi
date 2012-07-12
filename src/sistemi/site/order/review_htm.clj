(ns sistemi.site.order.review-htm
  (:require [clojure.tools.logging :as log]
            [ring.util.response :as resp]
            [sistemi.model.shelving :as shelving]
            [sistemi.translate :as tr]
            [sistemi.form :as sf]
            [sistemi.layout :as layout]
            [www.form :as f]
            [www.url :as url])
  (:use [ring.util.response :only (response)]))

(def names
  {:es "revisar"})

(defn head
  []
  (seq [;; jquery tooltips for pricing
        [:link {:rel "stylesheet" :href "/jquery-tooltip/jquery.tooltip.css"}]
        [:script {:type "text/javascript" :src "/jquery-tooltip/jquery.tooltip.pack.js"}]

        ]))

(defn body
  [params]
  (let [shelving (shelving/from-params params)
        price (shelving/price shelving)
        detail (shelving/html-price-report shelving)]
    [:div.text_content
     [:p.title "REVIEW DESIGN"]
     [:p "Please review your shelving specifications."]
     [:table.twocol
      [:tr [:td "Width"] [:td.white (str (:width params) " cm")]]
      [:tr [:td "Height"] [:td.white (str (:height params) " cm")]]
      [:tr [:td "Depth"] [:td.white (str (:depth params) " cm")]]
      [:tr [:td "Cutout"] [:td.white (:cutout params)]]
      [:tr [:td "Color"] [:td.white (:color params)
                          "&nbsp;&nbsp" [:span.label {:style (str "background-color: " (:color params) ";")} "&nbsp;&nbsp"]]]]

     ;; TODO: Use standard/localized currency formatting
     [:p {:style "margin-top: 10px;"} "The price of your custom shelving is " [:span#price.white (str "&euro;" (:v price))] " before taxes and shipping."]

     [:p {:style "margin-top: 18px;"} "If the specifications are correct you can checkout using PayPal."]

     [:form {:action "checkout" :method "POST" :style "margin: 0"}
      (f/hidden params)
      (f/hidden {:amount (:v price)})
      ;; TODO: move button rendering into paypal namespace?
      [:input {:name "checkout" :type "image" :src (str "https://www.paypal.com/" (tr/full-locale) "/i/btn/btn_xpressCheckout.gif")}]]

     [:p {:style "margin-top: 18px;"} "If the specifications are incorrect you can return to the previous page to edit them."]

     [:form {:action (tr/localize "/shelving.htm") :method "GET"}
      (f/hidden params)
      [:button#submit.btn.btn-inverse {:type "submit"} "Edit Specifications"]]

    [:script {:type "text/javascript"}
     "jQuery(document).ready(function() {
       $('#price').tooltip({
         bodyHandler: function() {
           //return $($(this).attr('href')).html();
           return '" detail "'
         },
         delay: 3000,
         left: -400,
         showURL: false
       });
     });"]
     ]))

(defn handle
  [req]
  (f/with-form sf/shelving (:params req)
    (if (f/errors?)
      (resp/redirect (tr/localize "/shelving.htm" {:query (:params req)}))
      (response (layout/standard-page (head) (body (f/values)) 544)))))

(sistemi.registry/register)
