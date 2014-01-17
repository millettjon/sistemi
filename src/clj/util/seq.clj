(ns util.seq)

(defn index-of
  "Returns the index of value val in collection coll."
  [coll val]
  (->> coll
       (map-indexed (fn [i elm]
                      (if (= val elm) i)))
       (some identity)))
