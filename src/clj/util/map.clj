(ns util.map
  (:use [clojure.contrib.map-utils :only (deep-merge-with)]))

(defn map-vals [f m]
  "Maps function f over the values of map m."
  (into {} (for [[k v] m] [k (f v)])))

(defn deep-merge [& maps]
  (->> maps
       (remove nil?)
       (apply deep-merge-with (fn [& l] (last l)))))

#_ (let [a {:params {:foo :bar}}
         b {:params {:baz :qux}}]
     (deep-merge a b))

#_ (let [a {:params {:foo :bar}}
         b nil]
     (deep-merge a b))
