;; Enable reflection warnings.
;; TODO: Consider disabling this for production.
;; TODO: Create a leiningen plugin that supresses reflection warnings
;; for all but a selected list of namespaces?
#_ (clojure.core/require 'util.reflection)
#_ (util.reflection/warn-on-reflection
  "clojure.java.classpath" "clojure.contrib" "ring" "clj-logging-config" "clj-stacktrace" "clojure.tools.logging" "clojure.tools.namespace" "ns-tracker.core" "clj-http" "cheshire.generate" "frinj" "postal")

(ns sistemi.core
  (:require [sistemi.logging]
            [taoensso.timbre :as log]
            [www.id :as id]
            [sistemi.init]
            [sistemi.registry :as registry]
            [sistemi.routes :as routes]
            [sistemi.datomic :as d]
            [app.config :as cf]
            git
            [clojure.tools.nrepl.server :as nrepl-server]
            [cider.nrepl :refer (cider-nrepl-handler)])
  (:use [ring.adapter.jetty :only (run-jetty)]
        clojure.java.browse
        clojure.contrib.strint
        locale.core))

(defn -main
  "Main entry point. Runs all startup initialization code. Note: The
   Heroku buildpack aot compiles everything so keep all side-effect code
   out of the top level."
  []

  ;; Generate a semi-unique boot id.
  (id/init!)

  ;; Initialize loggers.
  (sistemi.logging/init! {:boot-id www.id/boot-id})

  (log/info {:event :boot/git :git-branch (git/branch):git-sha (git/sha)})

  ;; ===== CONFIGURATION =====
  (taoensso.timbre/info {:event :boot/config :config (harpocrates.core/redact cf/config)})

  ;; ===== LOCALIZATION =====
  (let [m (cf/conf :internationalization)]
    (set-locales! (m :locales))
    (set-default-locale! (m :default-locale))
    (set-default-territories! (m :default-territories)))

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
  (require 'sistemi.product.bookcase)

  ;; ===== ROUTES =====
  (log/info "Bulding routes.")
  (def routes (routes/build-routes))

  ;; Start nrepl server.
  (log/info "Starting cider-nrepl server on 127.0.0.1:7888.")
  (nrepl-server/start-server :port 7888 :bind "127.0.0.1" :hander cider-nrepl-handler)

  ;; Launch a browser if configured.
  (when (cf/conf :launch-browser)
    (browse-url  (<< "file://~(System/getProperty \"user.dir\")/var/doc/uberdoc.html"))
    (browse-url  (<< "http://localhost:~{(cf/conf :port)}")))

  ;; Start jetty.
  (run-jetty #'routes {:host "127.0.0.1" :port (cf/conf :port)}))
