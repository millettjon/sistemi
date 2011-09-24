(clojure.core/require 'util.reflection)
(util.reflection/warn-on-reflection
 "clojure.contrib" "ring" "clj-logging-config" "clj-yaml" "clj-stacktrace")

(ns sistemi.core
  (:require [clojure.tools.logging :as log])
  (:use [ring.adapter.jetty :only (run-jetty)]
        clj-logging-config.log4j
        clojure.java.browse
        clojure.contrib.strint
        (app config run-level)
        app.config.core
        locale.core
        sistemi.routes))

;; ===== LOGGING =====
;; See: https://github.com/malcolmsparks/clj-logging-config
;; %p   priority
;; %X   MDC
;; %c   logger name (class)
;; %m   message
;; %t   thread
(set-loggers!
 :root     {:level :info :pattern "%p|%X{id}|%c|%m%n"}
 "sistemi" {:level :debug}
;; "www" {:level :debug}
 )

(require 'www.middleware.request-id)
(log/info (<< "Generated boot-id '~{www.id/boot-id}'."))

;; ===== RUN LEVEL =====
(init-run-level!)
(log/info (<< "Entering run level '~{run-level}'."))

;; ===== CONFIGURATION =====
(set-config!
   (file-map "etc/default.yaml")
   (file-map  (str "etc/" (name run-level) ".yaml"))
   (environment-map "PORT" "DATABASE_URL" "PAYPAL"))

(log/info (<< "Using configuration ~{config}."))

;; ===== LOCALIZATION =====
(let [m (conf :internationalization)]
  (set-locales! (m :locales))
  (set-default-locale! (m :default-locale)))

;; ===== SWANK =====
;; Starts a swank server. Useful when running locally from foreman.
;; Note: Uses eval since swank-clojure may not be available in production (e.g., heroku).
(defn start-swank []
  (log/info "Starting swank.")
  (eval '(do (require 'swank.swank)
             (swank.swank/start-server :host "localhost" :port 4005))))

;; ===== ROUTES =====
(def routes (build-routes))

;; ===== MAIN =====
(defn -main
  "Starts jetty.
   Also launches swank and a browser if configured."
  []
  (if (conf :swank)
    (start-swank))
  (let [port (conf :port)]
    (when (conf :launch-browser)
      (browse-url  (<< "file://~(System/getProperty \"user.dir\")/autodoc/index.html"))
      (browse-url  (<< "http://localhost:~{port}")))
    (run-jetty #'routes {:port port})))
