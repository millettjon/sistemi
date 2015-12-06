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
        :intro "Sistemi Moderni is all about open sourcing.  We believe that you should have access to the same fabricators we are privileged to work with as their success is our success as well as yours.  Feel free to reach out to them."
        :partners {:dryades {:name "Dryades"
                             :bio [:span "Dryades has been providing wood panel fabrication services to the French marketplace for over 25 years.  Their manufacturing prowess comes from a deep knowledge of wood and the power of automation.  Quality and personalization are their main goals.  Sistemi Moderni is proud to call Dryades a partner.  Click on the link above for their contact information if you need their services beyond the Sistemi Moderni product offering."]}}}
   :fr {}
   })

(defn body
  []
  [:div.text_content
   ;; Ordered loop that wraps fn in html block
   [:p (translate :intro)]

   (for [partner [:dryades]]
     (let [f #(translate :partners partner %)]
       [:div
        [:p.title (f :name) " - BIO"]
        [:p (f :bio)]]))])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
