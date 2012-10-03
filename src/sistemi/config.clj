(ns sistemi.config
  (:require [app.config.core :as conf]
            [util.environment :as env]
            [util.path :as path])
  (:use app.run-level))

(defn- dir-map
  "Loads a directory of conf files using the approprate gpg configuration if one exists."
  [dir]
  (let [name (path/name dir)
        passmap (env/eval "GPG_PASSPHRASE")
        passphrase ((keyword name) passmap)
        home (str (path/join dir ".gnupg"))]
    (if (or passphrase (path/exists? home))
      (conf/dir-map dir :gpg {:passphrase passphrase :home home})
      (conf/dir-map dir))))

(conf/set-config!
   (dir-map "etc/default")
   (dir-map (path/join "etc" (name run-level)))
   (conf/environment-map "PORT" "LAUNCH_BROWSER"))
