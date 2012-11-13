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
                                    "He has been designing and
                                     building home interior solutions for the last 12 years. E.M.'s
                                     passion for innovation has led him inexhorably towards the latest of
                                     his creations, Sistemi Moderni. Elegance, ease of use and above all
                                     personalization mark all the designs that you will find here.
                                     Do not hesitate to call or write to him. Just click here.")}
               :jon {:name "Jon Millett" :position "Chief Technical Officer"
                     :description (stru/join-lines
                                   "Jon has been creating technical frameworks for 15 years (?) enabling websites
                                    to offer you everything you ever wanted. Jon has a lot of great ideas. Why
                                     don't you reach out to him with yours?  Just click here.")}
               :einat {:name "Einat Grinbaum" :position "Chief Design Officer"
                       :description (stru/join-lines
                                     "Einat has been creating public and private environments combining
                                     social needs with artistic expression for over 15 years. Consideration
                                     of every detail is key in bringing to you a unique sculptural piece.
                                     Share with her your inspiration. Just click here.")}
               :david {:name "David Millett" :position "Developer"
                       :description (stru/join-lines
                                     "David, with an aerospace and software background, is helping adapt custom
                                     fabrication across multiple platforms to provide high quality products
                                     with precision and affordability.")}}
        
        }
   :es {}
   :fr {:title "Sistemi Moderni: cadres d'entreprise"
        :team {:eric {:name "E.M. Romeo" :position "PDG et Directeur Créatif"
                      :description (stru/join-lines
                                     "E.M. dessine et construit des solutions pour espaces intérieurs depuis
                                      12 ans. Sa passion de l’innovation l’a inexorablement mené vers sa dernière
                                       création, Sistemi Moderni. L’élégance, la facilite d’utilisation et
                                       pardessus tout, la personnalisation marquent tous les modèles que vous
                                        trouverez ici. N’hésitez pas à le contacter. Cliquez simplement ici.")}
               :jon {:name "Jon Millett" :position "Directeur Technique"
                     :description (stru/join-lines
                                    "Jon crée des structures techniques depuis 15 ans permettant aux sites
                                     internet de vous offrir tous ce dont vous rêvez. Jon a des tas de bonnes
                                      idées. Pourquoi ne pas lui faire parvenir les vôtres ? Cliquez simplement
                                       ici.")}

               :einat {:name "Einat Grinbaum" :position "Chef Design"
                       :description (stru/join-lines
                                      "Einat crée des environnements publics et prives qui marient besoins sociaux
                                       avec expression artistique depuis plus de 15 ans. La considération des moindres
                                        détails est essentielle à chaque objet sculptural qui vous est offert.
                                        Partagez votre inspiration avec Einat. Cliquez simplement ici.")}

               :david {:name "David Millett" :position "Ingénieur Logiciel"
                       :description (stru/join-lines
                                      "David est un ingénieur en astronautique reconverti en développeur de
                                      logiciels avec une expérience de plus de dix ans a créer des solutions
                                       sur mesure, bibliothèques et optimisation de code d’entreprises. Selon vous,
                                       sur quels trucs cool David pourrait travailler? Faites lui savoir en
                                       cliquant ici.")}
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
