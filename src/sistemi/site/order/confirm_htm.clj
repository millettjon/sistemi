(ns sistemi.site.order.confirm-htm
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [locale.core :as l]
            [sistemi.translate :as tr]
            [www.url :as url])
  (:use paypal
        app.config
        [ring.util.response :only (response)]
        [hiccup core form]
        [sistemi translate layout]))

(def names
  {}
  #_{:es "confirmar"})

(def strings
  {:en {:title "Sistemi Moderni: Complete Purchase"}})

;; TODO: Do we need javascript to disable the confirm button to
;;       prevent a double press? Paypal prevents double processing...

(defn body
  [req details]
  [:div.text_content
   [:p.title "COMPLETE PURCHASE"]
   [:p "Please review your final order and complete the purchase."]

   [:table
    [:caption.white {:style "text-align: left;"} "Order Total"]
    [:tr [:td (:l_desc0 details)] [:td {:style "text-align:right; padding-left: 20px;"} "&euro;" (:itemamt details)]]
    [:tr [:td "Shipping"] [:td {:style "text-align:right; padding-left: 20px;"} "&euro;" (:shippingamt details)]]
    [:tr [:td "Tax"] [:td {:style "text-align:right; padding-left: 20px;"} "&euro;" (:taxamt details)]]
    [:tr.white {:style "border-top: 1px solid white;"} [:td "Total"] [:td.white {:style "text-align:right; padding-left: 20px;"} "&euro;" (:itemamt details)]]]

   [:table {:style "margin-top: 15px; margin-bottom: 15px; width: 100%;" }
    [:caption.white {:style "text-align:left;"} "Shipping Address"
     [:tr [:td (:shiptoname details)]]
     [:tr [:td (:shiptostreet details)]]
     [:tr [:td (:shiptocity details) ", " (:shiptostate details) " " (:shiptozip details)]]]]

   [:form {:action (tr/localize "pay") :method "post"}
    ;; TODO: Store these in the session.
    (map #(apply hidden-field %) (select-keys details [:token :payerid :paymentrequest_0_amt :paymentrequest_0_currencycode]))
    ;;           (hidden-field :paymentrequest_0_paymentaction "Sale") 
    (submit-button "Complete Purchase")]])

(defn handle
  [req]
  (log/info req)
  (with-conf (conf :paypal)
    (let [qp (-> req :query-params normalize-keys)
          details (xc-details (select-keys qp [:token]))]
      (log/info "XC DETAILS" details)
      (response (standard-page "" (body req details) 544)))))

(sistemi.registry/register)

;; ? Should we pass custom data through paypal? e.g., internal order number?
;; ? Is there any way to go back to paypal and re-edit e.g., the shipping address?
;; - or, should we just collect that ourselves?

;; ? What if there is a currency conversion?
;;   ? does paypal send the price in dollars or the conversion rate? (wtf?)
;;     - nope
;;     - note: this shouldn't be an issue for france
;;     ? can we look it up somewhere?
;; ? should we allow location to be switched?
;;   ? should we just check the country code paypal sends back?

;; ORDER DETAILS
;; --------------------------------------------------------
;; Custom Shelving: 63x61x21cm; rectangle; #38BD14   250.00
;; Shipping                                           20.00
;; Tax                                                25.00
;; --------------------------------------------------------
;; Total                                             295.00

;; SHIPPING ADDRESS
;; --------------------------------------------------------
;; JONATHAN MILLETT
;; 23950 butternut
;; sturgis, MI 49091
;; United States

;; [CONFIRM ORDER]

;; currencycode	EUR
;; itemamt	250.00
;; shippingamt	0.00
;; handlingamt	0.00
;; taxamt	0.00
;; amt	250.00

;; l_name0	Custom Shelving
;; l_desc0	Shelving: 63x61x21cm; rectangle; #38BD14

;; firstname	JONATHAN
;; lastname	MILLETT

;; shiptoname	JONATHAN MILLETT
;; shiptostreet	23950 butternut
;; shiptocity	sturgis
;; shiptostate	MI
;; shiptozip	49091
;; shiptocountrycode	US

;; email	buyer_1301327961_per@sistemimoderni.com
