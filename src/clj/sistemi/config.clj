(ns sistemi.config
  (:require [app.config :as cf]
            [util.path :as path]
            [clojure.string :as str]))

(defn active-profile
  "Returns the active profile of the system."
  []
  (-> "etc/profile" slurp str/trim keyword))

(defn init!
  []
  (cf/set-config!
   (cf/dir-map "etc/profiles/default")
   (cf/dir-map (path/join "etc/profiles" (-> (active-profile) name)))
   (cf/dir-map "etc/profiles/local")
   {:profile (active-profile)}
   ))

#_ (init!)
#_ (harpocrates.core/redact (init!))
