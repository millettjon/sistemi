(ns sistemi.site.product
  "Product related string translations and configuration.")

(def strings
  "translation strings"
  {:en {:shelf {:name "Custom Shelf"}
        :shelving {:name "Custom Shelving Unit"
                   :cutout {:semplice "none" :ovale "oval" :quadro "rectangle"}}
        :params {:depth "depth"}}
   :es {}
   :fr {:shelving {:cutout {:semplice "semplice" :ovale "ovale" :quadro "quadro"}}}})

(def urls
  "design urls"
  {:shelf "/shelf.htm"
   :shelving "/shelving.htm"})

(def parameter-orders
  "display order of design paramters"
  {:shelf [:width :depth :finish :color]
   :shelving [:width :height :depth :cutout :finish :color]})

(sistemi.registry/register)
