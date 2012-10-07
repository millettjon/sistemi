(ns paypal.address
  "Convert paypal addresses to UPU addresses."
  (:require upu)
  (:refer-clojure :exclude [format]))

(defn- concat-keyword
  "Concats arguments into a single keyword."
  [& args]
  (keyword (apply str (map name args))))

(defn- GB
  [m prefix]
  {:name (m (concat-keyword prefix :name))
   :street (m (concat-keyword prefix :street))
   :city (m (concat-keyword prefix :city))
   :county (m (concat-keyword prefix :state))
   :code (m (concat-keyword prefix :zip))
   :country "GB"})

(defn to-upu
  "Converts an address from paypal to upu format."
  [m prefix]
  ((ns-resolve 'paypal.address
               (symbol (name ((concat-keyword prefix :countrycode) m)))) m prefix))

(defn format
  "Converts an address to upu format and then calls upu/format."
  [m prefix]
  (upu/format (to-upu m prefix)))
