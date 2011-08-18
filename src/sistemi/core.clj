(ns sistemi.core
  (:require [clojure.tools.logging :as log])
  (:use net.cgrand.moustache
        net.cgrand.enlive-html
        ring.util.response
        ring.middleware.file
        ring.middleware.file-info
        ring.middleware.stacktrace
        www.middleware.request-id
        [ring.adapter.jetty :only (run-jetty)]
        clj-logging-config.log4j
        config
        run-level
        clojure.java.browse
        clojure.contrib.strint)
  (:import (java.io File)))

;; TODO: use template namespaces (clojure.contribe.ns-utils immigrate)
;; TODO: use tracing when debugging http://richhickey.github.com/clojure-contrib/trace-api.html

;; ===== LOGGING =====
;; See: https://github.com/malcolmsparks/clj-logging-config
;; %p   priority
;; %X   MDC
;; %c   logger name (class)
;; %m   message
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

;; ===== SWANK =====
;; Starts a swank server. Useful when running locally from foreman.
;; Note: Uses eval since swank-clojure may not be available in production (e.g., heroku).
(defn start-swank []
  (log/info "Starting swank.")
  (eval '(do (require 'swank.swank)
             (swank.swank/start-server :host "localhost" :port 4005))))

;; ===== PAYPAL ======
;; Get enlive working:
;; Create a checkout page with the paypal express checkout button.
;; ? do symlinks work on heroku?
;; - make a call to the paypal NVP API
;;   - get paypal credentials (signature)

;; setup production app on heroku
;; setup staging app on heroku
;; setup ssl on heroku (production and staging)
;; ? move redirect for bare domain to heroku?
;; setup monitoring for all sites
;; - http://newrelic.com/

;; TODO: Create a wrapper?
;; (enlive/template checkout "pay/checkout.html")
(deftemplate checkout (File. "www/pay/checkout.html") [])

;; ? where should templates go?
;; ? how will languages be realized?

;; ===== RING HANDLERS =====
(def handlers (app
               wrap-stacktrace
               ;; (wrap-reload '[adder.middleware adder.core])
               wrap-file-info
               (wrap-file "www")
               wrap-request-id
               ["pay" "test"] "testing paypal"
               ["pay" "checkout"] (-> (checkout) response constantly)
               ;; TODO: Add a custom 404 here.
               ))

;; ===== MAIN =====
(defn -main
  "Starts jetty.
   Also launches swank and a browser if configured."
  []
  (if (:swank *config*)
    (start-swank))
  (if (:launch-browser *config*)
    (browse-url  (<< "http://localhost:~(:port *config*)")))
  (run-jetty #'handlers {:port (:port *config*)}))
