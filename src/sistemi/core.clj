; --------------------------------------------------
; Main file for clojure Sistemi Moderni web application.

(ns sistemi.core
  (:use compojure.core
	ring.adapter.jetty)
  (:require [compojure.route :as route])
  (:require [swank.swank]))

(defroutes main-routes
  (GET "/" [] "<h1>Bonjour Sistemi Moderni Monde!!</h1>")
  (route/not-found "<h1>Page not found</h1>"))

; Start swank repl.
; Useful when starting via "lein ring server"
(swank.swank/start-repl)
