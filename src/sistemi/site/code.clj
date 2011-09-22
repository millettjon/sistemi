(ns sistemi.site
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response content-type)]
        locale.core)
  (:import java.io.File))

(def language-names
  {"en" "english"
   "fr" "française"
   "de" "deutsch"
   "it" "italiano"
   "es" "español"
   })

(deftemplate html
  (File. "www/raw/index.htm")
  [req strings]
  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (req :locale))

  ;; Set the page title.
  [:title] (content (strings :title))

  ;; Set the refresh target.
  [:head (attr= :http-equiv "refresh")] (set-attr :content (str "160;URL=" ((req :luri) "/modern-shelving.htm")))

  ;; Set the Content-Type to utf-8
  [:head (attr= :http-equiv "Content-Type")] (set-attr :content "text/html; charset=utf-8")

  ;; Translate image slideshow links.
  [:div#slider :a] (set-attr :href ((req :luri) "/modern-shelving.htm"))

  ;; Configure the language selection menu.
  [:ul#nav1 :> :li :> :a] (content (strings :select-a-language))
  [:ul#nav1 :li :ul ] (content (map (fn [lang]
                                      (let [enabled (locales lang)
                                            li {:tag :li :attrs {:lang lang}}
                                            li (if enabled li (assoc-in li [:attrs :class] "notyet"))
                                            href (if enabled
                                                   (str ((req :luri) "/profile/locale")
                                                        "?lang=" lang
                                                        "&target=" ((req :luri) lang "/modern-shelving.htm"))
                                                   "#")
                                            a {:tag :a :attrs {:href href} :content (language-names lang)}]
                                        (assoc li :content [a])))
                                ["en" "fr" "de" "it" "es"]))
  )

;; TODO: Run transformations in lockstep where possible.
;; TODO: Review the whole document for other possible translations.
;; TODO: Translate the alt text.
;; TODO: Make a macro to remove the boilerplate.
;; TODO: Translate meta keywords and meta description.
;; TODO: Write some tests.
;; TODO: /images/loading.gif doesn't exist
;; TODO: Add content-length header.

(defn handle
  [req]
  (-> (html req (req :strings))
      (response)
      ;; Set the content type explicitly since this is served from / and has no .htm extension.
      (content-type "text/html; charset=utf-8")
      ))
