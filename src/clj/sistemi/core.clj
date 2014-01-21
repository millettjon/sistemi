;; Enable reflection warnings.
;; TODO: Consider disabling this for production.
;; TODO: Create a leiningen plugin that supresses reflection warnings
;; for all but a selected list of namespaces?
#_ (clojure.core/require 'util.reflection)
#_ (util.reflection/warn-on-reflection
  "clojure.java.classpath" "clojure.contrib" "ring" "clj-logging-config" "clj-stacktrace" "clojure.tools.logging" "clojure.tools.namespace" "ns-tracker.core" "clj-http" "cheshire.generate" "frinj" "postal")

(ns sistemi.core
  (:require [clojure.tools.logging :as log]
            [www.id :as id]
            sistemi.config
            frinj.jvm
            [sistemi.registry :as registry]
            [sistemi.routes :as routes]
            [app.config :as cf]
            git)
  (:use [ring.adapter.jetty :only (run-jetty)]
        clj-logging-config.log4j
        clojure.java.browse
        clojure.contrib.strint
        locale.core))

(defn -main
  "Main entry point. Runs all startup initialization code. Note: The
   Heroku buildpack aot compiles everything so keep all side-effect code
   out of the top level."
  []

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

  ;; ===== BOOT ID =====
  (id/init!)
  (log/info (<< "Using boot-id '~{www.id/boot-id}'."))
  (log/info (<< "git branch ~{(git/branch)}."))
  (log/info (<< "git sha ~{(git/sha)}."))

  ;; ===== CONFIGURATION =====
  (sistemi.config/init!)
  (log/info (<< "Using configuration ~{(harpocrates.core/redact cf/config)}."))

  ;; ===== LOCALIZATION =====
  (let [m (cf/conf :internationalization)]
    (set-locales! (m :locales))
    (set-default-locale! (m :default-locale))
    (set-default-territories! (m :default-territories)))

  ;; ===== UNIT CALCULATIONS =====
  (frinj.jvm/frinj-init!)

  ;; ===== HANDLERS =====
  ;; Register request handlers and build routes after localization
  ;; settings are initialized.
  (log/info "Loading request handlers.")
  (registry/load-handlers 'sistemi.site "src")

  ;; ===== PRODUCTS =====
  ;; Make sure product namespaces are loaded. This is needed for generic
  ;; pages e.g., cart.htm that work with any item type.
  (require 'sistemi.product)
  (require 'sistemi.product.shelf)

  ;; ===== ROUTES =====
  (log/info "Bulding routes.")
  (def routes (routes/build-routes))

  ;; Start nrepl if enabled.
  (when (cf/conf :nrepl)
    (log/info "Starting nrepl server on 127.0.0.1:7888.")
    (require 'clojure.tools.nrepl.server)
    (eval '(clojure.tools.nrepl.server/start-server :port 7888 :bind "127.0.0.1")))

  ;; Launch a browser if configured.
  (when (cf/conf :launch-browser)
    (browse-url  (<< "file://~(System/getProperty \"user.dir\")/var/doc/uberdoc.html"))
    (browse-url  (<< "http://localhost:~{(cf/conf :port)}")))

  ;; Start jetty.
  (run-jetty #'routes (select-keys cf/config [:port :host])))
