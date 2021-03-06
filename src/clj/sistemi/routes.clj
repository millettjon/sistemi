(ns sistemi.routes
  (:require [sistemi.registry :as registry]
            [util.string :as str]
            [sistemi.datomic :as sd]
            [www.event :as e]
            [sistemi.blog :as b])
  (:use net.cgrand.moustache
        (ring.middleware file file-info params keyword-params content-type session json)
        (ring.middleware stacktrace lint cookies) ; dev items
        www.middleware
        [locale.core :only (default-locale locales)]
        locale.middleware.locale
        locale.handler.redirect
        (sistemi [handler :only (make-404)]
                 [middleware :only (wrap-handler wrap-translate-uri)])))

(defn re-register
  "Callback function for wrap-reload that re registers namespaces under sistemi.site."
  [ns]
  (let [root-ns 'sistemi.site]
    (when (str/starts-with? (str ns) (str root-ns))
      (registry/register-namespace ns root-ns))))

(defn build-routes
  []
  (let [code-root "src/sistemi/site"
        conn (sd/get-conn)]
    (app
     ;; TODO: Add a 500 wrapper (like wrap-stacktrace bug logs)
     ;; TODO: Log POST params?
     ;; TODO: Log request maps for easy replay?
     wrap-ping                ; handles /ping requests to check connectivity and base response time
     (wrap-reload {:callback re-register})  ; reload and re-register namespaces

     wrap-lint
     wrap-request-id          ; add a unique request id for logging
     wrap-request-log         ; log a 1 line request summary
     wrap-stacktrace          ; catch exceptions and 
     wrap-exception-response  ; handle responses thrown as exceptions (e.g., 4xx errors)

     wrap-params              ; parse form and query string params
     wrap-keyword-params      ; keywordize the params map

     (wrap-session            ; reads/writes session data from/to session store
      {:cookie-name "session-id"
       :cookie-attrs {:secure true
                      :http-only true}})

     ;;wrap-cookies            ; convert cookies to/from a map; included by wrap-session

     ;; converts json requests into maps (required for event logging)
     (wrap-json-body {:keywords? true})

     ;; manages browser-id cookie used for javascript event tracking
     (e/wrap-event "event" conn)
     ;;spy

     wrap-file-info
     ;; TODO: make an easier way to set the charset
     ;; TODO: The options should be coerced to a hash automatically.
     ;; TODO: Do this in the handler? e.g., /for root documents?
     (wrap-content-type {:mime-types {"html" "text/html; charset=utf-8"
                                      "htm" "text/html; charset=utf-8"}})

     ;; Handle localized URLs.
     ;; Locale is the first path segment.
     [[locale locales] &]
     (app
      ;; (spy :prefix "before wrap-locale")
      (wrap-locale locale)
      ;; (wrap-file (str "www/" locale))                  ; Serve locale specific files first.
      ;; (wrap-file (str "www/" default-locale))          ; Fallback to the default locale.

      ;; Handle templates and custom handlers.
      wrap-translate-uri          ; Translate the uri.
      wrap-request                ; Binds *req* to the current request.
      wrap-render                 ; Force realization of response seq while *req* is in scope.
      ;; (spy :prefix "before wrap-handler")
      wrap-handler                ; Call a handler if one is defined for the uri.
      (wrap-file "www/raw" {:allow-symlinks? true})    ; Serve static files.
      ["blog" &] b/handle
      [&] pass)

     ;; Redirect the main home page to a localized version.
     [""] [wrap-detect-locale
           [&] locale-redirect]

     ;; For naked URLs, serve static resources out of the raw root.
     [&] [(wrap-file "www/raw" {:allow-symlinks? true}) [&] make-404])))

;; Run this to reload the routes.
#_ (do (in-ns 'sistemi.core)
       (def routes (sistemi.routes/build-routes)))
