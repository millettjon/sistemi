(ns locale.middleware.locale
  (:require [clojure.string :as str])
  (:use locale.core))

(defn wrap-locale
  "Saves the locale in the request map."
  [app locale]
  (fn [req]
    (app (assoc req :locale locale))))

(defn- parse-accept-language
  "Parses the Accept-language header."
  [req]
  (if-let [al (get (:headers req) "accept-language")]
    (sort-by
     second
     #(compare %2 %1) ; note: use compare w/ swapped args since reverse breaks sort stability
     (map (fn [part]
            (let [[l q] (str/split #";" part)]
              [(str/split #"-" l)
               (if q
                 ;; parse the quality
                 (Double/parseDouble (second (re-matches #"q=(.+)" q)))
                 ;; quality defaults to 1
                 1)]))
          (str/split #"," al)))))

(defn- detect-locale
  "Makes a best guess at which locale should be used."
  [req]
  (or
   ;; Check if we support a language in their accept-language header.
   (some (fn [[[lang]]]
           (if-let [locale (locales lang)] locale))
         (parse-accept-language req))

   ;; Else use the default locale.
   default-locale))

(defn wrap-detect-locale
  "Detects the user's locale and adds it to the request. The locale is determined from the locale
   cookie if present and from the client's Accept-Language header otherwise."
  [app]
  (fn [req]
    (let [locale (or (locales (get-in req [:cookies "locale" :value]))
                     (detect-locale req))]
      (app (assoc req :locale locale)))))

