(ns sistemi.site.product
  "Product related string translations and configuration.")

;; TODO: Can some of these be bumped up to the top level as general terms?
(def strings
  "translation strings"
  {:en {:shelf {:name "Custom Shelf"
                :width "width"
                :depth "depth"
                :finish {:_ "finish" :laquer-matte "matte" :laquer-satin "satin" :laquer-glossy "glossy"
                         :valchromat-raw "Valchromat – unfinished" :valchromat-oiled "Valchromat – oiled"}
                :color "color"
                }
        :bookcase {:name "Custom Bookcase"
                   :width "width"
                   :depth "depth"
                   :height "height"
                   :finish {:_ "finish" :laquer-matte "laquer - matte" :laquer-satin "laquer - satin" :laquer-glossy "laquer - glossy"
                            :valchromat-raw "Valchromat – unfinished" :valchromat-oiled "Valchromat – oiled"}
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
                :finish {:_ "Finitura" :laquer-matte "Opaco" :laquer-satin "Satinato" :laquer-glossy "Lucido"
                         :valchromat-raw "Valchromat – naturale" :valchromat-oiled "Valchromat – oliato"}
                }
        :bookcase {:width "Lunghezza"
                   :depth "Profondità"
                   :height "Altezza"
                   :color "Colore"
                   :cutout {:_ "Ritaglio" :semplice "nessuno" :ovale "ovale" :quadro "rettangolo"}
                   :finish {:_ "Finitura" :laquer-matte "Opaco" :laquer-satin "Satinato" :laquer-glossy "Lucido"
                            :valchromat-raw "Valchromat – naturale" :valchromat-oiled "Valchromat – oliato"}
                   }}

   :fr {:shelf {:name "Etagère personnalisée"
                :width "Largeur"
                :depth "Profondeur"
                :finish {:_ "Finition" :laquer-matte "Mat" :laquer-satin "Satiné" :laquer-glossy "Laqué"
                         :valchromat-raw "Valchromat – brut" :valchromat-oiled "Valchromat – huilé"}
                :color "Couleur"}
        :bookcase {:width "Longeur"
                   :depth "Profondeur"
                   :height "Hauteur"
                   :finish {:_ "Finition" :laquer-matte "Mat" :laquer-satin "Satiné" :laquer-glossy "Laqué"
                            :valchromat-raw "Valchromat – brut" :valchromat-oiled "Valchromat – huilé"}
                   :color "Couleur"
                   :cutout {:_ "Découpe" :semplice "semplice" :ovale "ovale" :quadro "quadro"}}}})

(def urls
  "design urls"
  {:shelf "/shelf.htm"
   :bookcase "/bookcase.htm"})
