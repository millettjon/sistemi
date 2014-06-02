(ns util.map
  (:require [clojure.walk :as w]))

(defn qualify-keys
  "Qualifies bare keywords in map m with a namespace ns. If they key
ns exists in m and points to a submap, the submap is used instead."
  [m ns]
  (let [m (if (map? (m ns))
            (m ns)
            m)]
    (reduce (fn [m [k v]] (assoc m (keyword (name ns) (name k)) v))
            {} m)))

;; qualify keys
#_ (qualify-keys
    {:address {:city "Sturgis" :country :US}}
    :address)

;; ? Does this blow up the stack?
;; ? Can this be done with walk?
#_ (defn qk
  [m ns]
  (reduce (fn [m [k v]]
            (assoc m
              (keyword (name ns) (name k))
              (if (map? v)
                (qk v k)
                v)))
          {} m))

(defn- qualify1
  "Qualifies bare keywords in map m with a namespace ns."
  [m ns]
  (reduce (fn [m [k v]] (assoc m (keyword (name ns) (name k)) v))
          {} m))
#_ (qualify1 {:city "Sturgis" :country :US}
                  :address)

(defn- qualify-children
  "If value v is a map, qualfies keywords for each value that is a submap. No map values are not modified."
  [v]
  (if (map? v)
    (reduce (fn [m [k v]]
              (assoc m k
                     (if (map? v)
                       (qualify1 v k)
                       v)))
            {}
            v)
    v
    ))
#_ (qualify-children 3)
#_ (qualify-children {:foo "FOO"})
#_ (qualify-children {:foo {:bar "BAR"}})

(defn qualify
  [m ns]
  ;;(w/postwalk-demo m)
  (qualify1 (w/postwalk qualify-children m) ns))

#_ (qualify
    {:shipping {:address {:city "Sturgis" :country :US}}
     :total 43}
    :order)
#_ {:order/shipping {:shipping/address {:address/city "Sturgis" :address/country :US}}}

(defn- unqualify1
  "Unqualifies keywords in a map."
  [m]
  (reduce (fn [m [k v]]
            (assoc m
              (if (keyword? k)
                (-> k name keyword)
                k)
              v))
          {}
          m))
#_ (unqualify1 {:address/city "Sturgis" :address/country :US :foo "FOO"})
#_ (instance? clojure.lang.IRecord (Rec. "hi"))

(defn unqualify
  [m]
  (w/postwalk (fn [v]
                (cond (instance? clojure.lang.IRecord v) v
                      (map? v) (unqualify1 v)
                      :else v))
              m))
;; Should it ignore records completely?
;; - yes since, if they came from datomic, they had to have already been coerced

#_ (unqualify1 {:order/shipping {:shipping/address {:address/city "Sturgis" :address/country :US}}})
#_ (unqualify {:order/shipping {:shipping/address {:address/city "Sturgis" :address/country :US}}})

#_ (unqualify 
    {:order/sub-total (frinj.ops/fj 1 :m)})

;; works
#_ (unqualify1
    {:order/sub-total (frinj.ops/fj 1 :m)})

#_ (map? (frinj.ops/fj 1 :m)) ; -> true
#_ (unqualify1 (frinj.ops/fj 1 :m)) ; -> loses frinj type
#_ (meta (frinj.ops/fj 1 :m)) ; -> nil
#_ (into (frinj.ops/fj) {})
; is type metadata

;; UnsupportedOperationException Can't create empty: frinj.core.fjv  frinj.core.fjv (core.clj:27)
#_ (empty (frinj.ops/fj))
