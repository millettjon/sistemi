(ns sistemi.site.product
  "Product related string translations and configuration.")

(def strings
  "translation strings"
  {:en {:shelf {:name "Custom shelf"}
        :params {:depth "depth"}}
   :es {}
   :fr {}})

(def urls
  "design urls"
  {:shelf "/shelf.htm"})

(def parameter-orders
  "Display order of design paramters."
  {:shelf [:width :depth :finish :color]})

(sistemi.registry/register)
