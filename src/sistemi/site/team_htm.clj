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
                                   "Jon has been creating technical frameworks for 15 years (?) enabling
                                   websites to offer you everything you ever wanted.")}
               :einat {:name "Einat Grinbaum" :position "Chief Design Officer"
                       :description (stru/join-lines
                                     "Einat has been creating public and private environments combining
                                     social needs with artistic expression for over 15 years. Consideration
                                     of every detail is key in bringing to you a unique sculptural piece.
                                     Share with her your inspiration. Just click here.")}
               :david {:name "David Millett" :position "Developer"
                       :description (stru/join-lines
                                     "David is an Aerospace engineer turned software developer with more
                                     than ten years creating custom solutions, libraries, and optimization
                                     of enterprise code.")}}
        
        }
   :es {}
   :fr {:title "Sistemi Moderni: cadres d'entreprise"
        :team {:eric {:name "E.M. Romeo" :position "chef de la direction et directeur de la création"
                      :description (stru/join-lines "todo")}
               :jon {:name "Jon Millett" :position "Chef de la direction technique"
                     :description (stru/join-lines "todo")}

               :einat {:name "Einat Grinbaum" :position "officier concepteur en chef"
                       :description (stru/join-lines "todo: notice how Einat's google translate is different")}

               :david {:name "David Millett" :position "Ingénieur Logiciel"
                       :description (stru/join-lines
                                      " todo: google translate seems to be missing something ...
                                      David est un ingénieur aéronautique s'est développeur de logiciels avec
                                      plus de plus de dix ans la création de solutions personnalisées, les
                                      bibliothèques et d'optimisation de code de l'entreprise.")}
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
