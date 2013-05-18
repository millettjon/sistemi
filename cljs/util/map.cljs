(ns util.map)

;; Copied from clojure.contrib.map_utils which can't be used as a
;; crossover due to it's use of macros.
(defn deep-merge-with
  "Like merge-with, but merges maps recursively, applying the given fn
	  only when there's a non-map at a particular level.
	 
	  (deepmerge + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
	               {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}})
	  -> {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}"
  [f & maps]
  (apply
   (fn m [& maps]
     (if (every? map? maps)
       (apply merge-with m maps)
       (apply f maps)))
   maps))
