(ns sistemi.site.feedback.thanks-htm
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {:es "gracias"})

(def strings
  {:en {:title "SistemiModerni: Thanks"
        :thanks {:title "THANK YOU"
                 :text [:div 
                        [:p "Thank you kindly. We value your feedback and are always looking for ways to improve."]]}}
   :es {}
   :fr {}})

(defn body
  []
  [:div.text_content
   [:p.title (translate :thanks :title)]
   (translate :thanks :text)])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
