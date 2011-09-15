(ns locale.core
  "Core definitions for the locale library."
  (:use [clojure.contrib.def :only (defvar)]
        [util.except :only (check)]))

;; ===== VARS =====
(defvar locales #{}
  "The set of all supported locales.")

(defvar default-locale nil
  "The default locale.")

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
