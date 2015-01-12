(ns sistemi.site.development-htm
  (:require [ring.util.response :as ru]
            [sistemi.layout :as l]
            [sistemi.translate :as tr]))
#_ (remove-ns 'sistemi.site.development-htm)

(def names
  {:es "estantaría"
   :fr "etagères"})

(def strings
  {:en {:back "Back"
        :contact-us "Contact Us"
        :credenza {:copy "Looks simple.  Does it not?  The only thing simple about Credenza is its assembly.
                          There are no screws, nails, dowels or glue necessary to put her together.  The ingenuity
                          is in the patent pending connection system that we cleverly hid from view.  You can
                          choose almost any dimension and color just like the bookcases and shelves.  However,
                          you will need to call on us to make it and give you a cost.  Credenza will become part
                          of our automated systems in the near future.
                          <br><br>
                          If you want to find out more about this innovative product, click on the contact button below."}

        :credenza-classic {:copy "We call this Classic because this cabinet is assembled in a traditional manner from parts
                                  that are made with a CNC machine.  Modern systems today will soon be classics tomorrow like
                                  this beautiful example of Credenza.  Like her more modern cousin, Credenza Classic is assembled
                                  without fasteners and can be custom ordered in so many dimensions, colors and materials.
                                  <br><br>
                                  If you want to find out how to get your hands on this product, click on the contact button below!"}

        :armoire {:copy "We are so busy that we have not even taken a moment to come up with a catchy name for this
                          innovative storage solution.  Just like Credenza, Armoire uses our patent pending connection
                          system to hide the magic of its easy assembly.  The choices are endless.
                          <br><br>
                          To find out just how personalized Armoire can be, give us a ring by clicking on the contact button below!"}

        :nata {:copy "The Nata sofa is truly amazing.  Like everything else we do, assembly of Nata is without fasteners.  But this
                      time, you get to further personalize your furniture by choosing just the right fabric for your seating pleasure.
                      We have successfully prototyped her in New York.  There is still some work we have to do in order to bring Nata
                      to Europe.  Perhaps you would like to be a part of that effort.
                      <br><br>
                      If you find Nata to be exactly the solution you were looking for, reach out to us by clicking on the contact
                      button below.  We would love to make Nata especially for you on this continent!"}

        :oasi {:copy "This is the solution every office needs.  Gone are the days of being surrounded by cushiony cubicle walls
                      that make us feel like patients in a sanatorium.  With Oasi we can surround ourselves not only with the
                      accouterments of any professional office, but also your personal collection of favorite architecture books.
                      The beyond modular design allows you to choose a single or double sided shelving system with computer desks
                      or without.  This design also allows you to hide cabling, pipes and wires.  If you want, you can even use
                      Oasi as an architectural element by creating a room.  Simply give us your floor to ceiling dimension.
                      And yes, Oasi is assembled without any fasteners whatsoever.
                      <br><br>
                      Does Oasi provide you the most amazing solution to your design problem?  If so, give us a call by clicking
                      on the contact button below."}}

   :fr {:contact-us "Nous Contacter"
        :back "Retour"
        :credenza {:copy "Simple, n'est-ce pas? La seule chose simple à propos de Credenza, c'est son assemblage. Il n'y a pas de vis, de
                          clous, de chevilles ou de colle nécessaire à son assemblage. L'ingéniosité réside dans le système d'assemblage
                          (ici caché de la vue de tous) dont le brevet est en cours de dépôt. Vous pouvez choisir n'importe quelle
                          dimension et couleur, tout comme nos bibliothèques et étagères. Cependant, le process n'est pas encore
                          automatisé et vous aurez besoin de nous contacter pour obtenir un prix. CREDENZA fera partie de nos
                          systèmes automatisés dans un avenir proche. Si vous souhaitez en savoir plus sur ce produit innovant, cliquez
                          sur le bouton de contact ci-dessous."}

        :credenza-classic {:copy "Nous l'appelons \"classic\" car son coffre est assemblé de façon traditionnelle à partir de pièces réalisées par
                                  une machine CNC. Comme le suggère ce bel exemple de crédence, les systèmes modernes d'aujourd'hui seront
                                  bientôt les classiques de demain. Tout comme son cousin plus moderne, la Credenza Classic est assemblée
                                  sans attaches et peut être commandée personnalisée en termes de dimensions, couleurs et matériaux . Si vous
                                  voulez savoir comment obtenir ce produit, cliquez sur le bouton de contact ci-dessous !"}

        :armoire {:copy "Nous sommes tellement occupés que nous n'avons pas encore pris un moment pour trouver un nom
                          accrocheur pour cette solution de stockage innovante. Tout comme CREDENZA, nos armoires utilisent notre
                          brevet d'assemblage en instance de dépôt pour un montage facile. Les possibilités sont infinies. Pour
                          découvrir jusqu'à quel point peuvent être personnalisées nos armoires, cliquez sur le bouton de contact ci-dessous !"}

        :nata {:copy "Le canapé Nata est vraiment incroyable. Comme tout ce que nous faisons, l'assemblage de Nata se fait sans
                      attaches. Mais cette fois, vous pouvez personnaliser davantage votre meuble en choisissant le tissu qui vous
                      convient pour votre confort d'assise. Nous l'avons prototypé avec succès à New York. Il reste encore du travail
                      à faire pour importer Nata en Europe. Peut-être souhaitez-vous faire partie de cet effort? Si vous trouvez que
                      Nata correspond exactement à la solution que vous recherchez, cliquez sur le bouton de contact ci-dessous.
                      Nous aimerions énormément réaliser Nata spécialement pour vous sur ce continent !"}

        :oasi {:copy "C'est la solution que chaque bureau devrait avoir. Dépassés sont les jours où nous étions entouré par les
                      parois de notre box qui nous faisaient sentir comme des patients dans un sanatorium.  Avec Oasi nous
                      pouvons nous entourer non seulement des fournitures nécessaires à tout bureau professionnel, mais aussi de
                      votre collection personnelle de livres d'architecture préférés. La conception modulaire vous permet de choisir
                      un système de rayonnage simple ou double avec postes de travail ou non. Cette conception permet également
                      de masquer le câblage et les tuyaux. Si vous le souhaitez, vous pouvez même utiliser Oasi comme un élément
                      architectural pour une pièce. Il suffit de nous donner vos dimensions du sol au plafond. Aussi, Oasi est
                      assemblé sans attaches. Oasi vous semble être la solution la plus étonnante à vos problèmes de conception ? Si
                      oui, contactez-nous en cliquant sur ​​le bouton de contact ci-dessous."}}})

(defn body [req]
  (let [p (:params req)
        type (-> p :type keyword)
        image (:image p)]
    [:div {:style {:margin-left "25px" :margin-top "25px" :width "625px" :color "#DDD"}}
     [:img {:src image :style {:width "625px" :margin-bottom "15px"}}]
     [:h2 {:style {:text-transform "uppercase"}} (tr/translate "/product" type :name)]
     [:p (tr/translate type :copy)]

     [:br]
     [:div {:style {:margin-bottom "15px"}}
      [:a {:href (tr/localize "/contact.htm")}
       ;; TODO Pass product information to the contact form.
       [:button#submit.btn.btn-inverse {:tabindex 1} (tr/translate :contact-us)]]
      [:a {:href (tr/localize "/")}
       [:button#submit.btn.btn-inverse {:tabindex 2 :style {:margin-left "20px"}} (tr/translate :back)]]]]))

(defn handle
  [req]
  (ru/response (l/standard-page "" (body req) 544)))
