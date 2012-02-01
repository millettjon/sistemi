(ns www.request
  "Utility functions for working with ring requests."
  (:require [www.url :as url]))

(def ^:dynamic *req*)

(defn self-referred?
  "Returns true if the request and its referer header reference the same server."
  [req]
  (let [referer (get-in req [:headers "referer"])]
    (apply = (map #(select-keys (url/new-URL %1) [:scheme :host :port]) [req referer]))))
