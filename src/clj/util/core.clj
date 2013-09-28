(ns util.core)

(defn contains-in?
  [coll ks]
  (if (empty? ks) (throw (IllegalArgumentException. "Empty key seq not allowed.")))
  (loop [coll coll
         ks ks]
    (let [k (first ks)
          ks (rest ks)]
      (if (contains? coll k)
        (if (empty? ks)
          true
          (recur (get coll k) ks))
        false))))
