(ns sistemi.site
  "Root of all urls in the site."
  (:require [locale.core :as l]
            [locale.translate :as ltr]
            [sistemi.registry :as registry]
            [sistemi.translate :as tr]
            [www.request :as req]
            [www.user-agent :as ua])
  (:use [ring.util.response :only (response content-type)]
        [hiccup page])
  (:import java.io.File))

(def strings
  {:en {; splash page
        :title "Modern Shelving Furniture : Sistemi Moderni"
        :select-a-language "Select A Language"

        ; layout
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
                           :shelving-units "Bookcases"
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
        :copyright ["Copyright 2012 SISTEMI MODERNI."
                    "All rights reserved."]}

   :fr {; splash page
        :title "Meubles Étagères Moderne : Sistemi Moderni"
        :select-a-language "Choisissez une Langue"

        ; layout
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
                          :shelving-units "Bibliothèques"
                          :tables "Tables"
                          :lamps "Eclairage"
                          :chairs "Chaises"
                          :paint  "Peinture"}
               :service "A Votre Service"
               :system "Le Système"
               :gallery "Gallérie"
               :blog "blog"
               :feedback "Vos Impressions"}
        :copyright ["© 2012 SISTEMI MODERNI."
                    "(all rights reserved)"]}

   :it {; splash page
        :title ""
        :select-a-language "Scegliere una lingua"
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
                          :shelving-units "Libreria"}
               :service ""
               :system "Il Sistema"
               :gallery ""
               :blog "blog"
               :feedback "Feedback"}
        :copyright ["@ 2012 SISTEMI MODERNI."
                    "(all rights reserved)"]}

   :es {; splash page
        :title "Muebles de Estantería Moderna : Sistemi Moderni"
        :select-a-language "Elegir Idioma"

        ; layout
        :version "versión alpha"
        :account "Mi Cuenta"
        :cart "Carro"
        :signup "Recibir Correos"
        :contact "Contáctenos"
        :menu {:home "hogar"
               :vision "nuestro visión"
               :boutique {:_ "boutique en línea"
                          :shelving "estantaría"
                          :tables "mesas"
                          :lamps "lámparas"
                          :chairs "sillas"
                          :paint "pintura"}
               :service "a su servicio"
               :system "el sistema"
               :gallery "galería"
               :blog "blog"
               :feedback "sugerencias"}
        :copyright ["© 2012 SISTEMI MODERNI."
                    "Todos los derechos reservados."]}
   })

(def language-names
  {"en" "english"
   "fr" "français"
   "de" "deutsch"
   "it" "italiano"
   "es" "español"
   })

(defn page
  []
  (html5 {:lang (req/*req* :locale)}
   [:head
    [:meta {:http-equiv "Content-Type", :content "text/html; charset=utf-8"}]
    [:title (tr/translate :title)]
    
    ;; TODO: Include bootstrap?
    ;; TODO: Move this to layout, or inline.
    [:link {:href "general.css", :rel "stylesheet", :type "text/css"}]
    [:link {:href "fonts/stylesheet.css", :rel "stylesheet", :type "text/css"}]

    ;; TODO: Factor this out.
    ;; TODO: See if there is a small slider (e.g., bootstrap).
    ;; NIVO Slider
    [:link {:rel "stylesheet" :href "/nivo/nivo-slider.css" :type "text/css" :media "screen"}]
    [:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" :type "text/javascript"}]
    [:script {:type "text/javascript" :src "/3d/detector.js"}]
    [:script {:src "/nivo/jquery.nivo.slider.pack.js" :type "text/javascript"}]
    [:script {:type "text/javascript"}
     "$(window).load(function() {
	$('#slider').nivoSlider({
		startSlide:0, //Set starting Slide (0 index)
		slideshowEnd: function(){$('#slider').data('nivo:vars').stop = true;},
		effect:'fade', // Specify sets like: 'fold,fade,sliceDown'
        animSpeed:200, // Slide transition speed
        pauseTime:3000, // How long each slide will show
        directionNav:false, // Next & Prev navigation
        directionNavHide:true, // Only show on hover
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
     ]
    [:meta {:name "keywords" :content "modern furniture, modern shelves, shelving, shelf, book case, mod furniture, contemporary shelf"}]
    [:meta {:name "description" :content "Modern shelves and shelving systems by Sistemi Moderni in France, serving modern furniture design addicts throughout Europe."}]
    [:meta {:http-equiv "refresh" :content (str "16;URL=" (tr/localize "/home.htm"))}]]
   
   [:body
    [:div#wrapper
     [:div#rotate1
      [:div#slider
       [:a {:href (tr/localize "home.htm")} [:img {:src "graphics/modern-shelf.jpg" :alt "Modern Shelf" :width "541" :height "722" :border "0"}]]
       [:a {:href (tr/localize "home.htm")} [:img {:src "graphics/modern-bookcase.jpg" :alt "Modern Bookcase" :width "541" :height "722" :border "0"}]]
       [:a {:href (tr/localize "home.htm")} [:img {:src "graphics/modern-shelving.jpg" :alt "Modern Shelving" :width "541" :height "722" :border "0"}]]]]

     ;; Site Requirements Message
     [:div {:style "position: absolute; top: 50px; left: 550px; text-align: center; font-size: 16px; text-transform: uppercase; padding: 10px;"}
      [:noscript {:style "color: #F00;"} (tr/translate :javascript_required)]
      [:span#webgl_recommended {:style "display: none;"} (tr/translate :webgl_recommended)
       ;; Recommend firefox/chrome for non firefox/chrome users.
       (case (:browser_group (ua/req->features req/*req*))
         "Firefox" ""
         "Chrome" ""
         [:span"&nbsp;" (tr/translate :recommended_browsers)])]
      [:span#canvas_recommended {:style "display: none; font-size: 16px; color: #F00"}
       (tr/translate :canvas_required) "&nbsp;" (tr/translate :recommended_browsers)]]

     [:div#rightcol1
      [:img.logo1 {:src "graphics/sistemi-moderni.jpg" :alt "Modern Furniture" :width "318" :height "183"}]
      [:img.thing1 {:src "graphics/nav-graphic.jpg" :width "146" :height "89"}]
      [:div#menu1
       [:div#num "I" [:span.white "."]]
       [:ul#nav1
        [:li
         [:a.white2 {:href "#"} (tr/translate :select-a-language)]
         [:ul
          (map (fn [lang]
                 (let [active (= (l/locales lang) (req/*req* :locale))
                       clss (if active "live" "notyet")
                       href  (tr/localize "/profile/locale"
                                       :query
                                       {:lang lang
                                        :target (str (ltr/localize-path "/home.htm" registry/localized-paths (keyword lang)))})]
                   [:li [:a {:href href :class clss} (language-names lang)]]))
               l/locales)

          ;; <li><a href="modern-shelving.htm" class="live">english</a></li>
          ;;   <li><a href="#"  class="notyet">franÇais</a></li>
          ;;   <li><a href="#"  class="notyet">deutsche</a></li>
          ;;   <li><a href="#"  class="notyet">italiano</a></li>
          ;;   <li><a href="#"  class="notyet">espaÑol</a></li>
          ]]]]]]]))

(defn handle
  [req]
  (-> (page)
      response
      ;; Set the content type explicitly since this is served from / and has no .htm extension.
      (content-type "text/html; charset=utf-8")))