(ns util.fs
  "Utilities for manipulating / separated paths."
  (:require [clojure.java.io :as io]
            [clojure.contrib.str-utils2 :as stru])
  (:use [clojure.string :only (split)])
  (:import java.io.File))

;; GOALS
;; - easy manipulations of paths
;; - don't use java.io.File since it is superflous
;; - clojurization
;;   - use sequences
;;   - use predicates
;;   - follow idioms
;; - use / as path separator
;;   - coerce internally where necessary for platform operations
;; - encapsulate java interop
;; - batteres included
;;   - typical shell operations
;; - review clojure/java/ruby/python/shell for others
;; - handle both file and url paths
;; FUNCTIONS
;; - is fs (file segment/file system) the right name? (file?)
;; - child?         ; true if a is child of p
;; - empty?         ; true if dir has no children or file size is zero
;; - exists?        ; true if path exists?
;; - root?          ; true if path is "/"
;; - symlink?       ; true if path is a symlink
;; - readlink       ; true if path is a 
;; - dir?           ; true if path is a directory
;; - file?          ; true if path is a file
;; - children       ; seq of children; breadth-first; default depth is 1 (children "/foo" 1)
;; - dirs           ; seq of child directories
;; - files          ; seq of child files
;; - parents        ; seq of parents
;; - breadth-first/ ; macro that sets a binding to make tree seq order breadth first (breadth-first children "/tmp")
;;   order                                                                           (order :breadth-fist expr)
;; - as-unix        ; coerce from dos to unix format
;; - as-dos         ; coerce from unix to dos format
;; - as-local       ; coerce to format of local platform
;; - parent         ; parent path
;; - name           ; file or dir name
;; - extension      ; file extension
;; - parts          ; seq of path segements
;; - canonicalize   ; returns the canonical path
;; - absolute?      ; true if path is absolute
;; - relative?      ; true if path is relative
;; - qualify        ; makes path absolute if not already (using ~ or supplied or current dir)
;; - unqualify      ; makes path relative by removing the supplied prefix (or number of parts)
;; - expand/glob/find    ; returns seq of matching shell expansions (including **/..)
;; MISC
;; - move, copy, delete, touch, temp-file, delete, chmod, chown, extended attributes, get and set times

;; namespace -> fs, file (file/shell)
;; fs -> file, join
;; fs-seq -> parts, split


(defn fs
  "Joins path segments. Condenses consecutive slashes into a single slash.
   Removes trailing slashes for strings of length > 1."
  ([] nil)
  ([& args]
     (let [args (filter identity args)]
       (if (empty? args)
         nil
         (let [s (apply str (interpose "/" args))
               s (stru/replace s #"/{2,}" "/")  ; replace repeated slashes with a single slash
               s (stru/replace s #"(?<!\A)/$" "")]  ; remove a trailing slash
           s)))))

(defn relative?
  "Returns true if the path is a relative path."
  [path]
  (cond
   (nil? path) (throw (NullPointerException.))
   (empty? path) true
   :default (not= \/ (first path))))

(defn absolute?
  "Returns true if the path is an absolute path."
  [path]
  (not (relative? path)))

(defn qualify
  "Qualifies a relative path."
  ([path] (qualify path "/"))
  ([path dir]
     (cond
      (nil? path) nil
      (absolute? path) path
      :default (fs dir path))))

(defn ffs
  "Calls fs on the arguments and makes the result fully qualified by prepending a leading / if necessary."
  [& args]
  (qualify (apply fs args)))

(defn fs-seq
 "Takes a list of paths splits them into segments and returns the flattened seq."
 ([] nil)
 ([& args]
    (let [args (filter #(not (empty? %1)) args)]
      (seq (filter #(not (empty? %1))
                   (flatten
                    (map #(split %1 #"/") args)))))))

(defn fs-rest
  "Removes the first path segment."
  [path]
  (if (nil? path)
      nil
      (apply (if (relative? path) fs ffs) (rest (fs-seq path)))))

(defn parent
  "Returns the parent path of a path. The parent of \"/\" is \"/\" and the parent of \"\" is \"\"."
  [path]
  (let [segments (butlast (fs-seq path))]
    (if (empty? segments)
      (cond (nil? path) nil
            (and (> (count path) 0) (= \/ (nth path 0))) "/"
            :default "")
      (if (relative? path)
        (apply fs segments)
        (apply ffs segments)))))

(defn root?
  "Returns true if the path is the root path i.e., \"/\"."
  ([] nil)
  ([path]
  (= "/" path)))

(defn canonical
  "Returns the canonical form of a path."
  [path]
  (.getPath (.getCanonicalFile (File. ^String path))))

(defn file-seq-bf [dir]
  "Returns a seq of all files and subdirectories of a directory in breadth first order."
  (let [dir (io/file dir)
        children (.listFiles dir)
        subdirs (filter #(.isDirectory ^File %) children)
        files (filter #(.isFile ^File %) children)]
    (concat [dir] files (mapcat file-seq-bf subdirs))))

(defn dir-seq-bf
  "Returns a seq of all subdirectories of a directory in breadth first order."
  [dir]
  (let [dir (io/file dir)
        children (.listFiles dir)
        subdirs (filter #(.isDirectory ^File %) children)]
    (concat [dir] (mapcat dir-seq-bf subdirs))))
