(ns www.url
  "URL related functions."
  (:require ring.middleware.params)
  (:require [clojure.string :as str])
  (:use [util.fs :only (fs fs-seq qualify absolute?)])
  (:import (clojure.lang Keyword IPersistentMap)
           (java.net URLDecoder URLEncoder URI)
           (com.google.gdata.util.common.base CharEscapers PercentEscaper)))

(defn- parse-params
  [s]
  (reduce (fn [m [key val]] (assoc m (keyword key) val))
          {}
          (#'ring.middleware.params/parse-params s "UTF-8")))

(defn decode
  "Returns the UTF-8 URL decoded version of the given string."
  [s]
  (URLDecoder/decode s "UTF-8"))

;; encode-data? encode-url
;; Use this for POSTS
;; rename escape-post?
#_(defn encode
  "Returns the UTF-8 URL decoded version of the given string."
  [s]
  (URLEncoder/encode s "UTF-8"))

(def ^PercentEscaper path-escaper
  (CharEscapers/uriPathEscaper))

(defn encode-path-segment
  [^String s]
  (.escape path-escaper s))
#_(encode-path-segment "estantería")

(def ^PercentEscaper query-escaper
  (CharEscapers/uriQueryStringEscaper))

(defn encode-query-segment
  [^String s]
  (.escape query-escaper s))
#_(encode-query-segment "estantería")

(defn encode-path
  [path]
  (when path
    (let [s (apply fs (map encode-path-segment (fs-seq path)))]
      (if (absolute? path)
        (qualify s)
        s))))
#_(encode-path "estantería")

(defn encode-query
  [m]
  (str/join
   "&"
   (map (fn [entry] (str/join "=" (map #(-> %1 name encode-query-segment) entry)))
           m)))
#_(encode-query {:a23 "bí"})

(defrecord URL [#^Keyword scheme #^String host #^Integer port #^String path #^IPersistentMap query]
  Object
  (toString [this]
    (let [a (when scheme
              [(name scheme) ":" "//" host
               (cond (= :http scheme) (if-not (= (or port 80) 80) [":" port])
                     (= :https scheme) (if-not (= (or port 443) 443) [":" port])
                     :default [":" port])
               ])
          a (flatten (filter identity a))
          ;; Encode the path.
          a (if path
              (concat a (let [s (encode-path path)]
                          (if (empty? a) s (qualify s))))
              a)
          ;; Encode the query string
          a (if query
              (concat a ["?" (encode-query query)])
              a)
          ]
      (apply str a))
    ))

#_(str (url "http://fubar.com"))
#_(str (url "http://fubar.com/a/b/estantería?lang=en&target=/estantería-moderna.htm"))
#_(str (url "foo"))
#_(str (url "/foo"))

#_(:query (url "http://fubar.com/a/b/estantería?lang=en&target=/estanter%C3%ADa-moderna.htm"))
#_(str (url "/a/b/estantería"))

#_ (url "a")
#_ (:scheme (url "a"))
#_ ((url "a") :path)

(defmulti url
  "Coerce argument to a URL instance."
  class)

(defmethod url String
  [s]
  (let [uri    (URI. s)
        scheme (.getScheme uri)
        host   (.getHost uri)
        port   (if (not= (.getPort uri) -1) (.getPort uri))
        path   (let [s (.getRawPath uri)] (if (str/blank? s) "/" (decode s)))
        query  (if-let [qs (.getQuery uri)] (parse-params qs))]
    (URL. (keyword scheme) host port path query)))

(defn canonicalize
  "Makes a canonical URL by combining the scheme, server, and port from a request map with the
   desired URI."
  [req uri]
  (let [{server :server-name port :server-port} req]
    (case (:scheme req)
      :http (str "http://" server (case (int port) 80 "" (str ":" port)) uri)
      :https (str "https://" server (case (int port) 443 "" (str ":" port)) uri))))

;; Note: stolen from ring-mock...
(defn parse
  "Parses a URL into a minimal ring request map."
  [uri]
  (let [uri    (URI. uri)
        host   (.getHost uri)
        port   (if (not= (.getPort uri) -1) (.getPort uri))
        scheme (.getScheme uri)
        path   (.getRawPath uri)]
    {:server-port    (or port 80)
     :server-name    host
     :uri            (if (str/blank? path) "/" path)
     :scheme         (keyword scheme)
     }))

(defn self-referred?
  "Returns true if the request and it's referer header reference the same server."
  [req]
  (apply = (map #(select-keys %1 [:scheme :server-name :server-port])
                [req
                 (parse (get-in req [:headers "referer"]))])))

;; TODO: Add this as a test.
;; (self-referred?
;;  {:scheme :http :server-name "localhost" :server-port 5000
;;   :headers {"referer" "http://localhost:5000/foo"}})
