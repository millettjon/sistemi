;; Enable reflection warnings.
;; TODO: Consider disabling this for production.
(clojure.core/require 'util.reflection)
(util.reflection/warn-on-reflection
  "clojure.java.classpath" "clojure.contrib" "ring" "clj-logging-config" "clj-stacktrace" "clojure.tools.logging" "clojure.tools.namespace" "ns-tracker.core" "clj-http" "cheshire.generate" "frinj" "postal")

(ns sistemi.core
  (:require [clojure.tools.logging :as log]
            git)
  (:use [ring.adapter.jetty :only (run-jetty)]
        clj-logging-config.log4j
        clojure.java.browse
        clojure.contrib.strint
        (app config run-level)
        [app.config.core :as cfg]
        locale.core))

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
(require 'www.id)
(log/info (<< "Using boot-id '~{www.id/boot-id}'."))

;; ===== RUN LEVEL =====
(init-run-level!)
(log/info (<< "Entering run level '~{run-level}'."))
(log/info (<< "Clojure version: '~(clojure-version)'"))
;; (log/info (str "Classpath: " (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader)))))
;; java.version
;; user.dir
;; java.vm.version
;; java.vm.name
;; java.runtime.version
;; java.vendor

;; ===== CONFIGURATION =====
(use 'sistemi.config)
(log/info (<< "Using configuration ~{(harpocrates.core/redact config)}."))

;; ===== LOCALIZATION =====
(let [m (conf :internationalization)]
  (set-locales! (m :locales))
  (set-default-locale! (m :default-locale))
  (set-default-territories! (m :default-territories)))

;; ===== UNIT CALCULATIONS =====
(require 'frinj.calc)
(frinj.calc/frinj-init!)

;; ===== HANDLERS =====
;; Register request handlers and build routes after localization
;; settings are initialized.
(log/info "Registering request handlers.")
(require '[sistemi.registry :as registry])
(registry/load-files "src/sistemi/site")

;; ===== ROUTES =====
(log/info "About to build routes.")
(use 'sistemi.routes)
(log/info "Bulding routes.")
(def routes (build-routes))

;; ===== SWANK =====
;; Starts a swank server. Useful when running locally from foreman.
;; Note: Uses eval since swank-clojure may not be available in production (e.g., heroku).
(defn start-swank []
  (log/info "Starting swank.")
  (eval '(do (require 'swank.swank)
             (swank.swank/start-server :host "localhost" :port 4005)))

  ;; Inject doc and javadocc into clojure core so they are there for the repl.
  ;; ? Why does this only work inside an require eval?
  ;;   ? Do symbols get evaluated at macro expansion time?
  ;; When compiling, the namespace is set by the ns in the file.
  ;; Later, it is set to "user".
  ;; See Gilardi Scenario: http://technomancy.us/143

  #_ (require 'clojure.java.javadoc) ; unable to resolve symbol ort. WTF?
  #_ (eval (intern 'clojure.core 'javadoc clojure.java.javadoc/javadoc))  ; fails
  #_(eval '(do
           #_(require 'clojure.repl)
           #_(let [orig-ns *ns*]
             (in-ns 'clojure.core)
             (def #^{:macro true} doc #'clojure.repl/doc)
             (in-ns orig-ns))
           ;; Note: This works, but only injects it into namespaces started after this...
           ;; Note: Why is the eval needed?
           (require 'clojure.java.javadoc)
           (intern 'clojure.core 'javadoc clojure.java.javadoc/javadoc))))

;; ===== MAIN =====
(defn -main
  "Starts jetty.
   Also launches swank and a browser if configured."
  []
  (if (conf :swank)
    (start-swank))
  (when (conf :launch-browser)
    (browse-url  (<< "file://~(System/getProperty \"user.dir\")/docs/uberdoc.html"))
    (browse-url  (<< "http://localhost:~{(conf :port)}")))
  (run-jetty #'routes (select-keys cfg/config [:port :host])))

;; can i say conf select-keys?

;; ===== DEVELOPMENT =====
(when (development?)
  (ns user
    (:require [pl.danieljanus.tagsoup :as tagsoup])
    (:use clojure.repl
          clojure.pprint)))
