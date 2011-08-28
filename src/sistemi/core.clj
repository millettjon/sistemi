(ns sistemi.core
  (:require [clojure.tools.logging :as log])
  (:use [ring.adapter.jetty :only (run-jetty)]
        clj-logging-config.log4j
        clojure.java.browse
        clojure.contrib.strint
        (app config run-level)
        [www.locale :only (set-translations!)]
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
 "www" {:level :debug})

(log/info (<< "Generated boot-id '~{www.id/boot-id}'."))

;; ===== CONFIGURATION =====
(log/info (<< "Entering run level '~{*run-level*}'."))

(set-config!
   (file-map "etc/default.yaml")
   (file-map  (str "etc/" (name *run-level*) ".yaml"))
   (environment-map "PORT" "DATABASE_URL" "PAYPAL"))

(log/info (<< "Using configuration ~{*config*}."))

;; String translations get their own map.
(set-translations! (file-map "etc/translations.yaml"))

;; ===== SWANK =====
;; Starts a swank server. Useful when running locally from foreman.
;; Note: Uses eval since swank-clojure may not be available in production (e.g., heroku).
(defn start-swank []
  (log/info "Starting swank.")
  (eval '(do (require 'swank.swank)
             (swank.swank/start-server :host "localhost" :port 4005))))

;; ===== MAIN =====
(defn -main
  "Starts jetty.
   Also launches swank and a browser if configured."
  []
  (if (:swank *config*)
    (start-swank))
  (if (:launch-browser *config*)
    (browse-url  (<< "http://localhost:~(:port *config*)")))
  (run-jetty #'routes {:port (:port *config*)}))
