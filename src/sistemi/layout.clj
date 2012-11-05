(ns sistemi.layout
  (:require [hiccup.core :as hcp]
            [clojure.string :as str]
            [locale.core :as l]
            [util.path :as path]
            [sistemi.translate :as tr]
            [www.request :as req]))

(def menu-data
  [:home
   :vision
   [:boutique
    {:label :shelves :page "/shelf.htm"}
    {:label :shelving-units :page "/shelving.htm"}
    :tables
    :lamps
    :chairs
    :paint]
   :system
   :feedback])

(defn menu
  []
  (let [cur-page (path/first (:uri req/*req*))]
    (for [item menu-data]
      (if (vector? item)
        ;; submenu
        (let [key (first item)
              label (tr/translate :menu key)]
          [:li.menui [:a.menui {:href "#"} [:span label]]
           [:ul.menum.submenu
            (for [item (rest item)]
              (let [page (if (map? item) (:page item)  (str "/" (name item) ".htm"))
                    label (tr/translate :menu key (if (map? item) (:label item) item))]
                [:li.menui
                 [(keyword (str "a" (if (= page cur-page) "#current_item" "") ".menui")) {:href (tr/localize page)} label]]
                ))]])
        ;; regular item
        (let [page (str "/" (name item) ".htm")
              label (tr/translate :menu item)]
          [:li.menui
           [(keyword (str "a" (if (= page cur-page) "#current_item" "") ".menui")) {:href (tr/localize page)} label]]
          )))))

(defn doctype-html5
 [html]
 (str "<!doctype html>" html))

(defn standard-page
  [head body height]
  (doctype-html5
   (hcp/html
    [:html {:lang (req/*req* :locale)}
     [:head
      [:meta {:http-equiv "Content-Type", :content "text/html; charset=utf-8"}]
      [:title  (tr/translate :title)]
      [:link {:href "/bootstrap/css/bootstrap.css", :rel "stylesheet", :type "text/css"}]
      [:link {:href "/css/layout.css", :rel "stylesheet", :type "text/css"}]
      [:link {:href "/menu/menu.css", :rel "stylesheet", :type "text/css"}]
      [:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" :type "text/javascript"}]
      [:script {:src "/bootstrap/js/bootstrap.js", :type "text/javascript"}]
      [:link {:href "/fonts/stylesheet.css", :rel "stylesheet", :type "text/css"}]
      [:meta {:name "keywords", :content "modern furniture, modern shelves, shelving, shelf, book case, mod furniture, contemporary shelf"}]
      [:meta {:name "description", :content "Modern shelving in Europe."}]

      ;; Plus One Button
      ;; TODO: Internationalize?
      ;; Note: The popup text is truncated in languages other than english.
      [:script {:type "text/javascript" :src "https://apis.google.com/js/plusone.js"}
       #_ "{lang: 'fr'}"]

      head]

     [:body
      ;; standard centered layout
      [:div.container

       ;; ----- TOP BORDER ROW -----
       [:div.row
        [:div.span3.greybox {:style "height: 40px;"}
         [:div.greyborder_br {:style "height: 39px;"}]]
        [:div.span6
         [:div.greyborder_br
          ;; Note: oversized line-height is used to acheive vertical centering
          ;;{:style "line-height: 39px; text-align: center;"}
          ;;(tr/translate :version)
          {:style "line-height: 39px; text-align: center; font-size: 20px; text-transform: uppercase;"}
          (tr/translate :construction)
          ]]]

       ;; ----- HEADER ROW -----
       [:div.row {:style "height: 136px"}
        ;; locale menu
        [:div.span3.greybox
         [:div.greyborder_br {:style "height: 135px"}
          [:div {:style "padding-left: 30px; padding-top: 25px;"}
           [:div#lang 
            (->>
             l/locales
             (map (fn [locale]
                    (if (= locale (req/*req* :locale))
                      [:a.select {:href "#"} locale]
                      [:a {:href (tr/localize "/profile/locale" :query {:lang locale})} locale])))
             (interpose [:span.line "|"]))]

           [:img {:src "/img/block-logo.gif" :alt "logo" :style "margin-bottom: 7px;"}]]]]

        [:div.span6
         [:div#shortcuts.greyborder_br {:style "height: 135px"}
          [:ul {:style "padding-top: 28px;"}
           [:li 
            [:a { :href "#"} (tr/translate :header :signup)]]
           [:li 
            [:a { :href "#"} (tr/translate :header :contact)]]
           [:li 
            [:a { :href (tr/localize "/team.htm")} (tr/translate :header :team)]]
           [:li 
            [:a { :href (tr/localize "/careers.htm")} (tr/translate :header :careers)]]]]]

        [:div.span3
         [:div.greyborder_b {:style "height: 135px"}
          [:a {:href (tr/localize "home.htm")}
           [:img {:src "/img/sistemi-moderni-systems.jpg", :width "206", :height "119" :alt "logo" :style "margin-left: 19px;"}]]]]]

       ;; ----- MENU AND CONTENT ROW -----
       ;; TODO: Find a way to not have to pass the height.
       [:div.row {:style (str "height: " height "px;")}
        ;; Menu
        [:div.span3.greybox {:style "height: 100%;"}
         [:div.greyborder_r {:style "height: 100%;"}
          [:ul.menu.menum
           (menu)] 
          ]]

        ;; ----- CONTENT -----
        ;; TODO: The content height must be passed in dynamically.
        [:div.span9
         body]]

       ;; ----- RED BAR -----
       [:div.row
        [:div#redbar.span12
         [:a.first {:href "#"}
          [:img {:src "/img/facebook.jpg", :border "0" :alt "facebook"}]]
         [:a { :href "#"}
          [:img {:src "/img/twitter.jpg", :border "0" :alt "twitter"}]]
         ;; TODO: Why is google +1 not working?
         [:g:plusone {:size "small" :annotation "none"}]
         ]]

       ;; ----- ADDRESS AND COPYRIGHT -----
       [:div.row
        [:div.span3.greybox
         [:div#footer.greyborder_r
          [:div#address "SISTEMI MODERNI"
           [:br] "St. Martin d&rsquo;Uriage, France"
           [:br] "M. +33 6 09 46 92 00"]
          [:div#copyright (interpose [:br] (tr/translate :copyright))]]]]]]])))
