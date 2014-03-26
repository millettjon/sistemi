(ns shipping.ups.shipping_options)

(def options
  {
   "01" { :desc "Next Day Air" }
   "02" { :desc "2nd Day Air" }
   "03" { :desc "Ground" }
   "07" { :desc "Express" }
   "08" { :desc "Expedited" }
   "11" { :desc "UPS Standard" }
   "12" { :desc "3 Day Select" }
   "13" { :desc "Next Day Air Saver" }
   "14" { :desc "Next Day Air Early AM" }
   "54" { :desc "Express Plus" }
   "59" { :desc "2nd Day Air AM." }
   "65" { :desc "UPS Saver" }
   "M2" { :desc "First Class Mail" }
   "M3" { :desc "Priority Mail" }
   "M4" { :desc "Expedited Mail Innovations" }
   "M5" { :desc "Priority Mail Innovations" }
   "M6" { :desc "Economy Mail Innovations" }
   "82" { :desc "UPS Today Standard" }
   "83" { :desc "UPS Today Dedicated Courier" }
   "84" { :desc "UPS Today Intercity" }
   "85" { :desc "UPS Today Express" }
   "86" { :desc "UPS Today Express Saver" }
   "96" { :desc "UPS Worldwide Express Freight" }
  } )