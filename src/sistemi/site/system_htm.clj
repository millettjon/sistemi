(ns sistemi.site.system-htm
  (:require [util.string :as stru])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {:es "systema"})

(def strings
  {:en {:title "Vision Of Modern Furniture : High Design, Resonably Priced, Sustainably."
        :system {:title "HOW IT IS MADE"
                 :text (stru/join-lines "Your product begins with you. The moment your choice is
                          submitted, the commands to run the machines at the factory are created.
                          The factory produces the sheets of top grade \"multiplex\" plywood at the
                          core of your shelves. Then they cut the sheets according to your requests with
                          unbelievable accuracy and speed. (Click here to see a video.) Once your shelves
                          are cut, they are inspected for potential imperfections and hand sanded in
                          preparation for painting. Once in the paint booth, your shelves are sealed and
                          coated with an automotive grade lacquer in your choice of color. Your shelves
                          are then carefully wrapped and boxed in a custom designed heavy duty cardboard
                          package in preparation for rapid delivery to your doorstep. You are going to
                          love opening your gift when it arrives.")}}
   :es {}
   :fr {:title "TODO: French"
        :system {:title "TODO French"
                 :text (stru/join-lines
                         "Votre produit commence avec vous. Vos choix envoyés, les ordres sont donnés aux
                         machines en usine. Les machines coupent le bois (contreplaqué certifié PEFC ou
                         medium (mdf)) selon vos besoins avec vitesse et précision.  Une fois votre étagère
                         coupée, elle est contrôlée et lissée à la main pour être prête à peindre. Dans la
                         cabine de peinture votre étagère est couverte par une couche de laque que vous
                         avez choisie. Ensuite, vos étagères sont soigneusement emballées et encartonnées
                         dans une boîte idéalement conçue pour la livraison à domicile.

                         Vous apprécierez d’ouvrir votre colis quand il arrivera chez vous. Montez-le
                         vous-même très facilement sans vis ni clous.")}}


   :it {:title "IL SISTEMA"
        :system {:title "Il Sistema"
                 :text (stru/join-lines
                        "Il suo prodotto comincia con Lei.  Appena terminata la sua scelta e piazzato l'ordine,
                        i comandi di robotica sono automaticamente inviati alle macchine in fabbrica, per creare
                        i vari componenti su misura.  Con velocità e precisione all’apice della tecnologia moderna,
                        i robot sagomano secondo il vostri requisiti i pannelli di Valchromat®, costituiti di fibre
                        di legno certificate FSC. Dei coloranti organici sono mescolati con una resina prodotta
                        appositamente per far penetrare il colore nella massa del legno. Valchromat® è resistente
                        all'umidità, non contiene sostanze tossiche, è ecologico, e possiede una solidità strutturale
                        superiore a quella di materiali simili." )}}

   })

(defn body
  []
  [:div.text_content
   [:p.title (translate :system :title)]
   [:p (translate :system :text)]])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))

(sistemi.registry/register)
