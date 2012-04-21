(ns www.url
  "Functions for working with URLs."
  (:require ring.middleware.params
            [clojure.string :as str]
            [util.path :as path])
  (:import (clojure.lang Keyword IPersistentMap)
           (java.net URLDecoder URLEncoder URI)
           (com.google.gdata.util.common.base CharEscapers PercentEscaper)
           (util.path Path)))

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

(defn encode-path-part
  "Encodes one segment of a URL path."
  [^String s]
  (.escape path-escaper s))

(defn encode-path
  "Encodes a URL path."
  [path]
  (let [path (path/new-path path)]
    (assoc path :parts (map encode-path-part (:parts path)))))

(def ^PercentEscaper query-escaper
  (CharEscapers/uriQueryStringEscaper))

(defn encode-query-part
  "Encodes one segment of a URL query string (i.e., a key or a value)."
  [^String s]
  (.escape query-escaper s))

(defn encode-query
  "Encodes a URL query string."
  [m]
  (into {} (map
            (fn [[k v]] [k (encode-query-part v)])
            m)))

(defn encode
  "Returns a new URL with the path and query string encoded."
  [url]
  (assoc url
    :path (encode-path (:path url))
    :query (encode-query (:query url))))

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

(defn query-to-str
  "Coerces a query from a map to a string."
  [query]
  (apply str (flatten (interpose "&" (map (fn [entry] (interpose "=" (map name entry))) query)))))

(defrecord URL [#^Keyword scheme #^String host #^Integer port #^IPersistentMap path #^IPersistentMap query #^String fragment]
  Object
  (toString
    [this]
    (let [a [(when scheme [(name scheme) ":"])
             (when host ["//" host])
             (cond
              (nil? port) nil
              (not= port (standard-ports scheme)) [":" port])]
          a (filter identity (flatten a))

          ;; Serialize the path.
          a (let [p (or path "")]
              (concat a [(if (empty? a)
                           p
                           (let [p (path/qualify p)]
                             (if-not (path/root? p)
                               p)))]))
          
          ;; Serialize the query string.
          a (if-not (empty? query)
              (concat a ["?"] (query-to-str query))
              a)
          a (if fragment
              (concat a ["#" fragment])
              a)]
      (apply str a))))

;; ========== CONSTRUCTORS =======================================

(defn- normalize-port
  "Normalizes the port field by setting it to nil if the port is a standard value for the given scheme."
  [url]
  (let [port (:port url)
        scheme (:scheme url)
        port (if (= -1 port)
               nil
               (if (= port (standard-ports scheme))
                 nil
                 port))]
    (assoc url :port port)))

(defn- normalize-path
  "Normalizes the path by forcing it to be absolute if a server name is defined."
  [url]
  (let [path (path/new-path (:path url))]
    (assoc url :path
           (if (and (path/relative? path) (:host url))
             (path/qualify path)
             path))))

(defn- normalize
  [url]
  (-> url
      normalize-port
      normalize-path))

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
        port (.getPort uri)
        path (.getRawPath uri)
        query (if-let [qs (.getQuery uri)] (parse-params qs))
        fragment (.getFragment uri)]
    (normalize (URL. scheme host port path query fragment))))

(defmethod new-URL IPersistentMap
  [m]
  (let [m (if (:server-name m)
            ;; Coerce from a ring request map.
            (normalize (URL. (:scheme m) (:server-name m) (:server-port m) (or (:uri m) "") (:query-params m) (:fragment m)))
            ;; Create directly from a map.
            (merge (URL. nil nil nil nil nil nil) m))]
    m))

(defmethod new-URL Path
  [path]
  (new-URL {:path path}))

(defmethod new-URL URL
  [url]
  url)

;; ========== MISC =======================================
(defn parent
  "Returns the parent of a url."
  [url]
  (let [url (new-URL url)]
    (assoc url :path (path/parent (:path url)))))

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
      :path (path/qualify (:path rurl) (:path aurl)))))

