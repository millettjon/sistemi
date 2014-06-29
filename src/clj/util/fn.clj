(ns util.fn)

(defn playback
  "Returns a variable arity function that returns values in coll. When coll is exhausted, returns the value of (apply f args)."
  [coll f]
  (let [a (atom coll)]
    (fn [& args]
      (if (empty? @a)
        (apply f args)
        (let [val (first @a)]
          (swap! a rest)
          val)))))
