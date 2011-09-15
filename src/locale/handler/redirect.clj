(ns locale.handler.redirect
  (:require [clojure.string :as str]
            [www.url :as url])
  (:use locale.core
        [ring.util.response :only [redirect]]))

(defn- parse-accept-language
  "Parses the Accept-language header."
  [req]
  (if-let [al (get (:headers req) "accept-language")]
    (sort-by
     second
     #(compare %2 %1) ; note: use compare w/ swapped args since reverse breaks sort stability
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
   ;; TODO: Add a test for this.
   ;; Use the locale from their cookie if that was passed.
   (locales (get-in req [:cookies "locale" :value]))

   ;; Check if we support a language in their accept-language header.
   (some (fn [[[lang]]]
           (if-let [kw (locales lang)] kw))
         (parse-accept-language req))

   ;; Else use the default locale.
   default-locale))

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
    (redirect (url/canonicalize req (str "/" locale uri)))))
