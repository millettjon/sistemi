(ns locale.core
  "Core definitions for the locale library."
  (:use [util.except :only (check)]))

;; ===== VARS =====
(def locales
  "The set of all supported locales."
   #{})

(def default-locale
  "The default locale."
  nil)

(def default-territories
  ""
  {})

;; ===== FUNCTIONS =====
(defn set-locales!
  "Sets the root binding for locales."
  [locales_]
  (alter-var-root #'locales
                  (constantly (set locales_))))

(defn set-default-locale!
  "Sets the root binding for default-locale."
  [key]
  (alter-var-root
   #'default-locale
   (constantly (check (locales key) "Invalid locale '~A'."))))

(defn set-default-territories!
  "Sets the root binding for default-territories."
  [m]
  (alter-var-root #'default-territories
                  (constantly m)))

(defn full-locale
  "Returns the full locale for a locale prefix."
  [locale]
  (let [territory (get default-territories (keyword locale))]
    (str (name locale) "_" territory)))
