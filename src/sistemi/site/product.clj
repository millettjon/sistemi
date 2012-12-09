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
   :fr {:shelf {:width "Longeur"
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

(def parameter-orders
  "display order of design paramters"
  {:shelf [:width :depth :finish :color]
   :shelving [:width :height :depth :cutout :finish :color]})

(def parameter-formats
  "Formatting functions for parameters."
  {:shelving {:height #(fmt/cm %)
              :width #(fmt/cm %)
              :depth #(fmt/cm %)
              :cutout #(tr/translate "/product" :shelving :cutout %)
              :color #(fmt/color %)}
   :shelf {:width #(fmt/cm %)
           :depth #(fmt/cm %)
           :color #(fmt/color %)}})

(defn translate-param
  [item param]
  (tr/translate "/product" (:type item) param))

(defn format-value
  [item param]
  (let [value (param item)]
    (if-let [format-fn (get-in parameter-formats [(:type item) param])]
      (format-fn value)
      value)))

(sistemi.registry/register)
