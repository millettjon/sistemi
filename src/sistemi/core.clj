(ns sistemi.core
  ( :use net.cgrand.moustache
         net.cgrand.enlive-html
         ring.util.response
         ring.middleware.file
         ring.middleware.file-info
         ring.middleware.stacktrace
         [ring.adapter.jetty :only [run-jetty]]))

(def handlers (app
               wrap-stacktrace
;    (wrap-reload '[adder.middleware adder.core])
;    (wrap-request-logging)
               wrap-file-info
               (wrap-file "www")
               ["sailing"] "clear sailing"
               ))


;; (run-jetty #'handlers {:port 8080 :join? false})

;; Starts a swank server. Useful when running locally from foreman.
(defn start-swank []
  (println "Starting swank.")
  ; Use eval here since swank-clojure may not be available in production (e.g., heroku).
  (eval '(do (require 'swank.swank)
             (swank.swank/start-server :host "localhost" :port 4005))))

(defn -main []
  ; Start swank if the SWANK environment variable is defined.
  (if (get (System/getenv) "SWANK") (start-swank))
  ; Start jetty.
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty #'handlers {:port port})))

;; ? did the original app use a var?
;;   - use a var in dev mode?
;;   ? what is the performance impact of using a var?

;; SNIPPETS
;; (handlers {:request-method :get :uri "/"})

;; - get request logging working
;;     - http://devcenter.heroku.com/articles/logging#writing_to_your_log
;;     - http://stackoverflow.com/questions/2671454/heroku-how-to-see-all-the-logs
;;     ? remote syslog server?
;; - get reloading working in dev environment
;;   - 
;; - get working on heroku
;; - remove gae related stuff
;; - checkin to github
;; - get a dev environment working for copeland
;;   - http://stackoverflow.com/questions/5939878/git-make-development-and-master-track-different-repos-sensible
;; - review lisa's document and identify server side tasks
;; - paypal integration
;; - enable SSL (www and naked domain)
;; - design dev workflow
;;   - http://devcenter.heroku.com/articles/git
;;   - www.sistemimoderni.com
;;   - test.sistemimoderni.com
;;   - dev.sistemimoderni.com
;; - get CI server working
