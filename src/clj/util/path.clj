(ns util.path
  "Utilities for manipulating / separated paths."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.contrib.str-utils2 :as stru]
            [clojure.core :as core])
  (:import (java.io File)
           (clojure.lang IPersistentMap
                         IPersistentVector))
  (:refer-clojure :exclude [first last rest name]))

;; TODO: Is it useful to merge this with the fs lib?

(defn- to-str
  "Coerces a path to a string."
  [path]
  (str (if (:relative path) "" "/")
       (str/join "/" (:parts path))))

(defrecord Path [#^IPersistentVector parts #^Boolean relative]
  Object
  (toString
    [this]
    (to-str this)))

(defmulti new-path
  "Creates a new path."
  class)

(defmethod new-path String
  [s]
  (let [parts (remove str/blank? (str/split s #"/"))
        relative (not= \/ (core/first s))]
    (new Path parts relative)))

(defmethod new-path File
  [^File file]
  (new-path (.getPath file)))

(defmethod new-path IPersistentMap
  [m]
  (new Path (:parts m) (:relative m)))

(defmethod new-path Path
  [path]
  path)

(defmacro defmulti-path
  "Creates a multimethod with two functions. The first takes a path argument and executes body.
   The second takes a string argument, coerces it to a path and calls the first function."
  [name docstring? & body]
  `(do
     (defmulti ~name ~docstring? class)
     (defmethod ~name IPersistentMap ~@body)
     (defmethod ~name String [s#] (~name (new-path s#)))
     (defmethod ~name File [s#] (~name (new-path s#)))))

(defmulti-path relative?
  "Returns true if the path is a relative path."
  [path]
  (:relative path))

(defmulti-path absolute?
  "Returns true if the path is an absolute path."
  [path]
  (not (relative? path)))

(defmulti-path blank?
  "Returns true if the path is blank (i.e., has no parts)."
  [path]
  (empty? (:parts path)))

(defmulti-path root?
  "Returns true if the path is the root path."
  [path]
  (and (absolute? path) (blank? path)))

(defmulti-path last
  "Returns the name of the last part in the path."
  [path]
  (core/last (:parts path)))

(defmulti-path extension
  "Returns the extension if any."
  [path]
  (if-let [name (last path)]
    (second (re-find #"\.([^\.]+)$" name))))

(defmulti-path name
  "Returns the name of a path including the extension."
  [path]
  (last path))

(defmulti-path basename
  "Returns the basename of a path without the extension (if any)."
  [path]
  (if-let [name (last path)]
    (second (re-find #"^(.*?)(\.[^\.]+)?+$" name))))

(defmulti-path shortname
  "Returns the basename of a path with all extensions removed."
  [path]
  (if-let [name (last path)]
    (second (re-find #"^([^\.]*)" name))))

(defmulti-path parent
  "Returns the parent path of a path. The parent of \"/\" is \"/\" and the parent of \"\" is \"\"."
  [path]
  (assoc path :parts (or (butlast (:parts path)) [])))

(defmulti-path first
  "Returns the name of the first part in the path."
  [path]
  (core/first (:parts path)))

(defmulti-path rest
  "Removes the first path segment from the given path and returns the resulting path."
  [path]
  (assoc path :parts (core/rest (:parts path)) :relative true))

(defn join
  "Joins arguments into a single path."
  [& paths]
  (let [paths (map #(new-path %) paths)]
    (if (empty? paths)
      (new-path "")
      (reduce #(assoc %1 :parts (concat (:parts %1) (:parts %2))) paths))))

(defmulti-path split
  "Splits a path into segments."
  [path]
  (:parts path))

(defn qualify
  "Qualifies a relative path using a parent path or / if no parent path is supplied."
  ([path]
     (join "/" path))
  ([path parent]
     (let [path (new-path path)]
       (if (relative? path)
       (join parent path)
       path))))

(defn joinq
  "Joins arguments into a single path and qualifies the result."
  [& paths]
  (let [paths (map #(new-path %) paths)]
    (qualify (reduce #(assoc %1 :parts (concat (:parts %1) (:parts %2))) paths))))

(defn unqualify
  "Unqualifies a path using a parent path or / if no parent path is supplied."
  ([path]
     (unqualify path "/"))
  ([path parent]
     (let [path (assoc (new-path path) :relative true)
           parent (new-path parent)]
       (reduce (fn [path part]
                 (if (= (first path) part)
                   (rest path)
                   path))
               path
               (:parts parent)))))

(defn sibling
  "Returns the sibling of a path."
  [path sib]
  (let [path (new-path path)
        sib (new-path sib)]
    (join (parent path) sib)))

(defn to-file
  "Calls join on the arguments and the coerces the result to a File."
  [part & parts] (File. ^String (to-str (apply join part parts))))

(defmulti-path exists?
  "Returns true if the path exists."
  [path]
  (.exists ^File (to-file path)))

;; TODO: Delete if not used.
#_(defn canonical
  "Returns the canonical form of a path."
  [path]
  (.getPath (.getCanonicalFile (File. ^String path))))

(defn children
  "Returns a seq of children of a directory."
  [dir]
  (.listFiles (io/file dir)))

;; TODO: implement protocol #'clojure.java.io/Coercions (e.g., as-file)
(defn files
  "Returns a seq of children of a directory that are files."
  [dir]
  (let [dir (io/file (str dir))
        children (.listFiles dir)]
    (filter #(.isFile ^File %) children)))

(defn file-seq-bf
  "Returns a seq of all files and subdirectories of a directory in breadth first order."
  [dir]
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
