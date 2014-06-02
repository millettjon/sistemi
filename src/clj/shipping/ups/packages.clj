(ns shipping.ups.packages
  (:import [java.io ByteArrayInputStream]
           [javax.crypto KeyGenerator])
  (:require [clojure.string :as str]
            [shipping.ups.units :as ups]))


(defn package-is-heavy?
  "Any package weight that exceeds 70 lbs or 31.5 KG is
  a heavy package and requires a special a 'heavy package sticker'.
  Default is KG
  Testing reveals France -> France limit is billing_weight: 26.0 KG

  See: http://www.ups.com/content/us/en/resources/ship/packaging/docs/labels.html#Over+70+lbs+Highlight+Sticker"
  ([weight] (package-is-heavy? weight "kg"))
  ([weight unit]
    ; todo: frinj the correct unit here
    (<= 26 weight) ) )

(defn package-exceeds-max-weight?
  "Does the package exceed the maximum UPS weight?
  Default is in KG"
  [weight]
  (<= 70 weight))

(defn package-exceeds-max-length?
  "If the length exceeds 270 cm. That is the max standard package
  length for UPS."
  [length]
  (<= 270 length))

;; TODO: Dave explain why
;; a) '() fails here
;; b) just use [] instead of list
;; c) or simplify to (* 2 (+ height width))
(defn package-calculate-girth
  "A UPS Girth calculation: 2 x height + 2 x width
  This is where I had to use (list) because '() breaks at compilation"
  [width height]
  (apply + (map #(* 2 %) (list width height)) ) )

(defn package-calculate-ups-length
  "Not sure what this is used for? It adds the actual package
  length to its girth (see calculate-girth)"
  [length height width]
  (+ length (package-calculate-girth width height)) )

; 150 x 48 x 18
; 20 kg
; :billing_weight 26.0 kgs
(defn package-calculate-billing-weight
  "This is the volume / 5000 (from UPS formula).
  This does not perform unit conversion (see frinj)."
  [length height width]
  (float (/ (* length height width) 5000)) )

(defn package-sizing-shelves
  "The shelf package size where most dimensions are fixed. The default
  dimensions are in centimeters (CM see ups_units). For example:
  width:  48 cm
  height: 18 cm
  length: variable [120 - 250] cm
  "
  ([length] (package-sizing-shelves length (ups/ups_units "Cenitmeter")))
  ([length unit]
    {:width 48 :height 18 :length length :unit unit}) )
