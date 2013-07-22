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
                :finish {:_ "finish" :laquer-matte "laquer - matte" :laquer-satin "laquer - satin" :laquer-glossy "laquer - glossy"
                         :valchromat-raw "valchromat - unfinished" :valchromat-oiled "valchromat - oiled"}
                :color "color"
                }
        :shelving {:name "Custom Shelving Unit"
                   :width "width"
                   :depth "depth"
                   :height "height"
                   ;:finish "finish"
                   :color "color"
                   :cutout {:_ "cutout" :semplice "none" :ovale "oval" :quadro "rectangle"}
                   }
        :params {:depth "depth"}}

   :es {}

   :it {:shelf {:name ""
                :width "Lunghezza"
                :depth "Profondità"
                :color "Colore"
                :cutout {:_ "Ritaglio" :semplice "nessuno" :ovale "ovale" :quadro "rettangolo"}
               ;; Not working with form.clj and
                :finish {:_ "Finitura" :matte "Opaco" :satin "Satinato" :glossy "Lucido"}
                }
        :shelving {:width "Lunghezza"
                   :depth "Profondità"
                   :height "Altezza"
                   :color "Colore"
                   :cutout {:_ "Ritaglio" :semplice "nessuno" :ovale "ovale" :quadro "rettangolo"}
                   :finish {:_ "Finitura" :matte "Opaco" :satin "Satinato" :glossy "Lucido"}
                   }}

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
