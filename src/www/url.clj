(ns www.url
  "Functions for working with URLs."
  (:require ring.middleware.params
            [clojure.string :as str]
            [util.fs :as fs])
  (:import (clojure.lang Keyword IPersistentMap)
           (java.net URLDecoder URLEncoder URI)
           (com.google.gdata.util.common.base CharEscapers PercentEscaper)))

;; ========== CONSTANTS =======================================
(def standard-ports
  "Map of standard protocol ports."
  {:http 80
   :https 443})

;; ========== DECODE =======================================
(defn decode
  "Returns the UTF-8 URL decoded version of the given string."
  [s]
  (URLDecoder/decode s "UTF-8"))

;; ========== ENCODE =======================================
;; Use google's escape classes since java.net's URL and URI don't
;; properly escape international characters in the various URL parts
;; (e.g., path, query string etc).

(def ^PercentEscaper path-escaper
  (CharEscapers/uriPathEscaper))

(defn encode-path-segment
  "Encodes one segment of a URL path."
  [^String s]
  (.escape path-escaper s))

(defn encode-path
  "Encodes the entire URL path."
  [path]
  (when path
    (let [s (apply fs/fs (map encode-path-segment (fs/fs-seq path)))]
      (if (fs/absolute? path)
        (or (fs/qualify s) "/")
        s))))

(def ^PercentEscaper query-escaper
  (CharEscapers/uriQueryStringEscaper))

(defn encode-query-segment
  "Encodes one segment of a URL query string (i.e., a key or a value)."
  [^String s]
  (.escape query-escaper s))

(defn encode-query
  "Encodes the URL's query string map."
  [m]
  (str/join
   "&"
   (map (fn [entry] (str/join "=" (map #(-> %1 name encode-query-segment) entry)))
           m)))

;; ========== RECORD =======================================
;;
;; Creating a record to represent URLs has the following advantages:
;; - automatic string conversion via str
;; - multimethod dispatch on URLs as distinct from ring request maps
;;
;; Note: java.net.URI is not that useful as it:
;; - fails to properly encode the path (e.g., international chars are not encoded)
;; - encodes the query string as one chunk and doesn't allow finer
;;   grained control needed to serialize a map
;;
(defrecord URL [#^Keyword scheme #^String host #^Integer port #^String path #^IPersistentMap query]
  Object
  (toString
    [this]
    (let [a [(when scheme [(name scheme) ":"])
             (when host ["//" host])
             (cond
              (nil? port) nil
              (not= port (standard-ports scheme)) [":" port])]
          a (filter identity (flatten a))
          ;; Encode the path.
          a (concat a (let [s (encode-path path)]
                        (if (empty? a) [s]      ; leave a relative path unchanged
                            (if (= "/" path)
                              nil                   ; omit a single "/"
                              [(fs/qualify s)]))))  ; prepend a "/" if the url is not relative 
          ;; Encode the query string
          a (if query
              (concat a ["?" (encode-query query)])
              a)]
      (apply str a))))

;; ========== CONSTRUCTORS =======================================

(defn- normalize
  "Normalizes the path when creating a new URL."
  [url]
  (let [path (:path url)]
    (if (str/blank? path)
      (assoc url :path "/")
      url)))

(defn parse-params
  "Parses a query string into a map. Keys are coerced to keywords. UTF-8 encoding is assumed."
  [s]
  (reduce (fn [m [key val]] (assoc m (keyword key) val))
          {}
          (#'ring.middleware.params/parse-params s "UTF-8")))

(defmulti new-URL
  "Creates a new URL record from the argument."
  class)

(defmethod new-URL String
  [uri]
  (let [uri (URI. uri)
        scheme (keyword (.getScheme uri))
        host (.getHost uri)
        port (let [i (.getPort uri)]
               (if (= -1 i)
                 nil
                 (if (= i (standard-ports scheme))
                   nil
                   i)))
        path (.getRawPath uri)
        query (if-let [qs (.getQuery uri)] (parse-params qs))]
    (normalize (URL. scheme host port path query))))

(defmethod new-URL IPersistentMap
  [m]
  (let [m (if (:server-name m)
            ;; Coerce from a ring request map.
            (URL. (:scheme m) (:server-name m) (:server-port m) (:uri m) (:query-params m))
            ;; Create directly from a map.
            (merge (URL. nil nil nil nil nil) m))]
    (normalize m)))

(defmethod new-URL URL
  [url]
  url)

;; ========== MISC =======================================

(defn qualify
  "Qualifies a relative url using an absolute one. Arguments are coerced to URL."
  [rurl aurl]
  (let [;; coerce arguments to url if necessary
        rurl (new-URL rurl)
        aurl (new-URL aurl)
        urls [rurl aurl]]
    (assoc rurl
      :scheme (some :scheme urls)
      :host (some :host urls)
      :port (some :port urls)
      :path (fs/qualify (:path rurl) (:path aurl)))))

(defn localize
  "Localizes a canonical url. Relative urls are qualified before localization."
  [url req]
  (let [url (new-URL url)
        path ((:luri req) (fs/qualify (:path url) (fs/parent (req :uri))))]
    (merge url {:path path})))
