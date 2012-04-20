(ns sistemi.site.order.confirm-htm
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [sistemi.translate :as tr]
            [www.url :as url])
  (:use paypal
        app.config
        [locale.core :only (full-locale)]
        [ring.util.response :only (response)]
        [hiccup core form-helpers]
        [sistemi translate layout]))


;; confirm.htm
;; - gets order details from paypal
;; - calculates final shipping, tax, and total
;; - displays final order preview to user
;; - includes "confirm" button
;; - submits POST to "pay"

;; pay
;; - captures payment w/ paypal
;; - sends email summary of order
;; - redirects to view order page

;; view.htm?id=xyz
;; The .htm extension is superflous...
;;   - dynamic responses include a content-type...
;;   - slightly useful for editing template files?
;;   - distinguishes between urls w/ views and actions only
;; ? Should restful urls be used?
;;   - /order/1

;; TODO: Do we need javascript to disable the confirm button to
;;       prevent a double press? Paypal prevents double processing...

(defn body
  [req details]
  [:div.text_content
   [:p.title "COMPLETE PURCHASE"]
   [:p "Please review your final order and complete the purchase."]
   [:h2 "Order Details"]
   [:table (map (fn [k] [:tr [:td k] [:td (k details)]])
                (sort (keys details)))]
   [:form {:action (tr/localize "pay") :method "post"}
    ;; TODO: Store these in the session.
    (map #(apply hidden-field %) (select-keys details [:token :payerid :paymentrequest_0_amt :paymentrequest_0_currencycode]))
    ;;           (hidden-field :paymentrequest_0_paymentaction "Sale") 
    (submit-button "Complete Purchase")]])

;; http://localhost:5000/en/order/confirm.htm?token=EC-95235306K2096024R&PayerID=YGZNZL2T74SPS
(defn handle
  [req]
  (log/info req)
  (with-conf (conf :paypal)
    (let [qp (-> req :query-params normalize-keys)
          details (xc-details (select-keys qp [:token]))]
      (log/info "XC DETAILS" details)
      (response (standard-page "" (body req details) 544)))))

(sistemi.registry/register)
