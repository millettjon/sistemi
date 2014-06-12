(ns sistemi.site.feedback-htm
  (:require [sistemi.form :as sf]
            [www.form :as f]
            [sistemi.translate :as tr])
  (:use [ring.util.response :only (response)]
        [sistemi layout]))

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
                          #_ [:p "Please click " [:a {:href "#survey"} "here"] " to take a moment and fill out our"
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
   :it {:title "SistemiModerni: Con Lei e per Lei"
        :feedback {:title "Con Lei e per Lei"
                   :call-head ""
                   :call-body "+33 (0)609 469200"
                   :write-head ""
                   :write-body "feedback@sistemimoderni.com"
                   :questionaire-head ""
                   :questionaire-body ""
                   :text [:div
                          [:p "Stiamo lavorando molto per realizzare la visione di Sistemi Moderni. Abbiamo ancora"
                           " una lunga strada davanti a noi, e vogliamo percorrerla con Lei, mirando a creare i migliori"
                           " prodotti e la miglior esperienza possibile per Lei. Il fatto stesso che Lei stia leggendo"
                           " questa pagina ci indica il suo interesse per ciò che Sistemi Moderni può offrire. Non si"
                           " fermi solo alla lettura! La preghiamo invece di prendere un momento per spiegarci le sue"
                           " impressioni a riguardo di Sistemi Moderni, e cosa desidererebbe che noi facessimo per Lei."]
                          [:p "Per favore segua questo link per riempire un questionario che le occuperà soltanto un"
                           " minuto.  La ricompenseremo spedendole un \"codice di sconto feedback\" che le permetterà"
                           " di risparmiare 10 euro sul suo prossimo acquisto con Sistemi Moderni. In più, se lei"
                           " trova i nostri prodotti interessanti e li propone ad un amico, lo incoraggi ad indicare"
                           " il suo codice al momento dell'acquisto: questo aggiungerà altri 10 euro al vostro sconto..."
                           " ed in questo modo gli sconti si possono accumulare velocemente!"]
                          [:p "Contiamo molto sui Suoi suggerimenti ed i Suoi commenti, positivi o negativi che siano,"
                           " perché è nostro scopo sincero migliorare i nostri servizi e prodotti. Per qualunque richiesta"
                           " o suggerimento, non esitate a contattarci: saremo sempre a sua disposizione."]
                          [:p "per telefono: +33 (0)609 469200"]
                          [:p "per email: feedback@sistemimoderni.com"]
                          [:p "o usando il formulario nella finestra qui sotto"]]
                   }}

   :fr {:title "SistemiModerni: Vos Impressions"
        :feedback {:title "Vos Impressions – Dites-nous tout!"
                   :call-head "Appelez-nous au:"
                   :call-body "+33 6 09 46 92 00"
                   :write-head "Écrivez-nous à :"
                   :write-body "feedback@sistemimoderni.com"
                   :questionaire-head "Questionnaire :"
                   :questionaire-body "Vous offrir des « feedback codes » qui vous permettront de profiter de remises."
                   :text [:div
                          [:p "Merci d’avoir ouvert cette page et de prendre un moment pour remplir notre formulaire."]
                          [:p "Nous vous adresserons en retour un “" [:span.bullet_title "feedback code"] "“ qui vous permettra de bénéficier d’une remise de 10 € lors de votre prochain achat. Si vous appréciez ce que nous faisons, faites connaître SistemiModerni à vos amis et communiquez leur votre feedback code. Ainsi, à chaque commande, vous bénéficierez de 10 € de remise supplémentaire. 10 €  + 10 €  + 10 € … : vous pourrez peut-être obtenir des produits SistemiModerni gratuits !"]
                          [:p [:span.bullet_title "Dites-nous tout"] " ! Nous avons hâte de vous lire : avez-vous apprécié notre démarche, nos articles ? Comment pouvons-nous améliorer nos services ? Nous sommes à votre écoute, vos critiques, vos idées, vos suggestions seront les bienvenues…"]
                          [:p "Ecris un truc cool dans la fenêtre ci-dessous."]]}}})

(defn placeholder
  [k]
  (str "(" (tr/translate k) ")"))

(defn body
  []
  [:div.text_content
   [:p.title (tr/translate :feedback :title)]
   (tr/translate :feedback :text)
   [:form#feedback {:method "post", :action "feedback", :name "feedback"}
    (f/text :email {:placeholder (placeholder :email) :tabindex 1})
    (f/textarea :message {:class "feedback_textarea" :placeholder (placeholder :message) :tabindex 1})
    [:br]
    [:div {:style "text-align: right"}
     [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} "Submit"]]
    ]])

(defn handle
  [req]
  (response (standard-page "" (f/with-form sf/feedback nil (body)) 544)))
