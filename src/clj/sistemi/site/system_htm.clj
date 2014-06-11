(ns sistemi.site.system-htm
  (:require [util.string :as stru])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {:es "systema"})

(def strings
  {:en {:title "Vision Of Modern Furniture : High Design, Resonably Priced, Sustainably."
        :system {:title "HOW IT IS MADE"
                 :text (stru/join-lines "Your product begins with you. The moment your choice is submitted, the
                          commands to run the machines at the factory are created.  With speed and precision at the
                          height of today’s technology, machines carve your order out of sheets of Valchromat, an
                          FSC certified wood fiber panel.  Organic pigments are specially mixed with environmentally
                          safe resins to create a panel colored all the way through. Valchromat is water resistant,
                          does not contain toxic substances and is structurally superior to similar products.
                          Through Sistemi Moderni’s partnership with Valchromat, we can provide you with ecologically
                          sensitive products that are safe to use in kitchens, bathrooms, playrooms and the rest of
                          your home or office.
                          <p/>
                          Once your order is fabricated, it is inspected for potential imperfections and hand sanded
                          in preparation for finishing with oil or lacquer.  If you choose to have your order
                          lacquered, we will seal and coat it with an automotive grade lacquer in your choice of any
                          RAL color. Your shelves are then carefully wrapped and boxed in a custom designed heavy
                          duty cardboard package in preparation for rapid delivery. You are going to love opening
                          your gift when it arrives at your doorstep.")}}
   :es {}
   :fr {:title "TODO: French"
        :system {:title "LE SYSTEME"
                 :text
                 [:div
                  [:p "Votre meuble commence avec votre choix."]
                  [:p [:span.bullet_title "Étape 1 "] ": Sélectionnez votre produit et ses caractéristiques (couleur, dimensions, finitions...)"]
                  [:p [:span.bullet_title "Étape 2 "] ": Vos choix sont transmis directement aux machines, dans l'usine partenaire la plus proche de chez vous. Ces machines vont découper sur mesure avec vitesse et précision des panneaux Valchromat  (brut ou huilée) teintés dans la masse ou du medium écologiques (mdf) si vous souhaitez une finition laquée."]
                  [:p [:span.bullet_title "Étape 3 "] ": Vos pièces sont contrôlées et lissées à la main pour être prêtes à peindre."]
                  [:p [:span.bullet_title "Étape 4 "] ": Lors de cette phase, une finition (huilée, brut, laquée) est appliquée à vos pièces si vous avez opté pour ce choix."]
                  [:p [:span.bullet_title "Étape 5 "] ": Vos étagères sont soigneusement emballées et encartonnées dans une boîte idéalement conçue pour la livraison à domicile."]
                  [:p [:span.bullet_title "Étape 6 "] ": À réception, montez votre meuble vous-même très facilement, sans clous ni vis."]
                  [:p [:span.bullet_title "Étape 7 "] ": Appréciez le résultat et faites-nous connaître vos impressions !"]]}}

   :it {:title "IL SISTEMA"
        :system {:title "Il sistema"
                 :text (stru/join-lines
                        "Il suo prodotto comincia con Lei. Appena terminata la sua scelta e piazzato l'ordine,
                        i comandi di robotica sono automaticamente inviati alle macchine in fabbrica, per
                        creare i vari componenti su misura. Con velocità e precisione all’apice della
                        tecnologia moderna, i robot sagomano secondo il vostri requisiti i pannelli di
                        Valchromat®, costituiti di fibre di legno certificate FSC. Dei coloranti organici
                        sono mescolati con una resina prodotta appositamente per far penetrare il colore
                        nella massa del legno. Valchromat® è resistente all'umidità, non contiene sostanze
                        tossiche, è ecologico, e possiede una solidità strutturale superiore a quella di
                        materiali simili." )}}

   })

(defn body
  []
  [:div.text_content
   [:p.title (translate :system :title)]
   [:p (translate :system :text)]])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
