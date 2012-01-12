(ns locale.handler.redirect
  (:require [www.url :as url])
  (:use [util.fs :only (ffs)]
        [ring.util.response :only [redirect]]))

(defn locale-redirect
  "Redirect a request to the root of the localized site."
  [req]
  (redirect (str (url/qualify (ffs (req :locale)) req))))
