(ns locale.handler.translate
  (:require locale.middleware.translate :as))

(defn translate-path
  "Translates a canonical path to a localized one."
  [m path locale]
  (let [result (reduce
               (fn [[parts m] part]
                 (let [m (m part)]
                   [(conj parts (:name m)) m]))
               [[locale] (m locale)]
               (:parts path))]
    (path/new-path {:parts (first result) :relative false})))

(defn untranslate-path
  "Translates a localized path to a canonical one."
  [m path]
  (let [locale (first (:parts path))
        result (reduce
                (fn [[parts m] part]
                  (let [m (m part)]
                    [(conj parts (:name m)) m]))
                [[] (m locale)]
                (rest (:parts path)))]
    (path/new-path {:parts (first result) :relative false})))
