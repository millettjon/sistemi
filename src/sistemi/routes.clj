(ns sistemi.routes
  (:use net.cgrand.moustache
        (ring.middleware file file-info params keyword-params content-type)
        (ring.middleware stacktrace lint cookies) ; dev items
        (www.middleware request-id spy)
        [locale.core :only (default-locale locales)]
        (locale.middleware locale translate)
        locale.handler.redirect
        (sistemi [handlers :only (make-404)]
                 [middleware :only (wrap-exception-response wrap-handler wrap-doall)])))

(defn build-routes
  []
  (let [code-root "src/sistemi/site"
        [localized-paths canonical-paths] (load-name-translations code-root)
        strings (load-string-translations code-root)]
    (app
     ;; TODO: Add a 500 wrapper (like wrap-stacktrace bug logs)
     ;; TODO: Log POST params?
     ;; TODO: Log request maps for easy replay?
     wrap-lint
     wrap-request-id          ; add a unique request id for logging
     wrap-request-log
     wrap-stacktrace
     wrap-exception-response  ; handle responses thrown as exceptions (e.g., 4xx errors)
     ;; (wrap-reload '[adder.middleware adder.core])
     ;; TODO: gzip
     ;; TODO: cache control (http://groups.google.com/group/ring-clojure/browse_thread/thread/cc8f72a15ae7fbc3)
     wrap-doall               ; force realization of body seq
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
      (wrap-locale locale)
      (wrap-file (str "www/" locale))                       ; Serve locale specific files first.
      (wrap-file (str "www/" default-locale))               ; Fallback to the default locale.
      (wrap-file "www")                                     ; Fallback to the web root.
      (wrap-translate-uri localized-paths canonical-paths)  ; Translate the uri.
      (wrap-translate-strings strings canonical-paths)      ; Add the string translation map.
      (wrap-handler code-root :template-root "www/raw")     ; Call a handler if one is defined for the uri.
      [&] make-404)

     ;; Handle requests for viewing raw templates.
     ["raw" &] (app
                (wrap-file "www/raw")
                (wrap-file "www")
                [&] pass)

     ;; Handle naked URIs.
     [&] (app ; Use a nested app to add middleware.
          (wrap-file "www")  ; Serve files out of the web root.
          [""] (app wrap-detect-locale
                    [&] locale-redirect)
          [&] make-404))
    ))
#_(do (in-ns 'sistemi.core)
      (def routes (build-routes)))
