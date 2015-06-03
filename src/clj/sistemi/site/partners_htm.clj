(ns sistemi.site.partners-htm
  (:require [util.string :as stru]
            [hiccup.core :as h]
            [hiccup.element :as el]
            [app.config :as cf])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {})

(defn mailto
  "Creates a mailto link for an email defined in the configuration."
  [who content]
  (el/mail-to (cf/conf :email who) content))

;; Puts this, with locale, on tab tittle
(def strings
  {:en {:title "SistemiModerni: Partners"
        :partners {:acme {:name "ACME Corp"
                          :description [:p "Maker of outlandish products that fail or backfire catastrophically at the worst possible times."]}}}
   :fr {}
   })

(defn body
  []
  [:div.text_content
   ;; Ordered loop that wraps fn in html block
   (for [partner [:acme]]
     (let [f #(translate :partners partner %)]
       [:div 
        [:p.title (f :name)]
        [:p (f :description)]]))])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
