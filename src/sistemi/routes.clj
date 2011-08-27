(ns sistemi.routes
  (:require [www.url :as url]
            [clj-time.core :as time])
  (:use (net.cgrand moustache enlive-html)
        (ring.middleware file file-info params keyword-params)
        (ring.middleware stacktrace lint cookies) ; dev items
        ring.util.response
        ring.persistent-cookies
        sistemi.handlers
        (www.middleware request-id spy)
        www.locale)
  (:import (java.io File)))

;; ======
;;:
;; Create a checkout page with the paypal express checkout button.
;; ? do symlinks work on heroku?
;; - make a call to the paypal NVP API
;;   - get paypal credentials (signature)

;; setup production app on heroku
;; setup staging app on heroku
;; setup ssl on heroku (production and staging)
;; ? move redirect for bare domain to heroku?
;; setup monitoring for all sites
;; - http://newrelic.com/

;; DIRECTORY LAYOUT
;; - static files
;;   - common /
;;   - localized /en
;;     ? fallback to common if no localized version?
;; - templates and snippets
;;   - common      /raw
;;   - localized   /raw/en
;;     ? fallback to common if no localized version?

;; - create an html form to change the language
;; - create a handler to handle the form POST
;; - store the language selection in a cookie
;; - read the language cookie when redirecting from naked uris
;; - create a shared version ofselect-language.html
;; - see if string translation is useful for reuse of the same template
;;   - "Select Language" -> "Cambiar Idioma"
;;   ? how to balance this versus doing it directly in the html (e.g., multiple copies)
;;   - ideally,
;;     - share one template and insert string translations
;;     - fallback to english if no translation is provided

;; TODO: Factor this out to a new file.
;; TODO: Do we want html in the url? probably since ultimately it is html format.
;; TODO: Figure out how to make templates reload during development.
;; TODO: Localize the strings to spanish.
(deftemplate select-language
  (File. "www/raw/profile/select-language.html")
  []
  [:div#name] (content "jon"))

(defn change-locale
  "Handles a request to change the user's locale. The new locale setting is persisted in a cookie
 and the user is redirected to the selected locale's version of the referring page."
  [req]
  ;; Validate the request method.
  (assert-method req :post)

  (let [lang (get-in req [:params :lang])
        locale (to-locale lang)]

    ;; Validate the form parameters.
    (when-not locale (raise-403 req (str "Invalid parameter lang=" lang ".")))

    (let [referer (get-in req [:headers "referer"])]
      ;; Validate the referrer. Only allow requests from the same server.
      (and referer
           (not (url/self-referred? req))
           (raise-403 req (str "Third party referals not supported (referer=" referer ").")))

      ;; Calculate the localized uri and redirect.
      ;; If referrer is not present then redirect to /.
      (let [uri (if referer
                  (second (re-matches #"/[^/]+(/.*)"
                                      (:uri (url/parse referer)))) ; remove locale
                  "/")
            response (redirect (url/canonicalize req (str "/" (name locale) uri)))]

        ;; Set the cookie.
        (assoc response :cookies
          [(persistent-cookie :locale (name locale) (time/date-time 2020 01 01) {:path "/"})])))))

;; TODO: add tests
;; TODO: move the above code somewhere else
;; TODO: make the above code cleaner.
;; TODO: function to set a cookie for 1 month, quarter, year, decade out 
;; TODO: function to localize a uri
;; TODO: function to unlocalize a uri


(def routes
  (app
   ;; TODO: Add a 500 wrapper (like wrap-stacktrace bug logs).
   ;; TODO: Log POST params?
   ;; TODO: Log request maps for easy replay?
   wrap-stacktrace
   wrap-lint
   wrap-condition           ; handle 4xx errors raised from below
   ;; (wrap-reload '[adder.middleware adder.core])
   ;; TODO: gzip
   ;; TODO: cache control
   wrap-request-id          ; add a unique request id for logging
   wrap-params              ; parse form and query string params
   wrap-keyword-params      ; keywordize the params map
   wrap-cookies             ; convert cookies to/from a map
   wrap-file-info

   ;; Locale is the first URI segment.
   [[locale to-locale] &] (app
                               (wrap-locale locale)
                               (wrap-file (str "www/" (name locale)))          ; serve locale specific files first
                               (wrap-file (str "www/" (name default-locale)))  ; but fallback to the default locale
                               ["profile" "select-language.html"] (-> (select-language) response constantly)
                               [&] make-404)

   ;; Shared handlers.
   ["profile" "locale"] change-locale

   ;; Handle naked URIs.
   [&] (app
        ;; Serve existing files out of the root directory.
        (wrap-file "www")
        ;; If no file exists, detect a locale and redirect.
        locale-redirect)))

