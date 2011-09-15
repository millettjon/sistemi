(ns locale.middleware.locale)

(defn wrap-locale
  "Saves the locale in the request map."
  [app locale]
  (fn [req]
    (app (assoc req :locale locale))))
