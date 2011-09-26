(ns util.reflection
  "Filters out reflection warning messages from specified namespaces."
  (:require [clojure.string :as str])
  (:import (java.io ByteArrayOutputStream PrintStream PrintWriter)))

;; See: http://blogs.oracle.com/nickstephen/entry/java_redirecting_system_out_and
;; TODO: Write tests.
;; TODO: Log reflection warnings as real warnings.
;; MAYBE: Make into a macro
;; MAYBE: Take a list form for siblings in a nested namespace

(defn- filter-err
  "Filters standard error by passing all records to filter-fn. Output is dropped if filter-fn returns nil."
  [filter-fn]
  (let [err (System/err) #_original-standard-error
        separator (System/getProperty "line.separator")
        baos (proxy [ByteArrayOutputStream] []
               (flush []
                 (locking this
                   (proxy-super flush)
                   (let [record (.toString this)]
                     (proxy-super reset)
                     (if-let [record (cond
                                      (empty? record) record
                                      (= separator record) record
                                      :default (filter-fn record))]
                       (.print err record)
                       (.flush err))))))]
    #_(System/setErr (PrintStream. baos true))
    (alter-var-root (var *err*) (constantly (PrintWriter. (PrintStream. baos true) true)))))

(defn- path-to-ns
  [path]
  (prn "************* path=" path)
  (str/escape {\/ \. \_ \-} path))

(defn warn-on-reflection
  "Sets *warn-on-reflection* to true and replaces System.err with a PrintStream that
   filters reflection warnings for the specified namesapces."
  [& namespaces]
  (.println System/err (str "Filtering *err* to suppress reflection warnings for: " namespaces "."))
  (set! *warn-on-reflection* true)
  (filter-err
   (fn [record]
     (if-let [match (re-find #"^Reflection warning, (.+)\.clj:" record)]
       (let [ns-name (path-to-ns (match 1))]
         (cond
          (contains? namespaces ns-name) nil
          (some #(.startsWith ns-name %1) namespaces) nil
          ;; Log a warning. Prefix a string to prevent capture in recursion.  Will this deadlock?
          ;;:default (log/warn (str "** " record))
          :default record
          ))
       record))))

;; How to pass log messages through without double processing them?
;; - send to logger using an agent to avoid deadlock?
;; - append a magic string to avoid double processing? YUCK
;; Options:
;; - subclass stderr,stdout before logging frameworks are initialized (done)
;; - Then *later* call logging functions?
;;   - 
;; interaction w/ log capture?
;; post process output with another script?

