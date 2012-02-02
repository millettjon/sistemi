(ns sistemi.middleware
  (:require [clojure.java.io :as io]
            [clojure.contrib.str-utils2 :as stru]
            [clojure.tools.logging :as log]
            [util.path :as path])
  (:import java.io.File))

;; how do i tell what the url is for a handler?
;; - before, was loading paths based on the exact path with code.clj appended
;; - now, some name mangling will be required
;; modern-shelving.htm -> modern_shelving_htm.clj
;;   - hmm, - clojure cannot have . in ns name
;;          - java cannot have - or . in class name
;;          - the filename can be anything as long as the ns name is ok
;;            - but load-file must be used in this case (not require)
;;              - hence wrap reload won't work
;;          ? use path-name map in ns to determine actual url path and translations?
;; - remove.clj
;; - convert trailing _htm to .htm
;; - convert _ to -
;; notes:
;; - _ no longer supported in path names
;; - all .clj files are interpreted as handlers?
;; url:  modern-shelving.htm
;; ns:   modern-shelving-htm
;; class/file: modern_shelving_htm
;;
;; ? how does it work w/ wrap-reload?
;; ? can classes register their paths when they get loaded?
;;   - update the global path translation, handler, and string maps?

(defn- load-handlers
  "Loads all page handlers under a directory and returns a
   map of handler functions keyed by canonical uri."
  [root]
  (reduce
   (fn [m dir]
     (let [cname (.getName ^File dir)
           cpath (stru/drop (.getPath ^File dir) (inc (count root))) ; unqualify
           file (io/file dir "code.clj")]
       (if (.exists file)
         ;; Note: The handler def should be the last sexp in the file.
         (try
           (assoc m (str (path/qualify cpath)) (load-file (.getPath file)))
           (catch Exception x
             (log/error x (str "Exception loading file " file))
             m))
         m)))
   {} (path/dir-seq-bf root)))
#_(load-handlers "src/sistemi/site")

(defn wrap-handler
  "Calls a handler if one is defined for the current URI. Otherwise passes the request to the next
   middleware."
  [app root & opts]
  (let [handler-map (load-handlers root)]
    (fn [req]
      (if-let [handler (handler-map (req :uri))]
        (handler req)
        (app req)))))
