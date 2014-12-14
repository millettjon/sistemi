(ns sistemi.site.development-htm
  (:require [ring.util.response :as ru]
            [sistemi.layout :as l]
            [sistemi.translate :as tr]))
#_ (remove-ns 'sistemi.site.development-htm)

(def names
  {:es "estantaría"
   :fr "etagères"})

(def strings
  {:en {}
   :es {}
   :it {}
   :fr {}})

(defn body [req]
  (let [p (:params req)
        type (:type p)
        image (:image p)]
    (prn "IMAGE" image)
    [:div {:style {:margin-left "25px" :margin-top "25px"}}
     [:img {:src image :style {:width "625px"}}]
     [:p "product copy for " type]
     [:p "general copy"]

     [:br]
     [:div {:style {:margin-bottom "15px"}}
      [:a {:href (tr/localize "/contact.htm")}
       ;; TODO Pass product information to the contact form.
       [:button#submit.btn.btn-inverse {:typez "submit" :tabindex 1} "Contact Us"]]
      [:a {:href (tr/localize "/")}
       [:button#submit.btn.btn-inverse {:typez "submit" :tabindex 2 :style {:margin-left "20px"}} "Back"]]]
]))

(defn handle
  [req]
  (ru/response (l/standard-page "" (body req) 544)))
