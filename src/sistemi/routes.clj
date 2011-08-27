(ns sistemi.routes
  (:require [www.url :as url])
  (:use (net.cgrand moustache enlive-html)
        (ring.middleware file file-info params)
        (ring.middleware stacktrace lint cookies) ; dev items
        ring.util.response
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
(deftemplate select-language
  (File. "www/raw/profile/select-language.html")
  []
  [:div#name] (content "jon"))

(defn change-locale
  "Handles a request to change the user's locale. The new locale setting is persisted in a cookie
 and the user is redirected to the new locale's version of the referring page."
  [req]
  ;; Validate the request method.
  (assert-method req :post)

  (let [lang (get-in req [:params "lang"])
        locale (to-locale lang)]

    ;; Validate the form parameters.
    (when-not locale (raise-403 req (str "Invalid parameter lang=" lang ".")))

    (let [referer (get-in req [:headers "referer"])]
      ;; Validate the referrer. Only allow requests from the same server.
      (and referer
           (not (url/self-referred? req))
           (raise-403 req (str "Third party referals not supported (referer=" referer ").")))

      ;; TODO: set the cookie

      ;; Calculate the localized uri and redirect.
      ;; If referrer is not present then redirect to /.
      (let [uri (if referer
                  (second (re-matches #"/[^/]+(/.*)"
                                      (:uri (url/parse referer)))) ; remove locale
                  "/")]
        (redirect (url/canonicalize req (str "/" (name locale) uri)))))))

;; TODO: function to localize a uri
;; TODO: function to unlocalize a uri


(def routes
  (app
   ;; TODO: Add a 500 wrapper (like wrap-stacktrace bug logs).
   ;; TODO: Log POST params?
   ;; TODO: Log request maps for easy replay?
   spy
   wrap-stacktrace
   wrap-lint
   wrap-condition
   ;; (wrap-reload '[adder.middleware adder.core])
   ;; TODO: gzip
   ;; TODO: cache control
   wrap-request-id          ; add a unique request id for logging
   wrap-params              ; parse form and query string params
   wrap-cookies
   wrap-file-info

   ;; Locale is the first URI segment.
   [[locale to-locale] &] (app
                               (wrap-locale locale)
                               (wrap-file (str "www/" (name locale)))
                               ["profile" "select-language.html"] (-> (select-language) response constantly)
                               [&] make-404)

   ;; Shared handlers.
   ["profile" "locale"] change-locale

   ;; Handle naked URIs.
   [&] (app
        ;; Serve existing files out of the root directory.
        (wrap-file "www")
        ;; If not file exists, detect a locale and redirect.
        locale-redirect)))

   ;; TODO: Create a 500 error page (see wrap stacktrace?).


;; Form
;; - Use POST (to /en/profile/locale)
;;   - search engines won't follow
;;   - works with javascript disabled (how to test this?)
;; - Style buttons as links (if possible).
;; - Params
;;   - language to set (disable selection of language currently being viewed)
;;   - uri to redirect to (can this be detected using referrer?)
;;     ? do all browsers send referrer? apparently but:
;;       - browsers can be configured to not send it
;;       - security proxies can hide or alter it
;;     - safer to not rely on referrer

;; How to POST forms using links
;; - http://natbat.net/2009/Jun/10/styling-buttons-as-links/
;; - http://www.xlevel.org.uk/post/How-to-style-a-HTML-Form-button-as-a-Hyperlink-using-CSS.aspx
;; - http://www.creativespirits.com.au/treasurechest/replaceSubmitButtonByTextLink.html
;; - http://www.thesitewizard.com/archive/textsubmit.shtml
;; - http://www.velocityreviews.com/forums/t160562-form-with-a-link-instead-of-a-button.html
