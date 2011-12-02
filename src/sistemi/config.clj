(ns sistemi.config
  (:use app.run-level
        app.config.core))

(set-config!
   (file-map "etc/default.yaml")
   (file-map  (str "etc/" (name run-level) ".yaml"))
   (environment-map "PORT" "DATABASE_URL" "PAYPAL" "LAUNCH_BROWSER"))
