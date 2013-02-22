(ns sistemi.site.product
  "Product related string translations and configuration."
  (:require [sistemi.translate :as tr]
            [sistemi.model.format :as fmt]))

;; TODO: Can some of these be bumped up to the top level as general terms?
(def strings
  "translation strings"
  {:en {:shelf {:name "Custom Shelf"
                :width "width"
                :depth "depth"
                :finish "finish"
                :color "color"}
        :shelving {:name "Custom Shelving Unit"
                   :width "width"
                   :depth "depth"
                   :height "height"
                   :finish "finish"
                   :color "color"
                   :cutout {:_ "cutout"
                            :semplice "none" :ovale "oval" :quadro "rectangle"}}
        :params {:depth "depth"}}
   :es {}
   :it {:shelf {:width "Lunghezza"
                :depth "Profondità"
                :finish "Finitura"
                :color "Colore"}
        :shelving {:width "Lunghezza"
                   :depth "Profondità"
                   :height "Altezza"
                   :finish "Finitura"
                   :color "Colore"
                   :cutout {:_ "Ritaglio" :semplice "nessuno" :ovale "ovale" :quadro "rettangolo"}}}

   :fr {:shelf {:width "Largeur"
                :depth "Profondeur"
                :finish "Finition"
                :color "Couleur"}
        :shelving {:width "Longeur"
                   :depth "Profondeur"
                   :height "Hauteur"
                   :finish "Finition"
                   :color "Couleur"
                   :cutout {:_ nil :semplice "semplice" :ovale "ovale" :quadro "quadro"}}}})

(def urls
  "design urls"
  {:shelf "/shelf.htm"
   :shelving "/shelving.htm"})

(sistemi.registry/register)
