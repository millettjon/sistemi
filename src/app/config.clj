(ns app.config
  "Queries the global configuration."
  (:use [app.config.core]))

;;
;; Configuration maps are expected to be in yaml format.
;; 
;; Requirements of configuration format.
;; - can represent nested data structures
;; - human readable
;; - can add comments (json doesn't support comments)
;; - safe (clj is not safe)
;;
;; Possible Features:
;; - caching and reloading (see fnmap)
;; - log overidden and merged settings
;; - report query statistics to find unused settings (see fnmap)
;; - advanced queries regex, xpath
;; - support more formats: json, xml, properties
;;
;; USAGE:
;; ;; To initialize.
;; (use 'app.config.core)
;;
;; (environment-map)
;; (environment-map "USERNAME" "HOME")
;; (file-map "etc/development.yaml")
;;
;; (set-config!
;;  (file-map "etc/default.yaml")
;;  (file-map  (str "etc/" (name (get-run-level)) ".yaml"))
;;  (environment-map "DATABASE_URL" "paypal"))
;;
;; (merge-configs
;;  (file-map "etc/default.yaml")
;;  (file-map  (str "etc/" (name (get-run-level)) ".yaml"))
;;  (environment-map "DATABASE_URL" "paypal"))
;;
;; To read config values.
;; (use 'app.config)
;; (conf :foo)                 ; return a top level value
;; (conf :foo :bar :baz)       ; return a nested value

(defn conf
  "Gets an entry from the global configuration using the specified keys."
  [& keys]
  (get-in config keys))
