(ns www.locale
  "Ring functions for managing the user's locale."
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [www.url :as url])
  (:use [clojure.contrib.def :only (defvar defvar-)]
        clojure.contrib.strint
        ring.util.response))

(defvar locales #{:en :es :fr :it :de}
  "The set of all supported locales.")

(defn to-locale
  "Coerce a string or keyword to a supported locale. Returns nil on failure."
  [s]
  (locales (keyword s)))

(defvar default-locale :en
  "The default locale.")

(defvar *locale* default-locale
  "The locale to use when looking up string translations.")

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
   ;; TODO: test this
   ;; Use the locale from their cookie if that was passed.
   (to-locale (get-in req [:cookies "locale" :value]))

   ;; Check if we support a language in their accept-language header.
   (some (fn [[[lang]]]
           (if-let [kw (locales (keyword lang))] kw))
         (parse-accept-language req))

   ;; Else use the default locale.
   default-locale))

(defn wrap-locale
  "Rebinds *locale* to the specified locale."
  [handler locale]
  (fn [request]
    (binding [*locale* locale]
      (handler request))))

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

(defvar- translations {}
  "String translation map.")

(defn set-translations!
  "Sets the root binding for *translations*."
  [m]
  (alter-var-root #'translations (constantly m)))

(defn i18n
  "Returns the string translation for a key sequence in *locale*.  If no translation is
found, returns a default translation.  If no default translation is found, returns the key
sequence."
  [& ks]
  (or (get-in translations (cons *locale* ks))
      (log/warn (str "No translation for: " ks " in locale " *locale* "."))
      (get-in translations (cons default-locale ks))
      (str/join " " (map name ks))))

;; ? What is the problem with using this solution with deftemplate?
;; ? Is there a way to offer the pure functional solution along side?
;; ? Is there a way to simplify the client code using macros instead of dynamic variables?

;; ? is it in a different thread? NO
;; - it is losing the logging id as well
;;   ? how does with-logging-context work? a macro that uses the NDC/MDC
;;
;; ? is it being evaluated outside the binding somehow? YES
;;   the template function must return some sort of lazy sequence...
;;   solutions:
;;   - realize the sequence before returning the request
;;     - realized sequences get cached?
;;     ? maybe in the middleware wrappers?
;;   - convert the sequences to bound sequences
;;     - easier to do from inside enlive since there may be multiple levels of lazyseq etc
;;
;; ? am i misusing dynamic variables?
;; See:
;; http://kotka.de/blog/2009/11/Taming_the_Bound_Seq.html
;; http://cemerick.com/2009/11/03/be-mindful-of-clojures-binding/
;; http://stackoverflow.com/questions/1641626/how-to-covert-a-lazy-sequence-to-a-non-lazy-in-clojure
