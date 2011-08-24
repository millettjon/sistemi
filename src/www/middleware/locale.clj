(ns www.middleware.locale
  "Ring middleware that gets the locale from the path and redirects if not found."
  (:require [clojure.string :as str])
  (:use [clojure.contrib.def :only (defvar defvar-)]
        clojure.contrib.strint
        ring.util.response))

(defvar locales #{:en :es :fr :it}
  "set of supported locales")

(defvar default-locale :en
  "default locale to use if none is specified")

(defvar- locale-uri-pattern
  (let [locale-pattern (apply str (interpose "|" (map name locales)))]
    (re-pattern (<< "^/(~{locale-pattern})(?:/|$)"))))

(defn- parse-locale
  "Returns the locale from a URI or nil if not found."
  [uri]
  (second (re-find locale-uri-pattern uri)))

(defn- parse-accept-language
  "Parses the Accept-language header."
  [req]
  (if-let [al (get (:headers req) "accept-language")]
    (sort-by
     second
     #(compare %2 %1)   ;; note: use compare w/ swapped args since reverse breaks sort stability
     (map (fn [part]
            (let [[l q] (str/split part #";")]
              [(str/split l #"-")
               (if q
                 ;; parse the quality
                 (Double/parseDouble (second (re-matches #"q=(.+)" q)))
                 ;; quality defaults to 1
                 1)]))
          (str/split al #",")))))

(defn- detect-locale
  "Detects the user's preferred locale."
  [req]
  (or
   ;; TODO: check for a locale cookie

   ;; check if we support a language in their accept-language header
   (some (fn [[[lang]]]
           (if-let [kw (locales (keyword lang))] kw))
         (parse-accept-language req))

   ;; else use the default locale.
   default-locale))


;; foo.com/en/profile/locale  POST called by html form, GET allowed manually
;; ? how does moustache routing work when the locale is in the url?


(defn canonicalize-url
  "Makes a canonical URL by combining the scheme, server, and port from a request map with the
   desired URI."
  [req uri]
  (let [{server :server-name port :server-port} req]
    (case (:scheme req)
      :http (str "http://" server (case port 80 "" (str ":" port)) uri)
      :https (str "https://" server (case port 443 "" (str ":" port)) uri))))

(defn wrap-locale
  "Extracts the locale from the request URI and inserts it into the request map.
   If not found, selects a locale and sends a redirect."
  [app]
  (fn [req]
    (if-let [locale (parse-locale (:uri req))]
      ;; add the locale to the request map
      (app (assoc req :locale locale))
      ;; redirect naked uris
      (let [locale (detect-locale req)
            uri (case (:request-method req)
                  ;; redirect get and head posts to the localized version of the same url
                  (:get :head) (str (:uri req)
                                    (if-let [qs (:query-string req)]
                                      (str "?" qs)))
                  ;; redirect everything else to the locale's root url
                  "")]
        (redirect (canonicalize-url req (str "/" (name locale) uri)))))))
