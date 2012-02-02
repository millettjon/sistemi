(ns sistemi.layout
  (:require [clojure.string :as str]
            [util.path :as path])
  (:use net.cgrand.enlive-html
        locale.core
        sistemi.translate
        www.request))

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
  [& keys]
  (fn [node]
    (-> node
        localize-href
        ((apply menu-content keys)))))

(defn layout
  "Applies layout transformations."
  [node]
  (at node
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
      ;; top level items
      [:div#p7PMM_1 :> :ul :> :li :> :a] (menu-item)

      ;; boutique sub menu
      [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul :a] (menu-item :boutique)

      ;; COPYRIGHT
      [:div#copyright] (content (interpose {:tag :br} (translate :copyright)))))

(defn standard-page
  "Loads a page's template, applies the standard layout transformation, applies the
   passed transformation function fn, and the calls emit*."
  [fn]
  (let [node (html-resource (path/to-file "www/raw" (:uri *req*)))]
    (-> node
         layout
         fn
         emit*)))
