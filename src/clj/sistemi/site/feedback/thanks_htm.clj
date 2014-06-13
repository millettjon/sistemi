(ns sistemi.site.feedback.thanks-htm
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {})

(def strings
  {:en {:title "SistemiModerni: Thank You"
        :thanks {:title "thank you"
                 :text [:div 
                        [:p "Thank you kindly. We value your feedback and are always looking for ways to improve."]]}}
   :fr {:title "SistemiModerni: Merci Beaucoup !"
        :thanks {:title "merci beaucoup !"
                 :text [:div 
                        [:p "Nous avons bien pris en compte vos commentaires. Nous sommes dans une optique d'amélioration continue et l'avis de nos utilisateurs est primordial !"]]}}})

(defn body
  []
  [:div.text_content
   [:p.title {:style {:text-transform "uppercase"}} (translate :thanks :title)]
   (translate :thanks :text)])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
