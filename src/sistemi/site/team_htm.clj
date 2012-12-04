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
               :einat {:name "Einat Grinbaum" :position "Chief Design Officer"
                       :description (stru/join-lines
                                      "Einat has spent the last 15 years artistically combining desires and needs
                                      to create stunning private and public environments. Consideration of every
                                      detail is key in bringing unique design to you! Why not reach out to her
                                      with your wishes by clicking here.")}
               :david {:name "David Millett" :position "Developer"
                       :description (stru/join-lines
                                     "David is our rocket scientist turned software guru. For over 10 years his
                                     been customizing solutions to delicate problems with precision and ingenuity.
                                     David is now applying his genius to developing software solutions for the
                                     design world.  What sort of cool problems do you think David should solve
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

               :einat {:name "Einat Grinbaum" :position "Chef Design"
                       :description (stru/join-lines
                                      "Einat crée des environnements publics et prives qui marient besoins avec
                                      expression artistique depuis plus de 15 ans. La considération des moindres
                                      détails apporte à chaque objet un design unique. Partagez votre inspiration
                                      avec Einat. Cliquez simplement ici.")}

               :david {:name "David Millett" :position "Developpeur Logiciel"
                       :description (stru/join-lines
                                      "David est un ingénieur en astronautique reconverti en développeur de
                                      logiciels avec une expérience de plus de dix ans a créer des solutions sur
                                      mesure, bibliothèques et optimisation de code d’entreprises. Selon vous, 
                                      sur quels trucs cool David pourrait travailler? Faites lui savoir en cliquant
                                      ici.")}
               }
        }
   })

(defn body
  []
  [:div.text_content
   ;; Ordered loop that wraps fn in html block
   (for [person [:eric :jon :einat :david]]
     (let [f #(translate :team person %)]
       (h/html
        [:p.title (f :name) " - " (f :position)]
        [:p (f :description)])))])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))

(sistemi.registry/register)
