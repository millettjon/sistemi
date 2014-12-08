(ns util.map) 

(defn map-vals [f m]
  "Maps function f over the values of map m."
  (into {} (for [[k v] m] [k (f v)])))
