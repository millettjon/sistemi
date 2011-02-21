; --------------------------------------------------
; Main file for clojure Sistemi Moderni web application.

; --------------------------------------------------
; QUESTIONS
; 
; - Is the stacktrace useful?  (:use ring.middleware.stacktrace)

(ns sistemi.core
  (:use compojure.core
	ring.adapter.jetty)
  (:require [compojure.route :as route])
  (:require [swank.swank]))

(defroutes main-routes
  (GET "/" [] "<h1>Hello Sistemi Moderni World!</h1>")
  (route/not-found "<h1>Page not found</h1>"))

;; Start swank repl.
(swank.swank/start-repl)
