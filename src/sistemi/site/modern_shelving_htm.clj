(ns sistemi.site.modern-shelving-htm
  (:require [sistemi.layout :as layout]
            [clojure.tools.logging :as log])
  (:use [ring.util.response :only (response)]))

(def strings
  {:en {:title "Modern Shelving Furniture : Sistemi Moderni"}
   :es {:title "Muebles de Estantería Moderna : Sistemi Moderni"}
   :fr {}})

(def names
  {:es "estantería-moderna"
   :fr "étagères-modernes"})

(defn body
  []
  [:div.col2d
   [:div#slider
    [:img {:src "graphics/contemporary-shelving.jpg" :alt "Contemporary Shelving" :width "633" :height "544" :border "0" :usemap "#m_rotate"}]
    [:img {:src "graphics/classic-shelving.jpg" :alt "Classic Shelves" :width "633" :height "544" :border "0"}]
    [:img {:src "graphics/modern-shelves.jpg" :alt "Modern Bookcase" :width "633" :height "544" :border "0"}]]])

(defn handle
  [req]
  (response (layout/standard-page (body))))

(sistemi.registry/register)
