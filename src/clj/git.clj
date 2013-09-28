(ns git
  "Utility function to call git."
  (:require [clojure.string :as str])
  (:use [clojure.java.shell :only [sh]]))

(defn conf
  "Read a git configuration setting. Example: (git/conf :user :email)."
  [& keys]
  (let [result (sh "git" "config" "--get" (apply str (map name (interpose "." keys))))]
    (str/trim-newline (:out result))))
