(ns sistemi.routes
  (:use (net.cgrand moustache enlive-html)
        (ring.middleware file file-info stacktrace lint cookies)
        ring.util.response
        (www.middleware request-id spy)
        www.locale)
  (:import (java.io File)))

;; ===== PAYPAL ======
;; Get enlive working:
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
;; setup test coverage: https://github.com/technomancy/radagast

;; (enlive/template checkout "pay/checkout.html")
;;(deftemplate checkout (File. "www/pay/checkout.html") [])

(defn- make-404
  [req]
  (status (response (str "Sorry, we couldn't find what you're looking for (page " 
                   (:uri req "??") 
                   " not found)."))
          404))

(def routes
  (app
   ;; TODO: add a 500 wrapper
   wrap-stacktrace
   wrap-lint
   ;; (wrap-reload '[adder.middleware adder.core])
   ;; TODO: gzip
   ;; TODO: cache control
   wrap-request-id          ; add a unique request id for logging
   wrap-cookies             ; parse cookies and add them to the request
   wrap-file-info

   ;; Locale is the first URI segment.
   [[locale to-locale] &] (app
                               (wrap-locale locale)
                               (wrap-file (str "www/" (name locale)))
                               [&] make-404
                               )

   ;; Handle naked URIs.
   [&] (app
        ;; Serve existing files out of the root directory.
        (wrap-file "www")
        ;; If not file exists, detect a locale and redirect.
        locale-redirect)))

   ;; TODO: Create a 404 error page (per language).
   ;; TODO: Create a 500 error page (see wrap stacktrace?).

;; /en/profile/locale  POST to here

;; How to POST forms using links
;; - http://natbat.net/2009/Jun/10/styling-buttons-as-links/
;; - http://www.xlevel.org.uk/post/How-to-style-a-HTML-Form-button-as-a-Hyperlink-using-CSS.aspx
;; - http://www.creativespirits.com.au/treasurechest/replaceSubmitButtonByTextLink.html
;; - http://www.thesitewizard.com/archive/textsubmit.shtml
;; - http://www.velocityreviews.com/forums/t160562-form-with-a-link-instead-of-a-button.html
