(ns sistemi.site.modern-shelving-html
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        [locale core translate])
  (:import java.io.File))

(defn menu-content
  "Returns a transformation function that updates the content of a
menu item using keys to lookup a string translation."
  [strings & keys]
  (fn [node]
    (let [n (first (:content node))
          f (content (apply strings :menu keys))]
      (if (string? n)
        ; update the :a node
        (f node)
        ; otherwise update the nested :span node
        (assoc node :content (list (f n)))))))

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
                         enabled (locales lang)
                         selected (= lang (req :locale))]
                     (if enabled
                       (if selected
                         #_((add-class "select") node)
                         ((add-class "select") node)
                         ((do-> (set-attr :href (localize "/profile/locale" req :query {:lang lang}))
                                (remove-class "select")) node))
                       node)))

  ;; TOP CENTER
  ;; version
  ;; [:div#header1 :div.col2b] (content (strings :version))
  ;; cart area
  [:div#cart :> (nth-child 1)] (content (strings :my-account))
  [:div#cart :> (nth-child 3)] (content (strings :cart))

  [:div#header2 :div.col2 :ul] #(at %
                                    [(nth-child 1) :a] (content (strings :sales))
                                    [(nth-child 2) :a] (content (strings :signup))
                                    [(nth-child 3) :a] (content (strings :contact)))

  ;; MENU
  [:div#p7PMM_1 :> :ul :> (nth-child 1) :a] (menu-content strings :home-page)
  [:div#p7PMM_1 :> :ul :> (nth-child 2) :a] (menu-content strings :vision)
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :> :a] (content (strings :menu :boutique))

  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 1) :a] (content (strings :menu :boutique :shelving))
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 2) :a] (content (strings :menu :boutique :tables))
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 3) :a] (content (strings :menu :boutique :lamps))
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 4) :a] (content (strings :menu :boutique :chairs))
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 5) :a] (content (strings :menu :boutique :paint))

  [:div#p7PMM_1 :> :ul :> (nth-child 4) :> :a] (menu-content strings :service)
  [:div#p7PMM_1 :> :ul :> (nth-child 5) :> :a] (menu-content strings :system)
  [:div#p7PMM_1 :> :ul :> (nth-child 6) :> :a] (menu-content strings :gallery)
  [:div#p7PMM_1 :> :ul :> (nth-child 7) :> :a] (menu-content strings :blog)
  [:div#p7PMM_1 :> :ul :> (nth-child 8) :> :a] (menu-content strings :feedback)

  ;; COPYRIGHT
  ;; [:div#copyright] (content (strings :copyright1))
  [:div#copyright] (content (interpose {:tag :br} (strings :copyright)))

  )
;; does it escape html characters?
;      <div id="copyright">Copyright 2011 SISTEMI MODERNI. <br>
;        All rights reserved.</div>

;; should req be a dynamic variable?
;; translate

(defn handle
  [req]
  (-> (html req (req :strings))
      (response)))
