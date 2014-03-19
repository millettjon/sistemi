(ns util.map)

(defn qualify-keys
  "Qualifies bare keywords in map m with a namespace ns. If they key
ns exists in m and points to a submap, the submap is used instead."
  [m ns]
  (let [m (if (map? (m ns))
            (m ns)
            m)]
    (reduce (fn [m [k v]] (assoc m (keyword (name ns) (name k)) v))
            {} m)))
