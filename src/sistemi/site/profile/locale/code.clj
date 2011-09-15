(ns sistemi.site.profile.locale
  (:require [www.url :as url]
            [clj-time.core :as time])
  (:use ring.util.response
        ring.persistent-cookies
        [sistemi.handlers :only (assert-method raise-403)]
        [locale.core :only (locales)]))

(defn handle
  "Handles a request to change the user's locale. The new locale setting is persisted in a cookie
   and the user is redirected to the selected locale's version of the referring page."
  [req]
  ;; Validate the request method.
  ;; TODO: define a multi-method to handle POST?
  ;; TODO: or just allow POST routing using moustache?
  (assert-method req :post)

  (let [lang (get-in req [:params :lang])
        locale (locales lang)]

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
;;                  (fs-rest (:uri (url/parse referer))) ; remove locale
                  ;; Translate the uri.
                  ((req :luri) locale ((req :curi) (:uri (url/parse referer))))
                  "/")
            ;;response (redirect (url/canonicalize req (str "/" (name locale) uri)))
            response (redirect (url/canonicalize req uri))]

        ;; Set the cookie.
        (assoc response :cookies
               [(persistent-cookie :locale (name locale) (time/date-time 2020 01 01) {:path "/"})])))))


;; TODO: (time/in 10 :years)
;; TODO: make the above code cleaner.
;; TODO: function to set a cookie for 1 month, quarter, year, decade out 
;; TODO: add tests
;; TODO: function to localize a uri
;; TODO: function to unlocalize a uri

;; NOTE: load-file returns the result of the last form executed in the file.
;; That could be used to set the handler ...
