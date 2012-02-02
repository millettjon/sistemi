(ns sistemi.site
  (:require [locale.translate :as tr])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response content-type)]
        locale.core
        sistemi.translate
        www.request)
  (:import java.io.File))

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
                                            li {:tag :li}
                                            clss (if active "live" "notyet")
                                            href (localize "/profile/locale"
                                                           :query
                                                           {:lang lang
                                                            :target (str (tr/localize-path (:localized-paths *req*) lang "/modern-shelving.htm"))})
                                            a {:tag :a :attrs {:class clss :href href} :content (language-names lang)}]
                                        (assoc li :content [a])))
                                    ["en" "fr" "de" "it" "es"])))

(defn handle
  [req]
  (-> (html)
      response
      ;; Set the content type explicitly since this is served from / and has no .htm extension.
      (content-type "text/html; charset=utf-8")))
