(ns sistemi.site.modern-shelving-html
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        [locale core translate])
  (:import java.io.File))

(deftemplate html
  (File. "www/raw/modern-shelving.htm")
  [req strings]

  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (req :locale))

  ;; Set the page title.
  [:title] (content (strings :title))

  ;; Set the Content-Type to utf-8
  [:head (attr= :http-equiv "Content-Type")] (set-attr :content "text/html; charset=utf-8")

  ;; Configure the language selection menu.
  [:div#lang :a] (fn [node]
                   (let [lang (-> node :content first str/lower-case)
                         enabled (locales lang)]
                     (if enabled
                       ((set-attr :href (localize "/profile/locale" req :query {:lang lang})) node)
                       node)))

;; (view/localize "/profile/locale" req)
;; (view/localize "/profile/locale" req :query {:lang lang})
;; (str (view/localize "/profile/locale" req) "?lang=" lang)
;; ? if it doesn't return a string, will it be stringified by enlive?

  ;; TODO: check how translation strings are passed in
  ;; TODO: see if some of the boilerplate can be removed
  ;; TODO: see if caching is useful

  ;; TOP CENTER
  ;; version
;  [:div#wrapper :div.col2b] (content (strings :version))
  ;; cart area
;  [:div#cart :> (nth-child 1)] (content (strings :my-account))
;  [:div#cart :> (nth-child 3)] (content (strings :cart))

  ;; [:div#wrapper :div.col2 :ul] #(at %
  ;;                                   [(nth-child 1) :a] (content (strings :sales))
  ;;                                   [(nth-child 2) :a] (content (strings :signup))
  ;;                                   [(nth-child 3) :a] (content (strings :contact)))

  ;; MENU
  ;; TODO: Skip translation transformations for the default locale.
  ;; TODO: ? Use the content to lookup the translation?
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 1) :a] (content (strings :menu :home-page))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 2) :a] (content (strings :menu :vision))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :> :a] (content (strings :menu :boutique))

  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 1) :a] (content (strings :menu :boutique :shelving))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 2) :a] (content (strings :menu :boutique :tables))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 3) :a] (content (strings :menu :boutique :lamps))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 4) :a] (content (strings :menu :boutique :chairs))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 5) :a] (content (strings :menu :boutique :paint))

  ;; [:div#p7PMM_1 :> :ul :> (nth-child 4) :> :a] (content (strings :menu :service))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 5) :> :a] (content (strings :menu :system))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 6) :> :a] (content (strings :menu :gallery))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 7) :> :a] (content (strings :menu :blog))
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 8) :> :a] (content (strings :menu :feedback))
   ;; <div id="content"><div class="col1" style="position: relative; z-index: 10;">
   ;;   <div id="p7PMM_1" class="p7PMMv01">
   ;;     <ul class="p7PMM">
   ;;       <li><a href="#">Home Page</a></li>
   ;;       <li><a href="#">Our Vision</a></li>
   ;;       <li><a href="#">On-Line Boutique</a>
   ;;           <div>
   ;;             <ul>
   ;;               <li><a href="#">Shelving</a></li>
   ;;               <li><a href="#">Tables</a></li>
   ;;               <li><a href="#">Lamps</a></li>
   ;;               <li><a href="#">Chairs</a></li>
   ;;               <li><a href="#">Paint</a></li>
   ;;             </ul>
   ;;           </div>
   ;;       </li>
)

(defn handle
  [req]
  (-> (html req (req :strings))
      (response)))

