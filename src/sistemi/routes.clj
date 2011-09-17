(ns sistemi.routes
  (:use net.cgrand.moustache
        (ring.middleware file file-info params keyword-params content-type)
        (ring.middleware stacktrace lint cookies) ; dev items
        (www.middleware request-id spy)
        [locale.core :only (default-locale locales)]
        (locale.middleware locale translate)
        locale.handler.redirect
        (sistemi [handlers :only (make-404)]
                 [middleware :only (wrap-condition wrap-handler)])))

(def routes
  (app
   ;; TODO: Add a 500 wrapper (like wrap-stacktrace bug logs. will it need to use (doall to catch template transformation errors?).
   ;; TODO: Log POST params?
   ;; TODO: Log request maps for easy replay?
   wrap-stacktrace
   wrap-lint
   wrap-condition           ; handle 4xx errors raised from below
   ;; (wrap-reload '[adder.middleware adder.core])
   ;; TODO: gzip
   ;; TODO: cache control (http://groups.google.com/group/ring-clojure/browse_thread/thread/cc8f72a15ae7fbc3)
   wrap-request-id          ; add a unique request id for logging
   wrap-params              ; parse form and query string params
   wrap-keyword-params      ; keywordize the params map
   wrap-cookies             ; convert cookies to/from a map
   wrap-file-info
   ;; TODO: make an easier way to set the charset
   ;; TODO: The options should be coerced to a hash automatically.
   (wrap-content-type {:mime-types {"html" "text/html; charset=utf-8"}})

   ;; Handle localized URLs.
   ;; Locale is the first path segment.
   [[locale locales] &]
   (app
    (wrap-locale locale)
    (wrap-file (str "www/" (name locale)))          ; Serve locale specific files first.
    (wrap-file (str "www/" (name default-locale)))  ; Fallback to the default locale.
    (wrap-translate "src/sistemi/site")             ; Translate the uri and add the localized string map.
    (wrap-handler "src/sistemi/site"                ; Call a handler if one is defined for the uri.
                  :template-root "www/raw")
    [&] make-404)

   ;; Handle naked URIs.
   [&]
   (app
    (wrap-file "www")  ; Serve existing files out of the root directory.
    locale-redirect))) ; If no file exists, detect a locale and redirect.
