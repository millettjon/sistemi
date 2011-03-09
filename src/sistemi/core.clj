; --------------------------------------------------
; Main file for clojure Sistemi Moderni web application.

(ns sistemi.core
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use compojure.core
	ring.util.servlet)
  (:require [compojure.route :as route]))

(defroutes main-routes
  (GET "/" [] "<h1>Bonjour Sistemi Moderni Monde!!</h1>")
  (route/not-found "<h1>Page not found</h1>"))

(defservice main-routes)
