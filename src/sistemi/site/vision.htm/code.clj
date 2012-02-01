(ns sistemi.site.vision-htm
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        locale.core
        sistemi.translate
        www.request)
  (:import java.io.File))

#_(defn menu-content
  "Returns a transformation function that updates the content of a
   menu item using keys to lookup a string translation."
  [& keys]
  (fn [node]
    (let [n (first (:content node))
          f (content (apply translate :menu keys))]
      (if (string? n)
        ; update the :a node
        (f node)
        ; otherwise update the nested :span node
        (assoc node :content (list (f n)))))))

(defn menu-content
  "Translates a menu item's text."
  [& keys]
  (fn [node]
    (let [n (first (:content node))
          key (mangle-text-to-kw (if (string? n) n (first (:content n))))
          f (content (apply translate :menu (concat keys [key])))]
      (if (string? n)
        ;; update the :a node
        (f node)
        ;; otherwise update the nested :span node
        (assoc node :content (list (f n)))))))

(defn localize-href
  "Localizes the href attribute."
  [node]
  (let [href (get-in node [:attrs :href])]
    ((set-attr :href (localize href)) node)))

(defn menu-item
  "Updates the content and href of a menu item."
  [node]
  (-> node
   localize-href
   ((menu-content))))

(deftemplate html
  (File. "www/raw/vision.htm")
  []

  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (*req* :locale))

  ;; Set the page title.
  [:title] (content (translate :title))

  ;; Set the Content-Type to utf-8
  [:head (attr= :http-equiv "Content-Type")] (set-attr :content "text/html; charset=utf-8")

  ;; Configure the language selection menu.
  [:div#lang :a] (fn [node]
                   (let [lang (-> node :content first str/lower-case)
                         enabled (locales lang)
                         selected (= lang (*req* :locale))]
                     (if enabled
                       (if selected
                         ((add-class "select") node)
                         ((do-> (set-attr :href (localize "/profile/locale" :query {:lang lang}))
                                (remove-class "select")) node))
                       node)))

  ;; TOP CENTER
  ;; version
  ;; TODO: Uncomment once this is text rather than an image.
  ;; [:div#header1 :div.col2b] (content (translate :version))

  ;; cart area
  [:div#cart :> (nth-child 1)] (content (translate :my-account))
  [:div#cart :> (nth-child 3)] (content (translate :cart))

  [:div#header2 :div.col2 :ul] #(at %
                                    [(nth-child 1) :a] (content (translate :sales))
                                    [(nth-child 2) :a] (content (translate :signup))
                                    [(nth-child 3) :a] (content (translate :contact)))

  ;; MENU
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 1) :a] menu-item
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 2) :a] menu-item
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :> :a] (menu-content tr :boutique)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 4) :> :a] (menu-content tr :service)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 5) :> :a] (menu-content tr :system)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 6) :> :a] (menu-content tr :gallery)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 7) :> :a] (menu-content tr :blog)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 8) :> :a] (menu-content tr :feedback)
  [:div#p7PMM_1 :> :ul :> :li :> :a] menu-item

  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 1) :a] (menu-content tr :boutique :shelving)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 1) :a] (do-> localize-href
  ;;                                                                   (menu-content :boutique))
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul :a] (do-> localize-href
                                                      (menu-content :boutique))

  ;;[:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 2) :a] (menu-content :boutique)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 3) :a] (menu-content tr :boutique :lamps)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 4) :a] (menu-content tr :boutique :chairs)
  ;; [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 5) :a] (menu-content tr :boutique :paint)


  ;; COPYRIGHT
  [:div#copyright] (content (interpose {:tag :br} (translate :copyright)))

  ;; CONTENT
  [:div#col3b :div.title] (content (translate :vision :title))
  [:div#col3b :> (nth-child 3)] (html-content (translate :vision :text)))

;; TODO: Remove the parameter "req"
;; TODO: Move this code into the file sistemi/site/vision_htm.clj.
;; TODO: Fix the wrap-handler code to call the correct handler.
(defn handle
  [req]
  (-> (html)
      (response)))
