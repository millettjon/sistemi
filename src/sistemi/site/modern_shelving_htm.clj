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

(defn handle
  [req]
  (response (layout/standard-page identity)))

(sistemi.registry/register)
