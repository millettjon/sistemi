(ns sistemi.site
  (:require [locale.translate :as tr]
            [sistemi.registry :as registry])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response content-type)]
        locale.core
        sistemi.translate
        www.request)
  (:import java.io.File))


(def strings
  {:en {; splash page
        :title "Modern Shelving Furniture : Sistemi Moderni"
        :select-a-language "Select A Language"

        ; layout
        :version "alpha version"
        :account "My Account"
        :cart "Cart"
        :signup "Sign Up For Emails"
        :contact "Contact Us"
        :menu {:home "home"
               :vision "our vision"
               :boutique  {:_ "on-line boutique"
                                   :shelving "shelving"
                                   :tables "tables"
                                   :lamps "lamps"
                                   :chairs "chairs"
                                   :paint "paint"}
               :service "at your service"
               :system "the system"
               :gallery "gallery"
               :blog "blog"
               :feedback "feedback"}
        :copyright ["Copyright 2011 SISTEMI MODERNI."
                    "All rights reserved."]}

   :es {; splash page
        :title "Muebles de Estantería Moderna : Sistemi Moderni"
        :select-a-language "Eligir Idioma"

        ; layout
        :version "versión alpha"
        :account "Mi Cuenta"
        :cart "Carro"
        :signup "Recibir Correos"
        :contact "Contáctenos"
        :menu {:home "hogar"
               :vision "nuestro visión"
               :boutique {:_ "boutique en línea"
                          :shelving "estantaría"
                          :tables "mesas"
                          :lamps "lámparas"
                          :chairs "sillas"
                          :paint "pintura"}
               :service "a su servicio"
               :system "el sistema"
               :gallery "galería"
               :blog "blog"
               :feedback "sugerencias"}
        :copyright ["© 2011 SISTEMI MODERNI."
                    "Todos los derechos reservados."]}

   :fr {; splash page
        :select-a-language "Choisissez une Langue"

        ; layout
        :account "Mon Compte"
        :cart "Panier"
        :signup "S’abonner aux emails"
        :contact "Contactez Nous"
        :menu {
               :home "Accueil"
               :vision "Notre Vision"
               :boutique {:_ "Boutique En Ligne"
                          :shelving "Etagères"
                          :tables "Tables"
                          :lamps "Eclairage"
                          :chairs "Chaises"
                          :paint  "Peinture"}
               :service "A Votre Service"
               :system "Le Système"
               :gallery "Gallérie"
               :blog "blog"
               :feedback "Vos Impressions"}
        :copyright ["© 2011 SISTEMI MODERNI."
                    "(all rights reserved)"]}})


(def language-names
  {"en" "english"
   "fr" "française"
   "de" "deutsch"
   "it" "italiano"
   "es" "español"})

(deftemplate html
  (File. "www/raw/index.htm")
  []
  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (*req* :locale))

  ;; Set the page title.
  [:title] (content (translate :title))

  ;; Set the refresh target.
  [:head (attr= :http-equiv "refresh")] (set-attr :content (str "16;URL=" (localize "/modern-shelving.htm")))

  ;; Set the Content-Type to utf-8
  [:head (attr= :http-equiv "Content-Type")] (set-attr :content "text/html; charset=utf-8")

  ;; Translate image slideshow links.
  [:div#slider :a] (set-attr :href (localize "/modern-shelving.htm"))

  ;; Configure the language selection menu.
  [:ul#nav1 :> :li :> :a] (content (translate :select-a-language))
  [:ul#nav1 :li :ul ] (content (map (fn [lang]
                                      (let [active (= (locales lang) (*req* :locale))
                                            clss (if active "live" "notyet")
                                            enabled (contains? locales lang)
                                            href (if enabled (localize "/profile/locale"
                                                                      :query
                                                                      {:lang lang
                                                                       :target (str (tr/localize-path "/modern-shelving.htm" registry/localized-paths (keyword lang)))})
                                                     "")
                                            a {:tag :a :attrs {:class clss :href href} :content (language-names lang)}]
                                        {:tag :li :content [a]}))
                                    ["en" "fr" "de" "it" "es"])))

(defn handle
  [req]
  (-> (html)
      response
      ;; Set the content type explicitly since this is served from / and has no .htm extension.
      (content-type "text/html; charset=utf-8")))

(sistemi.registry/register)
