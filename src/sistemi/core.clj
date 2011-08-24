(ns sistemi.core
  (:require [clojure.tools.logging :as log])
  (:use (net.cgrand moustache enlive-html)
        (ring.middleware file file-info stacktrace cookies)
        ring.util.response
        [ring.adapter.jetty :only (run-jetty)]
        clj-logging-config.log4j
        clojure.java.browse
        clojure.contrib.strint
        (www.middleware request-id locale spy)
        (app config run-level))
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

;; (enlive/template checkout "pay/checkout.html")
(deftemplate checkout (File. "www/pay/checkout.html") [])

(def handlers
  (app
   wrap-stacktrace
   ;; (wrap-reload '[adder.middleware adder.core])
   wrap-request-id          ; add a unique request id for logging
   wrap-cookies
;;   (spy :prefix "start" :keys [:accept-language :uri :query-string] :response true)
   (spy :prefix "start" :response true)
   wrap-locale              ; get locale from path; redirect if not present
   (spy :prefix "locale" :keys [:locale])
   wrap-file-info
   (wrap-file "www")        ; serve static files from the www directory
   ["pay" "test"] "testing paypal"
   ["pay" "checkout"] (-> (checkout) response constantly)
   ["/"] "not found"
   ;; TODO: Add a custom 404 here.
   ))


;; ===== RING HANDLERS =====
(def handlers (app
               wrap-stacktrace
               ;; (wrap-reload '[adder.middleware adder.core])
               wrap-request-id          ; add a unique request id for logging
               (spy :prefix "before" :keys [:uri :query-string] :response true)

               ;; [[foo #"((en|it|es|fr))"]] "testing"
               [[foo #"(?:en|it|es|fr|de)"] &] (fn [req] (response (str "testing: " foo)))

               wrap-locale              ; get locale from path; redirect if not present
               (spy :prefix "after" :keys [:uri :locale])
               wrap-file-info
               (wrap-file "www")        ; serve static files from the www directory
               ["pay" "test"] "testing paypal"
               ["pay" "checkout"] (-> (checkout) response constantly)
               ;; TODO: Add a custom 404 here.
               ))

;; ? where should templates go?
;; ? how will languages be realized?

;; - pages are localized by prefixing the url with a locale component
;;   ? how does this work?
;;   ? is there a fallback mechanism to select a default page if a locale specific one doesn't exist?
;; - page links use relative paths to preserve the locale portion

;; How to POST forms using links
;; - http://natbat.net/2009/Jun/10/styling-buttons-as-links/
;; - http://www.xlevel.org.uk/post/How-to-style-a-HTML-Form-button-as-a-Hyperlink-using-CSS.aspx
;; - http://www.creativespirits.com.au/treasurechest/replaceSubmitButtonByTextLink.html
;; - http://www.thesitewizard.com/archive/textsubmit.shtml
;; - http://www.velocityreviews.com/forums/t160562-form-with-a-link-instead-of-a-button.html

;; How to Improve the quality of your software: find an old computer 
;; - http://news.ycombinator.com/item?id=2911935

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
