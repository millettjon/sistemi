(ns sistemi.site.order.review-htm
  (:require [clojure.tools.logging :as log])
  (:use
   [ring.util.response :only (response)]
   [sistemi translate layout]))

(def names
  {:es "revisar"})

(defn hidden
  "Converts a ring param map into a seq of hidden field tags."
  [m]
  (map
   (fn [[k v]] [:input {:type "hidden" :name (name k) :value v}])
   m))

(defn body
  [params]
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

   [:br]
   [:p "The price for your shelves is " [:span.white "$250"] " before taxes and shipping."]

   [:form {:action "checkout" :method "POST"}
    (hidden params)
    (hidden {:amount "250"})
    [:input {:name "checkout" :type "image" :src "https://www.paypal.com/en_GB/i/btn/btn_xpressCheckout.gif" :align "left" :style "margin-right: 7px;"}]
    ;; TODO: Implement "Edit Specifications" (or is back button ok?).
    #_"or"
    #_[:button#cancel.btn.btn-inverse {:type "submit" :name "cancel" :style "margin-left: 7px;"} "Edit Specifications"]
    ]
   ])

(defn handle
  [req]
  ;; TODO: Validate parameters.
  (response (standard-page "" (body (:params req)) 544)))

(sistemi.registry/register)
