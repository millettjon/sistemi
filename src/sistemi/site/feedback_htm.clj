(ns sistemi.site.feedback-htm
  (:require [sistemi.form :as sf]
            [www.form :as f])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def company
  [:span.company_name "SistemiModerni"])

(def names
  {:es "sugerencias"
   :fr "impressions"})

(def strings
  {:en {:title "SistemiModerni: Feedback"
        :feedback {:title "WITH YOU FOR YOU"
                   :call-head "Call"
                   :call-body "us at: +33 6 09 46 92 00"
                   :write-head "Write"
                   :write-body "us a note at: "
                   :questionaire-head "Questionaire"
                   :questionaire-body "we are giving out feedback codes\nwhich entitle you to a discount."
                   :text [:div 
                          [:p "We have been working hard to make " company "'s vision a reality. There is"
                           " a road ahead and we want to take it WITH YOU so we can create the best possible"
                           " experience and products FOR YOU. The fact that you are here tells us that you"
                           " are intrigued by what " company " has to offer. Do not stop here! Take a moment"
                           " to really tell us how you feel about " company " and what you would like to see"
                           " us do for you."]
                          [:p "Please click " [:a {:href "#survey"} "here"] " to take a moment and fill out our"
                           " questionnaire. Once you submit it, we will give you a feedback code which entitles"
                           " you to a ¤ 10 discount on your next purchase. If you are sufficiently impressed"
                           " with what we are doing and refer us to a friend, make sure you give them your code"
                           " as each time a friend makes a purchase using your code you will receive an"
                           " additional ¤ 10 of savings. This can add up quickly!"]
                          [:p "We really want your comments good and bad so we can provide you with the best"
                           " service and products. If there is anything you would like to see "company " make"
                           " or change, do not hesitate to contact us. We are always here for you."]
                          [:p "Call us at: +33 6 09 46 92 00" [:br]
                           "Write us a note at: " [:a {:href "mailto:feedback@sistemimoderni.com"} "feedback@sistemimoderni.com"]]
                          [:p "Or"]
                          [:p "Write something cool in the window below:"]]}}
   :es {}
   :fr {:title "SistemiModerni: Vos Impressions"
        :feedback {:title "Vos Impressions – Dites-nous tout!"
                   :call-head "Appelez-nous au:"
                   :call-body "+33 6 09 46 92 00"
                   :write-head "Écrivez-nous à :"
                   :write-body "feedback@sistemimoderni.com"
                   :questionaire-head "Questionnaire :"
                   :questionaire-body "Vous offrir des « feedback codes » qui vous permettront de profiter de remises."
                   :text [:div
                          [:p "Merci d’avoir ouvert cette page et de prendre un moment pour remplir notre"
                           " formulaire."]
                          [:p "Lorsque vous l’aurez envoyé, nous vous adresserons en retour un “feedback code“"
                           " qui permettra de bénéficier d’une remise de 10 € lors de votre prochain achat."
                           " Si vous appréciez ce que nous faisons et que vous recommandez SistemiModerni à des"
                           " amis,  vous pouvez leur communiquer votre feedback code. Ainsi, à chaque fois qu’un"
                           " de vos amis commandera vous bénéficierez de 10€ de remise supplémentaire. Votre remise"
                           " peut devenir très importante et vous permettre d’obtenir des produits SistemiModerni"
                            " gratuits!"]
                          [:p "Pour améliorer nos produits, nous sommes aussi curieux de ce que vous avez apprécié"
                           " que de ce qui vous a déplu. Si vous avez des suggestions à nous faire, nous sommes"
                           " aussi à votre écoute. Alors, n’hésitez pas à nous contacter! Nous sommes toujours à"
                           " votre écoute."]
                          [:p "Ecris un truc cool dans la fenêtre ci-dessous."]]}}

   })

(defn body
  []
  [:div.text_content
   [:p.title (translate :feedback :title)]
   (translate :feedback :text)
    ;; TODO: handle phone and email here
   ;; TODO: collect name, email, and subject
   [:form#feedback {:method "post", :action "feedback", :name "feedback"}
    (f/textarea :message {:class "greytextarea" :tabindex 1})
    [:br]
    [:div {:style "text-align: right"}
     [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} "Submit"]]
    ]])

(defn handle
  [req]
  (response (standard-page "" (f/with-form sf/feedback nil (body)) 544))
  )

(sistemi.registry/register)
