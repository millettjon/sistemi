(ns locale.handler.redirect
  (:require [www.url :as url]
            [util.path :as path])
  (:use [ring.util.response :only [redirect]]))

(defn locale-redirect
  "Redirect a request to the root of the localized site."
  [req]
  (redirect (str (url/qualify (path/qualify (req :locale)) req))))
