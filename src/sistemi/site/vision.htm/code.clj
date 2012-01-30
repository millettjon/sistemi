(ns sistemi.site.modern-shelving-html
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        [locale core translate])
  (:import java.io.File))

;; - The request should have a map of strings (not a function).
;; - The lookup function should be separately defined.
;; - The lookup function should use a dynamic var to get the request.
;; - The response must be rendered before leaving the execution scope of the dynamic var.
;;
;; Solutions
;; - function to remap a function with a dynamic variable prepended to the arglist
;; - define two versions of a function: the base one, and the one that uses a dynamic var
;;   ? what naming convention should be used? "foo*" "foo", "foo" "foo-", "foo" "foo_"
;; - remap namespace like w/ fidjet
;;   - string translation, localize and canonicalize urls
;;   - probably not. it seems better to keep functions grouped logically
;; 
;; (tr req :foo)
;; (tr :foo)
;; (tr- :foo)
;; (tr_ :foo)
;; (tr* :foo)
;;
;; (defn translate [req & keys])
;; (defn translate [& keys])
;; 
;; remap-with-arg ?
;; (tr :foo :bar)
;;
;; Revisit if the string and url translation maps belong in the request.
;; - Should those be dynamic variables also?
;;   - if so, when/how should they be set?
;;     - how can they be updated?
;;     - use a background thread to monitor the filesystem and update
;;       the vars
;; - maybe just using regular vars suffices?
;; ? should name.yaml and strings.yaml be .clj files instead?
;;   - easier to reload?

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
  (File. "www/raw/vision.htm")
  [req tr]

  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (req :locale))

  ;; Set the page title.
  [:title] (content (tr :title))

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
  ;; TODO: Uncomment once this is text rather than an image.
  ;; [:div#header1 :div.col2b] (content (tr :version))

  ;; cart area
  [:div#cart :> (nth-child 1)] (content (tr :my-account))
  [:div#cart :> (nth-child 3)] (content (tr :cart))

  [:div#header2 :div.col2 :ul] #(at %
                                    [(nth-child 1) :a] (content (tr :sales))
                                    [(nth-child 2) :a] (content (tr :signup))
                                    [(nth-child 3) :a] (content (tr :contact)))

  ;; MENU
  [:div#p7PMM_1 :> :ul :> (nth-child 1) :a] (menu-content tr :home-page)
  [:div#p7PMM_1 :> :ul :> (nth-child 2) :a] (menu-content tr :vision)
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :> :a] (menu-content tr :boutique)

  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 1) :a] (menu-content tr :boutique :shelving)
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 2) :a] (menu-content tr :boutique :tables)
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 3) :a] (menu-content tr :boutique :lamps)
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 4) :a] (menu-content tr :boutique :chairs)
  [:div#p7PMM_1 :> :ul :> (nth-child 3) :ul (nth-child 5) :a] (menu-content tr :boutique :paint)

  [:div#p7PMM_1 :> :ul :> (nth-child 4) :> :a] (menu-content tr :service)
  [:div#p7PMM_1 :> :ul :> (nth-child 5) :> :a] (menu-content tr :system)
  [:div#p7PMM_1 :> :ul :> (nth-child 6) :> :a] (menu-content tr :gallery)
  [:div#p7PMM_1 :> :ul :> (nth-child 7) :> :a] (menu-content tr :blog)
  [:div#p7PMM_1 :> :ul :> (nth-child 8) :> :a] (menu-content tr :feedback)

  ;; COPYRIGHT
  [:div#copyright] (content (interpose {:tag :br} (tr :copyright)))

  ;; CONTENT
  [:div#col3b :div.title] (content (tr :vision :title))
  [:div#col3b :> (nth-child 3)] (html-content (tr :vision :text))
  )


(defn handle
  [req]
  (-> (html req (req :strings))
      (response)))
