(ns sistemi.core
  (:use net.cgrand.moustache
        net.cgrand.enlive-html
        ring.util.response
        ring.middleware.file
        ring.middleware.file-info
        ring.middleware.stacktrace
        [ring.adapter.jetty :only (run-jetty)]
        [clojure.tools.logging :only (info error)]
        clj-logging-config.log4j))

;;; ===== LOGGING =====
(set-loggers!
 :root {:level :info :pattern "%p|%c|%m%n"}
 "sistemi" {:level :debug})

;;; ===== RING HANDLERS =====
(def handlers (app
               wrap-stacktrace
               ;; (wrap-reload '[adder.middleware adder.core])
               wrap-file-info
               (wrap-file "www")
               ["sailing"] "clear sailing"
               ;; TODO: add a custom 404 here
               ))

;;; ===== SWANK =====
;;; Starts a swank server. Useful when running locally from foreman.
(defn start-swank []
  (info "Starting swank.")
  ;; Use eval here since swank-clojure may not be available in production (e.g., heroku).
  (eval '(do (require 'swank.swank)
             (swank.swank/start-server :host "localhost" :port 4005))))

;;; ===== MAIN =====
(defn -main []
  ;; Start swank if the SWANK environment variable is defined.
  (if (get (System/getenv) "SWANK") (start-swank))
  ;; Start jetty.
  ;; TODO: What is the peformance impact of passing a var here?
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty #'handlers {:port port})))
