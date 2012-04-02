(ns sistemi.site
  (:require [locale.translate :as tr]
            [sistemi.registry :as registry])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response content-type)]
        locale.core
        sistemi.translate
        www.request)
  (:import java.io.File))


(def strings
  {:en {; splash page
        :title "Modern Shelving Furniture : Sistemi Moderni"
        :select-a-language "Select A Language"

        ; layout
        :version "alpha version"
        :account "My Account"
        :cart "Cart"
        :signup "Sign Up For Emails"
        :contact "Contact Us"
        :menu {:home "home"
               :vision "our vision"
               :boutique  {:_ "on-line boutique"
                                   :shelving "shelving"
                                   :tables "tables"
                                   :lamps "lamps"
                                   :chairs "chairs"
                                   :paint "paint"}
               :service "at your service"
               :system "the system"
               :gallery "gallery"
               :blog "blog"
               :feedback "feedback"}
        :copyright ["Copyright 2011 SISTEMI MODERNI."
                    "All rights reserved."]}

   :es {; splash page
        :title "Muebles de Estantería Moderna : Sistemi Moderni"
        :select-a-language "Eligir Idioma"

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
        :copyright ["© 2011 SISTEMI MODERNI."
                    "Todos los derechos reservados."]}

   :fr {; splash page
        :select-a-language "Choisissez une Langue"

        ; layout
        :account "Mon Compte"
        :cart "Panier"
        :signup "S’abonner aux emails"
        :contact "Contactez Nous"
        :menu {
               :home "Accueil"
               :vision "Notre Vision"
               :boutique {:_ "Boutique En Ligne"
                          :shelving "Etagères"
                          :tables "Tables"
                          :lamps "Eclairage"
                          :chairs "Chaises"
                          :paint  "Peinture"}
               :service "A Votre Service"
               :system "Le Système"
               :gallery "Gallérie"
               :blog "blog"
               :feedback "Vos Impressions"}
        :copyright ["© 2011 SISTEMI MODERNI."
                    "(all rights reserved)"]}})

(def language-names
  {"en" "english"
   "fr" "française"
   ;"de" "deutsch"
   ;"it" "italiano"
   ;"es" "español"
   })

#_[:html {:lang (*req* :locale)}
 [:head
  [:meta {:http-equiv "Content-Type", :content "text/html; charset=utf-8"}]
  [:title (translate :title)]
  
  ;; TODO: Include bootstrap?
  ;; TODO: Move this to layout, or inline.
  [:link {:href "general.css", :rel "stylesheet", :type "text/css"}]
  [:link {:href "fonts/stylesheet.css", :rel "stylesheet", :type "text/css"}]

  ;; TODO: Factor this out.
  ;; TODO: See if there is a small slider (e.g., bootstrap).
  ;; NIVO Slider
  [:link {:rel "stylesheet" :href "nivo-slider.css" :type "text/css" :media "screen"}]
  [:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" :type "text/javascript"}]
  [:script {:src "jquery.nivo.slider.pack.js" :type "text/javascript"}]
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
  [:meta {:http-equiv "refresh" :content (str "16;URL=" (localize "/home.htm"))}]]
 
 [:body
  [:div#wrapper
   [:div#rotate1
    [:div#slider
      ;; <a href="modern-shelving.htm"><img src="graphics/modern-shelf.jpg" alt="Modern Shelf" width="541" height="722" border="0" /></a>
      ;; <a href="modern-shelving.htm"><img src="graphics/modern-bookcase.jpg" alt="Modern Bookcase" width="541" height="722" border="0" /></a>
      ;; <a href="modern-shelving.htm"><img src="graphics/modern-shelving.jpg" alt="Modern Shelving" width="541" height="722" border="0" /></a>
     ]
    ]
   [:div#rightcol1
    [:img.logo1 {:src "graphics/sistemi-moderni.jpg" :alt "Modern Furniture" :width "318" :height "183"}]
    [:img.thing1 {:src "graphics/nav-graphic.jpg" :width "146" :height "89"}]
    [:div#menu1
     [:div#num "I" [:span.white "."]]
     [:ul#nav1
      [:li
       [:a.white2 {:href "#"} (translate :select-a-language)]
       [:ul
        [:li [:a ]]
        ;; <li><a href="modern-shelving.htm" class="live">english</a></li>
        ;;   <li><a href="#"  class="notyet">franÇais</a></li>
        ;;   <li><a href="#"  class="notyet">deutsche</a></li>
        ;;   <li><a href="#"  class="notyet">italiano</a></li>
        ;;   <li><a href="#"  class="notyet">espaÑol</a></li>
        ]]]]]]]]

(deftemplate html
  (File. "www/raw/index.htm")
  []
  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (*req* :locale))

  ;; Set the page title.
  [:title] (content (translate :title))

  ;; Set the refresh target.
  [:head (attr= :http-equiv "refresh")] (set-attr :content (str "16;URL=" (localize "/home.htm")))

  ;; Set the Content-Type to utf-8
  [:head (attr= :http-equiv "Content-Type")] (set-attr :content "text/html; charset=utf-8")

  ;; Translate image slideshow links.
  [:div#slider :a] (set-attr :href (localize "/home.htm"))

  ;; Configure the language selection menu.
  [:ul#nav1 :> :li :> :a] (content (translate :select-a-language))
  [:ul#nav1 :li :ul ] (content (map (fn [lang]
                                      (let [active (= (locales lang) (*req* :locale))
                                            clss (if active "live" "notyet")
                                            enabled (contains? locales lang)
                                            href (if enabled (localize "/profile/locale"
                                                                      :query
                                                                      {:lang lang
                                                                       :target (str (tr/localize-path "/home.htm" registry/localized-paths (keyword lang)))})
                                                     "")
                                            a {:tag :a :attrs {:class clss :href href} :content (language-names lang)}]
                                        {:tag :li :content [a]}))
                                    ["en" "fr" "de" "it" "es"])))

(defn handle
  [req]
  (-> (html)
      response
      ;; Set the content type explicitly since this is served from / and has no .htm extension.
      (content-type "text/html; charset=utf-8")))

(sistemi.registry/register)
