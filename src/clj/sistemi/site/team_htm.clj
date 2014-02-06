(ns sistemi.site.team-htm
  (:require [util.string :as stru]
            [hiccup.core :as h])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {})

;; Puts this, with locale, on tab tittle
(def strings
  {:en {:title "SistemiModerni: Team"
        :team {:eric {:name "E.M. Romeo" :position "CEO and Creative Director"
                      :description (stru/join-lines
                                    "EE.M.’s 12 Years of designing and directing are the foundation for Sistemi
                                    Moderni.  Elegance, ease of use and above all personalization are the core
                                    of every product you find here. If you want to find out more or you just
                                    want to ask some questions, you can get a direct line by just clicking here." )}
               :jon {:name "Jon Millett" :position "Chief Technical Officer"
                     :description (stru/join-lines
                                   "Jon is the man behind our website.  His programming knowledge spanning 15 years
                                   makes it possible for you to have everything you want, the way you want it with
                                   the greatest of ease.  He has a lot of great ideas.  Why don't you reach out to
                                   Jon with yours?  Just click here.")}
               :nata {:name "Nata Yevich" :position "Chief Design Officer"
                       :description (stru/join-lines
                                      "Nata has spent the last 15 years artistically combining desires and needs
                                      to create stunning private and public environments. Consideration of every
                                      detail is key in bringing unique design to you! Why not reach out to her
                                      with your wishes by clicking here.")}
               :david {:name "Ruff Anderson" :position "Developer"
                       :description (stru/join-lines
                                     "Ruff is our rocket scientist turned software guru. For over 10 years his
                                     been customizing solutions to delicate problems with precision and ingenuity.
                                     Ruff is now applying his genius to developing software solutions for the
                                     design world.  What sort of cool problems do you think Ruff should solve
                                     for you?  Let him know by clicking here.")}}
        
        }
   :es {}
   :fr {:title "Sistemi Moderni: cadres d'entreprise"
        :team {:eric {:name "E.M. Romeo" :position "PDG et Directeur Créatif"
                      :description (stru/join-lines
                                     "E.M. dessine et construit des solutions pour espaces intérieurs depuis 12 ans.
                                     Sa passion de l’innovation l’a inexorablement mené vers sa dernière création,
                                     Sistemi Moderni. L’élégance, la facilite d’utilisation et pardessus tout, la
                                     personnalisation marquent tous les modèles que vous trouverez ici. N’hésitez
                                     pas à le contacter. Cliquez simplement ici.")}
               :jon {:name "Jon Millett" :position "Directeur Technique"
                     :description (stru/join-lines
                                    "Jon crée des structures techniques depuis 15 ans permettant aux sites internet
                                    de vous offrir facilement touts ce dont vous rêvez. Jon a des tas de bonnes idées.
                                    Pourquoi ne pas lui faire parvenir les vôtres? Cliquez simplement ici.")}

               :nata {:name "Nata Yevich" :position "Chef Design"
                       :description (stru/join-lines
                                      "Nata crée des environnements publics et prives qui marient besoins avec
                                      expression artistique depuis plus de 15 ans. La considération des moindres
                                      détails apporte à chaque objet un design unique. Partagez votre inspiration
                                      avec Nata. Cliquez simplement ici.")}

               :david {:name "Ruff Anderson" :position "Developpeur Logiciel"
                       :description (stru/join-lines
                                      "Ruff est un ingénieur en astronautique reconverti en développeur de
                                      logiciels avec une expérience de plus de dix ans a créer des solutions sur
                                      mesure, bibliothèques et optimisation de code d’entreprises. Selon vous, 
                                      sur quels trucs cool Ruff pourrait travailler? Faites lui savoir en cliquant
                                      ici.")}
               }
        }
   :it {:title "Sistemi Moderni:"
        :team {:eric {:name "E.M. Romeo" :position "Amministratore Delegato e Direttore Creativo"
                      :description (stru/join-lines
                                     "I suoi 12 anni di progettazione e di direzione sono il fondamento per
                                     SistemiModerni. L'eleganza, la semplicità d'uso e, soprattutto,la
                                     personalizzazione sono al cuore di ogni prodotto che trovate qui. Se volete
                                     saperne di più o volete semplicemente porre delle domande, è possibile
                                     ottenere una linea diretta, semplicemente cliccando qui.")}
               :jon {:name "Jon Millett" :position "Chief Technical Officer"
                     :description (stru/join-lines
                                    "Jon è l'uomo dietro il nostro sito web. La sua conoscenza della programmazione,
                                    maturata in15 anni d'esperienza, vi rende possibile ottenere le forme che volete,
                                    come le volete, con la massima facilità. Jon e' ricco di grandi idee. Perché non
                                    contattarlo con le vostre?  Basta cliccare qui.")}

               :nata {:name "Nata Yevich" :position "Chief Design Officer"
                       :description (stru/join-lines
                                      "Nata ha trascorso gli ultimi 15 anni unendo artisticamente desideri e
                                      necessità per creare ambienti privati ​​e pubblici mozzafiato. L'esame di ogni
                                      dettaglio è fondamentale per offrirvi un design unico!  Perché non contattare
                                      Nata con i vostri desideri, cliccando qui?")}

               :david {:name "Ruff Anderson" :position "Developer"
                       :description (stru/join-lines
                                      "Ruff è il nostro ingegnere aerospaziale trasformato in guru del software.
                                      Da oltre 10 anni concepisce con precisione ed ingegno delle soluzioni a
                                      problemi delicati.  Ora Ruff applica il suo genio allo sviluppo disoluzioni
                                      software per il mondo del design. Quale tipo di problemi interessanti Ruff
                                      potrebbe risolvere per voi? Fateglielo sapere cliccando qui.")}
               }
        }
   })

(defn body
  []
  [:div.text_content
   ;; Ordered loop that wraps fn in html block
   (for [person [:eric :jon :nata :david]]
     (let [f #(translate :team person %)]
       (h/html
        [:p.title (f :name) " - " (f :position)]
        [:p (f :description)])))])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
