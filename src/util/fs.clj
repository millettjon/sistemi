(ns util.fs
  "Utilities for manipulating / separated paths."
  (:use [clojure.contrib.string :only (split replace-re)]))

(defn fs
  "Joins path segments. Condenses consecutive slashes into a single slash.
   Removes trailing slashes for strings of length > 1."
  ([] nil)
  ([& args]
     (let [args (filter identity args)]
       (if (empty? args)
         nil
         (let [s (apply str (interpose "/" args))
               s (replace-re #"/{2,}" "/" s) ; replace repeated slashes with a single slash
               s (replace-re #"(?<!\A)/$" "" s)] ; remove a trailing slash
           s)))))

(defn ffs
  "Calls fs on the arguments and makes the result fully qualified by prepending a leading / if necessary."
  [& args]
  (let [path (apply fs args)]
    (cond
     (nil? path) nil
     (= \/ (first path)) path
     :default (str "/" path))))

(defn fs-seq
 "Takes a list of paths splits them into segments and returns the flattened seq."
 ([] nil)
 ([arg & args]
    (flatten (map #(split #"/" %1) (concat (list arg) args)))))

(defn parent
  "Returns the parent path of a path or nil if there is no parent."
  ([] nil)
  ([path]
  (apply fs (butlast (fs-seq path)))))
