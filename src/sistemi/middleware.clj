(ns sistemi.middleware
  (:require [clojure.java.io :as io]
            [clojure.contrib.str-utils2 :as stru]
            [clojure.tools.logging :as log])
  (:use [slingshot.slingshot :only [try+]]
        [util fs])
  (:import java.io.File))

(defn wrap-exception-response
  "Ring wrapper that catches response maps thrown as exceptions from further down the handler stack."
  [app]
  (fn [req]
    (try+
      (app req)
      (catch map? m m))))

(defn wrap-doall
  "Ring wrapper that calls doall on the response content. This forces enlive templates to be
   rendered so that exceptions can be handled in a user friendly manner."
  [app]
  (fn [req]
    (let [response (app req)
          body (:body response)]
      (if (seq? body)
        (assoc response :body (doall body))
        response))))

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
           (assoc m (ffs cpath) (load-file (.getPath file)))
           (catch Exception x
             (log/error x (str "Exception loading file " file))
             m))
         m)))
   {} (dir-seq-bf root)))
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
