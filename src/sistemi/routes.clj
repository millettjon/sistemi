(ns sistemi.routes
  (:use net.cgrand.moustache
        (ring.middleware file file-info params keyword-params content-type)
        (ring.middleware stacktrace lint cookies reload) ; dev items
        www.middleware
        [locale.core :only (default-locale locales)]
        locale.middleware.locale
        locale.handler.redirect
        (sistemi [handler :only (make-404)]
                 [middleware :only (wrap-handler wrap-translate-uri)])))

(defn build-routes
  []
  (let [code-root "src/sistemi/site"]
    (app
     ;; TODO: Add a 500 wrapper (like wrap-stacktrace bug logs)
     ;; TODO: Log POST params?
     ;; TODO: Log request maps for easy replay?
     wrap-ping                ; handles /ping requests to check connectivity and base response time
     wrap-reload              ; reload changed namespaces
     wrap-lint
     wrap-request-id          ; add a unique request id for logging
     spy
     wrap-request-log         ; log a 1 line request summary
     wrap-stacktrace          ; catch exceptions and 
     wrap-exception-response  ; handle responses thrown as exceptions (e.g., 4xx errors)

     ;; TODO: gzip?
     ;; TODO: cache control (http://groups.google.com/group/ring-clojure/browse_thread/thread/cc8f72a15ae7fbc3)
     wrap-params              ; parse form and query string params
     wrap-keyword-params      ; keywordize the params map
     wrap-cookies             ; convert cookies to/from a map

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
      (spy :prefix "before wrap-locale")
      (wrap-locale locale)
      ;; (wrap-file (str "www/" locale))                  ; Serve locale specific files first.
      ;; (wrap-file (str "www/" default-locale))          ; Fallback to the default locale.

      ;; Handle templates and custom handlers.
      wrap-translate-uri          ; Translate the uri.
      wrap-request                ; Binds *req* to the current request.
      wrap-render                 ; Force realization of response seq while *req* is in scope.
      (spy :prefix "before wrap-handler")
      wrap-handler                ; Call a handler if one is defined for the uri.
      (wrap-file "www/raw")       ; Serve static files.
      [&] pass)

     ;; Handle requests for viewing raw templates.
     ["raw" &] [(wrap-file "www") [&] pass]

     ;; Redirect the main home page to a localized version.
     [""] [wrap-detect-locale
           [&] locale-redirect]

     ;; For naked URLs, serve static resources out of the web root.
     [&] [(wrap-file "www/raw") [&] make-404])))

;; Run this to reload the routes.
#_(do (in-ns 'sistemi.core)
      (def routes (build-routes)))

(ns user
  (:use clojure.repl
        clojure.pprint))
