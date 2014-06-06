(ns sistemi.site
  "Root url and strings table."
  (:require [sistemi.layout :as layout])
  (:use [ring.util.response :only (response content-type)]))

(def strings
  {:en {;; page
        :title "Modern Shelving Furniture : Sistemi Moderni"

        ;; layout
        :construction "Under Construction"
        :webgl_recommended "This site works best if WebGL rendering is enabled."
        :canvas_required "This site requires html5 canvas or WebGL to function."
        :recommended_browsers "We recommend <a href='http://www.mozilla.org'>Firefox</a> or <a href='http://www.google.com/chrome'>Chrome</a>."
        :javascript_required "This site requires javascript to function."

        :account "My Account"
        :cart {:_ "Cart"
               :add "Add to cart"
               :update "Update cart"}
        :header {:signup "Sign Up For Emails"
                 :contact "Contact Us"
                 :careers "Careers"
                 :team "Team"}
        :menu {:home "home"
               :vision "our vision"
               :boutique  {:_ "on-line boutique"
                           :shelves "shelves"
                           :bookcases "Bookcases"
                           ;; TODO: How to indicate forthcoming products??
                           :tables "tables"
                           :lamps "lamps"
                           :chairs "chairs"
                           :paint "paint"}
               :service "at your service"
               :system "the system"
               :gallery "gallery"
               :blog "blog"
               :feedback "feedback"}
        :copyright ["Copyright 2014 SISTEMI MODERNI."
                    "All rights reserved."]

        ;; general misc
        :contact "contact"

        ;; general - cart/order related
        :total "total"
        :subtotal "subtotal"
        :shipping "shipping"
        :item "item"
        :quantity "quantity"
        :price "price"
        :unit_price "unit price"
        :copy "copy"
        :edit "edit"
        :delete "delete"
        :payment "Payment"
        :continue "Next"
        }

   :fr {;; page
        :title "Meubles Étagères Moderne : Sistemi Moderni"

        ;; layout
        :construction "Page En Construction"
        :webgl_recommended "Ce site est optimisé pour une visualisation avec rendu WebGL activé."
        :canvas_required "Le bon fonctionnement de ce site nécessite HTML5 canvas, ou WebGL."
        :recommended_browsers "Nous recommandons des navigateurs comme <a href='http://www.mozilla.org'>Firefox</a> ou <a href='http://www.google.com/chrome'>Chrome</a>."
        :javascript_required "Ce site nécessite JavaScript pour son fonctionnement."

        :account "Mon Compte"
        :cart {:_ "Panier"
               :add "Ajouter au panier"}

        :header {:signup "S’abonner"
                 :contact "Nous contacter"
                 :careers "Jobs"
                 :team "Qui sommes nous"}
        :menu {
               :home "Accueil"
               :vision "Notre Vision"
               :boutique {:_ "Boutique En Ligne"
                          :shelves "Etagères"
                          :bookcases "Bibliothèques"
                          :tables "Tables"
                          :lamps "Eclairage"
                          :chairs "Chaises"
                          :paint  "Peinture"}
               :service "A Votre Service"
               :system "Le Système"
               :gallery "Gallérie"
               :blog "blog"
               :feedback "Vos Impressions"}
        :copyright ["© 2014 SISTEMI MODERNI."
                    "(all rights reserved)"]

        ;; general
        :contact "contacter"

        ;; general - cart/order related
        :subtotal "Sous-total"
        :item "article"
        :quantity "quantité"
        :price "prix"
        :unit_price "prix unitaire"
        :copy "copier"
        :edit "modifier"
        :delete "supprimer"
        :payment "Paiement"
        :continue "Suivant"
        }

   :it {;; page
        :title ""

        ;; layout
        :construction "In Construzione"
        :webgl_recommended "Questo sito è ottimizzato per una visualizzazione con WebGL rendering attivato."
        :canvas_required "Il buon funzionamento di questo sito richiede HTML5 canvas, o WebGL."
        :recommended_browsers "Raccomandiamo browsers come <a href='http://www.mozilla.org'>Firefox</a> o <a href='http://www.google.com/chrome'>Chrome</a>."
        :javascript_required "Questo sito richiede JavaScript per funzionare."

        :account ""
        :cart {:_ ""
               :add "Aggiungere al carrello"}
        :header {:signup "Iscriversi per ricevere emails"
                 :contact "Contatti"
                 :careers "Opportunità di carriera"
                 :team "La squadra"}
        :menu {:home "Home"
               :vision "La nostra visione"
               :boutique {:_ "Boutique online"
                          :shelves "Scaffali"
                          :bookcases "Libreria"}
               :service ""
               :system "Il Sistema"
               :gallery ""
               :blog "blog"
               :feedback "Feedback"}
        :copyright ["@ 2014 SISTEMI MODERNI."
                    "(all rights reserved)"]

        ;; general - cart/order related
        :subtotal "totale parziale"
        :item "articolo"
        :quantity "quantità"
        :price "prezzo"
        :unit_price "prezzo unitario"
        :edit "modifica"
        :copy "copia"
        :delete "annulla"
        }

   :es {;; page
        :title "Muebles de Estantería Moderna : Sistemi Moderni"

        ;; layout
        :version "versión alpha"
        :account "Mi Cuenta"
        :cart "Carro"
        :signup "Recibir Correos"
        :contact "Contáctenos"
        :menu {:home "hogar"
               :vision "nuestro visión"
               :boutique {:_ "boutique en línea"
                          :bookcase "estantaría"
                          :tables "mesas"
                          :lamps "lámparas"
                          :chairs "sillas"
                          :paint "pintura"}
               :service "a su servicio"
               :system "el sistema"
               :gallery "galería"
               :blog "blog"
               :feedback "sugerencias"}
        :copyright ["© 2014 SISTEMI MODERNI."
                    "Todos los derechos reservados."]}
   })

