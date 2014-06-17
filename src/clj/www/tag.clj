(ns www.tag
  (:require [app.config :as cf]
            [clojure.string :as str]))

(defn script
  "Returns a script tag for the given path. Modifies the path if minimization is enabled."
  [path]
  (let [path (if (cf/conf :minimize?)
               (str/replace path #".js" ".min.js")
               path)]
    [:script {:type "text/javascript" :src path}]))
#_ (str/replace "js/main.js" #".js" ".min.js")
#_ (script "js/main.js")
