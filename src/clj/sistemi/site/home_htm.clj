(ns sistemi.site.home-htm
  (:require [sistemi.layout :as layout]
            [clojure.tools.logging :as log])
  (:use [ring.util.response :only (response)]))

(def strings
  {:en {:title "Modern Shelving Furniture : Sistemi Moderni"}
   :es {:title "Muebles de Estantería Moderna : Sistemi Moderni"}
   :fr {:title ""}
   :it {:title ""}})

(def names
  {:es "hogar"
   :fr "accueil"})

(def head
  (seq [
        ;; NIVO Slider
        [:link {:rel "stylesheet" :href "/nivo/nivo-slider.css" :type "text/css" :media "screen"}]
        ;;[:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" :type "text/javascript"}]
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
  (response (layout/standard-page head body 544)))