(def head
  (seq [
        ;; NIVO Slider
        [:link {:rel "stylesheet" :href "/nivo/nivo-slider.css" :type "text/css" :media "screen"}]
        [:script {:src "/nivo/jquery.nivo.slider.pack.js" :type "text/javascript"}]
        [:script {:type "text/javascript"}
         "$(window).load(function() {
	$('#slider').nivoSlider({
		startSlide:1, //Set starting Slide (0 index)
		slideshowEnd: function(){$('#slider').data('nivo:vars').stop = true;},
		effect:'fade', // Specify sets like: 'fold,fade,sliceDown'
        animSpeed:200, // Slide transition speed
        pauseTime:3000, // How long each slide will show
        directionNav:false, // Next & Prev navigation
        directionNavHide:false, // Only show on hover
        controlNav:false, // 1,2,3... navigation
        controlNavThumbs:false, // Use thumbnails for Control Nav
        controlNavThumbsFromRel:false, // Use image rel for thumbs
        controlNavThumbsSearch: '.jpg', // Replace this with...
        controlNavThumbsReplace: '_thumb.jpg', // ...this in thumb Image src
        keyboardNav:true, // Use left & right arrows
        pauseOnHover:false, // Stop animation while hovering
        manualAdvance:false, // Force manual transitions
        captionOpacity:0.8, // Universal caption opacity
	});
});"
         ]]))

(def body
  [:div#slider {:style "margin-left: 42px"}
   [:img {:src "graphics/contemporary-shelving.jpg" :alt "Contemporary Shelving" :width "633" :height "544"}]
   [:img {:src "graphics/classic-shelving.jpg" :alt "Classic Shelves" :width "633" :height "544"}]
   [:img {:src "graphics/modern-shelves.jpg" :alt "Modern Bookcase" :width "633" :height "544"}]])

(defn handle
  [req]
  (-> (response (layout/standard-page head body 544))
      ;; Set the content type explicitly since this is served from / and has no .htm extension.      
      (content-type "text/html; charset=utf-8")))
