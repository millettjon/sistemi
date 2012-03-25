(ns sistemi.layout
  (:require [hiccup.core :as hcp]
            [clojure.string :as str]
            [util.path :as path])
  (:use net.cgrand.enlive-html
        locale.core
        sistemi.translate
        www.request))

(defn menu-item
  ([label]
     (menu-item label (str (name label) ".htm")))
  ([label page]
     (let [cur-page (path/first (:uri *req*))
           label (translate :menu label)]
       [:a
        {:href (localize page)}
        (if (= page cur-page)
          [:span.white label]
          label)])))

(defn boutique-item
  [item]
  (let [page (str (name item) ".htm")
        item (translate :menu :boutique item)]
    [:li [:a {:href (localize page)} item]]))

(defn doctype-html5
 [html]
 (str "<!doctype html>" html))

(defn standard-page
  [body]
  (doctype-html5
   (hcp/html
    [:html {:lang (*req* :locale)}
     [:head
      [:meta {:http-equiv "Content-Type", :content "text/html; charset=utf-8"}]
      [:title  (translate :title)]
      [:link {:href "general.css", :rel "stylesheet", :type "text/css"}]
      [:link {:href "fonts/stylesheet.css", :rel "stylesheet", :type "text/css"}]
      [:meta {:name "keywords", :content "modern furniture, modern shelves, shelving, shelf, book case, mod furniture, contemporary shelf"}]
      [:meta {:name "description", :content "Modern shelving in Europe."}]
      [:link {:href "p7pmm/p7PMMv01.css", :rel "stylesheet", :type "text/css", :media "all"}]
      [:script {:type "text/javascript", :src "p7pmm/p7PMMscripts.js"}]

      ;; NIVO
      [:script {:src "jquery.nivo.slider.pack.js" :type "text/javascript"}]
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
       ]

      [:script {:type "text/javascript", :src "p7_eqCols2_10.js"}]]
     [:body #_{:onload "P7_equalCols2(0,'content','DIV', 'col2e','P','col3b','P', 'col1', 'DIV')"}
      [:div#wrapper2
       [:div#header1
        [:div.col1d " "]
        [:div.col2b (translate :version)]
        [:div.col4 " "]]
       [:div#header2
        [:div.col1b

         ;; Construct locale menu.
         [:div#lang
          (->>
           locales
           (map (fn [locale]
                  (if (= locale (*req* :locale))
                    [:a.select {:href "#"} locale]
                    [:a {:href (localize "/profile/locale" :query {:lang locale})} locale])))
           (interpose [:span.line "|"]))]

         [:img {:src "img/block-logo.gif" :alt "logo"}]]
        [:div.col2c
         [:div.col2
          [:ul 
           [:li 
            [:a { :href "#"} (translate :signup)]]
           [:li 
            [:a { :href "#"} (translate :contact)]]]]]
        [:div.col4b
         [:img {:src "graphics/sistemi-moderni-systems.jpg", :width "206", :height "119" :alt "logo"}]]]
       [:div#content
        [:div#col1
         [:div#p7PMM_1.p7PMMv01
          [:ul.p7PMM
           [:li (menu-item :home-page "modern-shelving.htm")]
           [:li (menu-item :our-vision)]
           [:li (menu-item :on-line-boutique)
            [:div 
             [:ul
              (boutique-item :shelving)
              (boutique-item :tables)
              (boutique-item :lamps)
              (boutique-item :chairs)
              (boutique-item :paint)]]]
           [:li (menu-item :at-your-service)]
           [:li (menu-item :the-system)]
           [:li (menu-item :gallery)]
           [:li (menu-item :blog)]
           [:li (menu-item :feedback)]]
          [:script {:type "text/javascript"} "\n<!--\nP7_PMMop('p7PMM_1',0,2,0,-5,0,0,0,1,0,3,1,1,0,0);\n//-->\n       "]
          [:div 
           [:script {:type "text/javascript"} "\n  (function() {\n    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;\n    po.src = 'https://apis.google.com/js/plusone.js';\n    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);\n  })();\n"]]]
         [:div#redbar
          [:a.first {:href "#"}
           [:img {:src "graphics/facebook.jpg", :border "0" :alt "facebook"}]]
          [:g:plusone {:size "small"}]
          [:a { :href "#"}
           [:img {:src "graphics/twitter.jpg", :border "0" :alt "twitter"}]]]
         [:div#address "SISTEMI MODERNI"
          [:br] " \n      St. Martin d&rsquo;Uriage, France   "
          [:br] "\n      M. +33 06 09 46 92 00"]
         [:div#copyright (interpose [:br] (translate :copyright))]]

        ;; This worked for feedback.
        ;; #_[:div#col2e
        ;;    [:p  " "]]
        ;; #_[:div#col3b body]

        body

        ]]]])))
