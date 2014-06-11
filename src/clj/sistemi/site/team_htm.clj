(ns sistemi.site.team-htm
  (:require [util.string :as stru]
            [hiccup.core :as h]
            [hiccup.element :as el]
            [app.config :as cf])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {})

(defn mailto
  "Creates a mailto link for an email defined in the configuration."
  [who content]
  (el/mail-to (cf/conf :email who) content))

;; Puts this, with locale, on tab tittle
(def strings
  {:en {:title "SistemiModerni: Team"
        :team {:eric {:name "E.M. Romeo" :position "CEO and Creative Director"
                      :description [:p "E.M.’s 12 Years of designing and directing are the foundation for Sistemi
                                    Moderni.  Elegance, ease of use and above all personalization are the core
                                    of every product you find here. If you want to find out more or you just
                                    want to ask some questions, you can get a direct line by just clicking " (mailto :eric "here") "." ]}
               :jon {:name "Jon Millett" :position "Chief Technical Officer"
                     :description [:p "Jon is the man behind our website.  His programming knowledge spanning 15 years
                                   makes it possible for you to have everything you want, the way you want it with
                                   the greatest of ease.  He has a lot of great ideas.  Why don't you reach out to
                                   Jon with yours?  Just click " (mailto :jon "here") "."]}
               :einat {:name "Nata" :position "Chief Design Officer"
                       :description [:p "Nata has spent the last 15 years artistically combining desires and needs
                                      to create stunning private and public environments. Consideration of every
                                      detail is key in bringing unique design to you! Why not reach out to her
                                      with your wishes by clicking " (mailto :einat "here") "."]}
               :dave {:name "Ruff Anderson" :position "Developer"
                      :description [:p "Ruff is our rocket scientist turned software guru. For over 10 years he has
                                     been customizing solutions to delicate problems with precision and ingenuity.
                                     Ruff is now applying his genius to developing software solutions for the
                                     design world.  What sort of cool problems do you think Ruff should solve
                                     for you?  Let him know by clicking " (mailto :dave "here") "."]}
               :antoine {:name "Antoine Gonnot" :position "Communications Director"
                         :description [:p "Antoine is the guardian of Sistemi Moderni's voice.  He
                                       imagines, develops and broadcasts our vision to our audience in the digital
                                       universe.  Are you following our story?  Click on your favorite network
                                       icon at the bottom of the page and let us know what you think!  If you have
                                       any content ideas that you would like to suggest or if you want to help us
                                       spread our passion for custom design, please click " (mailto :antoine "here") " to get Antoine's
                                       undivided attention."]}}}
   :es {}
   :fr {:title "Sistemi Moderni: cadres d'entreprise"
        :team {:eric {:name "E.M. Romeo" :position "PDG et Directeur Créatif"
                      :description [:p "E.M. dessine et construit des solutions pour espaces intérieurs depuis 12 ans.
                                     Sa passion de l’innovation l’a inexorablement mené vers sa dernière création,
                                     Sistemi Moderni. L’élégance, la facilite d’utilisation et pardessus tout, la
                                     personnalisation marquent tous les modèles que vous trouverez ici. N’hésitez
                                     pas à le contacter. Cliquez simplement " (mailto :eric "ici") "."]}
               :jon {:name "Jon Millett" :position "Directeur Technique"
                     :description [:p "Jon crée des structures techniques depuis 15 ans permettant aux sites internet
                                    de vous offrir facilement touts ce dont vous rêvez. Jon a des tas de bonnes idées.
                                    Pourquoi ne pas lui faire parvenir les vôtres? Cliquez simplement " (mailto :jon "ici") "."]}

               :einat {:name "Nata" :position "Chef Design"
                       :description [:p "Nata crée des environnements publics et prives qui marient besoins avec
                                      expression artistique depuis plus de 15 ans. La considération des moindres
                                      détails apporte à chaque objet un design unique. Partagez votre inspiration
                                      avec Nata. Cliquez simplement " (mailto :einat "ici") "."]}

               :dave {:name "Ruff Anderson" :position "Developpeur Logiciel"
                      :description [:p "Ruff est un ingénieur en astronautique reconverti en développeur de
                                      logiciels avec une expérience de plus de dix ans a créer des solutions sur
                                      mesure, bibliothèques et optimisation de code d’entreprises. Selon vous, 
                                      sur quels trucs cool Ruff pourrait travailler? Faites lui savoir en cliquant
                                      " (mailto :dave "ici") "."]}
               :antoine {:name "Antoine Gonnot" :position "Directeur communication"
                         :description [:p "Antoine imagine et développe la présence de l'univers de
                                     Sistemi Moderni auprès de ses publics, notamment grâce au Web digital. Vous
                                     ne suivez pas encore la marque sur les médias sociaux? Cliquez sur les icônes
                                     à gauche en bas de page et faites nous savoir ce que vous aimez ! Si vous avez
                                     des suggestions pour qu'Antoine puisse vous faire découvrir notre passion pour
                                     le design sur-mesure, contactez-le en cliquant " (mailto :antoine "ici") " !"]}
               }
        }
   :it {:title "Sistemi Moderni:"
        :team {:eric {:name "E.M. Romeo" :position "Amministratore Delegato e Direttore Creativo"
                      :description [:p "I suoi 12 anni di progettazione e di direzione sono il fondamento per
                                     SistemiModerni. L'eleganza, la semplicità d'uso e, soprattutto,la
                                     personalizzazione sono al cuore di ogni prodotto che trovate qui. Se volete
                                     saperne di più o volete semplicemente porre delle domande, è possibile
                                     ottenere una linea diretta, semplicemente cliccando " (mailto :eric "qui") "."]}
               :jon {:name "Jon Millett" :position "Chief Technical Officer"
                     :description [:p "Jon è l'uomo dietro il nostro sito web. La sua conoscenza della programmazione,
                                    maturata in15 anni d'esperienza, vi rende possibile ottenere le forme che volete,
                                    come le volete, con la massima facilità. Jon e' ricco di grandi idee. Perché non
                                    contattarlo con le vostre?  Basta cliccare " (mailto :jon "qui") "."]}

               :einat {:name "Nata" :position "Chief Design Officer"
                       :description [:p "Nata ha trascorso gli ultimi 15 anni unendo artisticamente desideri e
                                      necessità per creare ambienti privati ​​e pubblici mozzafiato. L'esame di ogni
                                      dettaglio è fondamentale per offrirvi un design unico!  Perché non contattare
                                      Nata con i vostri desideri, cliccando " (mailto :einat "qui") "?"]}

               :dave {:name "Ruff Anderson" :position "Developer"
                      :description [:p "Ruff è il nostro ingegnere aerospaziale trasformato in guru del software.
                                      Da oltre 10 anni concepisce con precisione ed ingegno delle soluzioni a
                                      problemi delicati.  Ora Ruff applica il suo genio allo sviluppo disoluzioni
                                      software per il mondo del design. Quale tipo di problemi interessanti Ruff
                                      potrebbe risolvere per voi? Fateglielo sapere cliccando " (mailto :dave "qui") "."]}
               }
        }
   })

(defn body
  []
  [:div.text_content
   ;; Ordered loop that wraps fn in html block
   (for [person [:eric :jon :einat :dave :antoine]]
     (let [f #(translate :team person %)]
       [:div 
        [:p.title (f :name) " - " (f :position)]
        [:p (f :description)]]))])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
