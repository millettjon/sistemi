(ns sistemi.middleware
  (:require [clojure.tools.logging :as log]
            [locale.translate :as tr]
            [sistemi.registry :as registry]
            [util.path :as path]))

(defn wrap-handler
  "Calls a handler if one is defined for the current URI. Otherwise delegates to the next
   middleware."
  [app]
  (fn [req]
    (let [uri-parts (path/split (req :uri))]
      (if-let [handler (get-in registry/handlers (concat uri-parts [:handler]))]
        (handler req)
        (app req)))))

(defn wrap-translate-uri
  "Translates the request uri."
  [app] ; map for translating localized urls to canonical ones
  (fn [req]
    (let [locale (req :locale)
          luri (req :uri)
          fluri (path/joinq locale luri)]
      (if-let [curi (tr/canonicalize-path luri registry/canonicalized-paths (keyword locale))]
        (app (assoc req :uri (str curi)))
        (app req)))))
