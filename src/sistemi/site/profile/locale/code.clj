(ns sistemi.site.profile.locale
  (:require [www.url :as url]
            [util.path :as path]
            [www.request :as request]
            [clj-time.core :as time])
  (:use ring.util.response
        ring.persistent-cookies
        [sistemi.handlers :only (throw-403)]
        [locale.core :only (locales)]
        locale.translate
        [util.except :only (safely)]))

(defn handle
  "Handles a request to change the user's locale. The new locale setting is persisted in a cookie
   and the user is redirected to the selected locale's version of the referring page."
  [req]

  (let [lang (get-in req [:params :lang])
        locale (locales lang)
        target (get-in req [:params :target])
        referer (get-in req [:headers "referer"])]

    ;; Validate form parameters.
    (when-not locale (throw-403 req (str "Invalid parameter lang=" lang ".")))

    ;; Validate client supplied inputs and compute the redirect target.
    (let [uri (cond
               target (if-let [url (safely (url/new-URL target) nil)]
                        (if (some #(get url %) [:scheme :host :port])
                          (throw-403 req (str "Invalid parameter target=" target " (not a locale url)."))
                          (str url))
                        (throw-403 req (str "Invalid parameter target=" target ".")))

               referer (if (not (request/self-referred? req)) ; Only allow requests from the same server.
                         (throw-403 req (str "Third party referals not supported (referer=" referer ")."))
                         (localize (canonicalize (:path (url/new-URL referer)) req) (assoc req :locale locale)))

               :default (path/qualify locale))
          response (redirect (str (url/qualify uri req)))]

      (-> response
          (assoc :cookies [(persistent-cookie :locale locale (time/date-time 2020 01 01) {:path "/"})])
          (content-type "text/html; charset=utf-8")))))

;; TODO: (time/in 10 :years)
;; TODO: function to set a cookie for 1 month, quarter, year, decade out
;; TODO: add tests
