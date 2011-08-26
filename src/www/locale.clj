(ns www.locale
  "Ring functions for managing the user's locale."
  (:require [clojure.string :as str]
            [www.url :as url])
  (:use [clojure.contrib.def :only (defvar defvar-)]
        clojure.contrib.strint
        ring.util.response))

(defvar locales #{:en :es :fr :it}
  "The set of all supported locales.")

(defn to-locale
  "Coerce a string or keyword to a supported locale. Returns nil on failure."
  [s]
  (locales (keyword s)))

(defvar default-locale :en
  "default locale to use if none is specified")

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

(defn wrap-locale
  "Saves the locale in the request map."
  [handler locale]
  (fn [request]
    (handler (assoc request :locale locale))))

(defn locale-redirect
  "Redirect a request for a naked URIs to a locale specific one."
  [req]
  (let [locale (detect-locale req)
        uri (case (:request-method req)
              ;; redirect get and head posts to the localized version of the same url
              (:get :head) (str (let [u (:uri req)]
                                  (if (= "/" u) "" u))  ; strip a lone slash
                                (if-let [qs (:query-string req)]
                                  (str "?" qs)))
              ;; redirect everything else to the locale's root url
              "")]
    (redirect (url/canonicalize req (str "/" (name locale) uri)))))
