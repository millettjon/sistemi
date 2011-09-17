(ns sistemi.middleware
  (:require [clojure.java.io :as io]
            [clojure.contrib.string :as str]
            [clojure.tools.logging :as log])
  (:use [clojure.contrib (condition :only (handler-case *condition*))]
        [util fs])
  (:import java.io.File))

(defn wrap-condition
  "Ring wrapper that handles http error conditions."
  [app]
  (fn [req]
    (handler-case :type
      (app req)
      (handle :http
        (:response *condition*)))))

(defn- load-handlers
  "Loads all page handlers under a directory directory and returns a
   map of handler functions keyed by canonical uri."
  [root]
  (reduce
   (fn [m dir]
     (let [cname (.getName ^File dir)
           cpath (str/drop (inc (count root)) (.getPath ^File dir)) ; unqualify
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
;;(load-handlers "src/sistemi/site")

(defn wrap-handler
  "Calls a handler is one is defined for the current uri. Otherwise pass the request to the next
   middleware."
  [app root & opts]
  (let [handler-map (load-handlers root)]
    (fn [req]
      (if-let [handler (handler-map (req :uri))]
        (handler req)
        (app req)))))