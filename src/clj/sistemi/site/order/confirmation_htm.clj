(ns sistemi.site.order.confirmation-htm
  (:require [ring.util.response :refer [response]]
            [sistemi.translate :as tr]
            [sistemi.order :as o]
            [sistemi.site.order.detail :as od]
            [sistemi.layout :as layout]))

(def names {})

(def strings
  {:en {}
   :es {}
   :fr {}
   :it {}})

(defn body
  [{{:keys [id]} :params}]
  (let [order (o/lookup id)]
    [:div#inner-content

     [:p "Thank you for making your first purchase with
Sistemi Moderni.  Your personalized order has been immediately sent to
the fabricator closest to your product's final destination.  Feel free
to " [:a {:href (tr/localize "/contact.htm") :tabindex "-1"} (tr/translate :contact)]
      " us with any questions that may arise while you wait for
your order's speedy delivery.  Below is a summary of your purchase for
your records."]

     [:table
      [:tr [:td "Order Number"] [:td id]]
      ]

     (od/detail order)
     
     [:br]

     [:p "Over the next few weeks, we will be preparing and delivering your
order to you.  If you would like to know your order's fabrication or
delivery status, do not hesitate to send us a note or even call.  We
are working on new systems to automate status reports in real time."]

     ;; Leave out referral code until implemented
     #_ [:p "As a way to say thank you, please accept this personal customer
code. code here The code entitles you to a 10 euro discount on your
next purchase.  If you give your code out to a friend or colleague and
they make a purchase, we will add another 10euros towards your next
purchase.  So please spread the code as this will make it easy for us
to repay you for your kindness."]

     [:p "All the best,</br>
E. M. Romeo - President, Sistemi Moderni, SAS "]
     ]))

(defn handle
  [{{:keys [format]} :params :as req}]
  (let [html (case format
               "email" (layout/email-page (body req))
               (layout/standard-page nil (body req) 0))]
    (response html)))
