(ns locale.handler.redirect
  (:require [www.url :as url]
            [util.path :as path])
  (:use [ring.util.response :only [redirect]]))

(defn locale-redirect
  "Redirect a request to the root of the localized site."
  [req]
  (prn "LOCALE-REDIRECT: locale" (req :locale))
  (prn "LOCALE-REDIRECT: qualified path" (path/qualify (req :locale)))
  (prn "LOCALE-REDIRECT: qualified url" (url/qualify (path/qualify (req :locale)) req))
  (redirect (str (url/qualify (path/qualify (req :locale)) req))))
