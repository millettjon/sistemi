(ns sistemi.layout
  (:require [hiccup.core :as hcp]
            [clojure.string :as str]
            [locale.core :as l]
            [util.path :as path]
            [sistemi.translate :as tr]
            [sistemi.format :as fmt]
            [www.request :as req]
            [www.user-agent :as ua]
            [www.cart :as cart]
            [www.google :as g]
            [sistemi.order :as order]
            [util.net :as net]
            [util.calendar :as cal])
  (:use app.config))

(def menu-data
  [:home
   :vision
   [:boutique
    {:label :shelves :page "/shelf.htm"}
    {:label :bookcases :page "/bookcase.htm"}]
   :system
   :feedback
   :blog
   ])

(defn menu
  []
  (let [cur-page (path/first (:uri req/*req*))]
    (for [item menu-data]
      (if (vector? item)
        ;; submenu
        (let [key (first item)
              label (tr/translate :menu key)]
          [:li.menui [:a.menui {:href "#" :style {:height "20px"}} [:span label]]
           [:ul.menum.submenu
            (for [item (rest item)]
              (let [page (if (map? item) (:page item)  (str "/" (name item) ".htm"))
                    label (tr/translate :menu key (if (map? item) (:label item) item))]
                [:li.menui
                 [(keyword (str "a" (if (= page cur-page) "#current_item" "") ".menui")) {:href (tr/localize page) :style {:height "20px"}} label]]
                ))]])
        ;; regular item
        (let [page (str "/" (name item) ".htm")
              label (tr/translate :menu item)]
          [:li.menui
           [(keyword (str "a" (if (= page cur-page) "#current_item" "") ".menui")) {:href (tr/localize page) :style {:height "20px"}} label]]
          )))))

(defn cart-block
  []
  (let [cart (-> req/*req* cart/get)]
    (if (cart/empty? cart)
      [:li [:i.fa.fa-shopping-cart.fa-lg.fa-fw] " (0)"]
      [:li 
       [:a { :href (tr/localize "/cart.htm")}
        [:i.fa.fa-shopping-cart.fa-lg.fa-fw] " (" (order/total-items cart) ") "
        (-> cart :price :total fmt/eur-short)
        [:br]
        [:i.fa.fa-truck.fa-lg.fa-fw {:style {:margin-top "10px"}}] " " (-> 15 cal/business-days cal/format-france)

        #_ (tr/translate :cart)]])))
;; TODO: Display date in locale of request.
;; TODO: Calculate date in locale of fabrication chain.
;; TODO: Calculate date when cart changes or shipping address changes.

(defn doctype-html5
 [html]
 (str "<!doctype html>" html))

(defn standard-page
  [head body sidebar]
  (doctype-html5
   (hcp/html
    [:html {:lang (req/*req* :locale)}
     [:head
      [:meta {:http-equiv "Content-Type", :content "text/html; charset=utf-8"}]
      [:title  (tr/translate :title)]
      [:link {:href "/bootstrap/css/bootstrap.min.css", :rel "stylesheet", :type "text/css"}]
      [:link {:href "/bootstrap/css/custom.css", :rel "stylesheet", :type "text/css"}]
      [:link {:href "/css/layout.css", :rel "stylesheet", :type "text/css"}]
      [:link {:href "/menu/menu.css", :rel "stylesheet", :type "text/css"}]
      [:link {:href "//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" :rel "stylesheet"}]

      [:script {:src (if (and (conf :offline-enabled) (net/offline?))
                       "/js/jquery-1.7.1.min.js"
                       (www.url/match-scheme "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" req/*req*))
                :type "text/javascript"}]

      [:script {:src "/bootstrap/js/bootstrap.js", :type "text/javascript"}]
      [:script {:type "text/javascript" :src "/3d/detector.js"}]
      [:link {:href "/fonts/stylesheet.css", :rel "stylesheet", :type "text/css"}]
      [:meta {:name "keywords", :content "modern furniture, modern shelves, shelving, shelf, book case, mod furniture, contemporary shelf"}]
      [:meta {:name "description", :content "Modern shelving in Europe."}]

      ;; Google analytics.
      (g/analytics)
      
      ;; Plus One Button
      ;; TODO: Internationalize?
      ;; Note: The popup text is truncated in languages other than english.
      [:script {:type "text/javascript" :src "https://apis.google.com/js/plusone.js"}
       #_ "{lang: 'fr'}"]

      head]

     [:body
      ;; standard centered layout
      [:table {:cellpadding "0" :cellspacing "0" :width "900" :style {:margin "auto"}}

       ;; ----- TOP BORDER ROW -----
       [:tr
        [:td.greybox.greyborder_br {:width "225"}]

        ;; SITE MESSAGE AREA
        [:td.greyborder_br {:colspan "2" :width "450"}
         [:div
          {:style "padding: 10px; min-height: 19px; text-align: center; font-size: 20px; text-transform: uppercase;"}
          [:noscript {:style "color: #F00;"} (tr/translate :javascript_required)]
          [:span#under_construction {:style "display: none;"} (tr/translate :construction)]
          [:span#webgl_recommended {:style "display: none; font-size: 16px;"} (tr/translate :webgl_recommended)
           ;; Recommend firefox/chrome for non firefox/chrome users.
           (case (:browser_group (ua/req->features req/*req*))
             "Firefox" ""
             "Chrome" ""
             [:span"&nbsp;" (tr/translate :recommended_browsers)])]
          [:span#canvas_recommended {:style "display: none; font-size: 16px; color: #F00"}
           (tr/translate :canvas_required) "&nbsp;" (tr/translate :recommended_browsers)]
          ]]
        [:td {:width "225"}]]

       ;; ----- HEADER ROW -----
       [:tr {:style "height: 136px"}
        ;; locale menu
        [:td.greybox {:width "225"}
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

        #_ [:div.span6
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

        [:td {:width "225"}
         [:div#shortcuts.greyborder_b {:style "height: 135px"}
          [:ul {:style {:padding-top "28px" :margin-left "25px" :padding-left "0px"}}
           [:li 
            [:a { :href "#"} (tr/translate :header :signup)]]
           [:li 
            [:a { :href "#"} (tr/translate :header :contact)]]
           [:li 
            [:a { :href (tr/localize "/team.htm")} (tr/translate :header :team)]]
           [:li 
            [:a { :href (tr/localize "/careers.htm")} (tr/translate :header :careers)]]]]]

        [:td
         [:div#shortcuts.greyborder_br {:style "height: 135px"}
          [:ul {:style {:padding-top "28px" :margin-left "25px" :padding-left "0px"}}
           (cart-block)]]]

        [:td
         [:div.greyborder_b {:style "height: 135px"}
          [:a {:href (tr/localize "home.htm")}
           [:img {:src "/img/sistemi-moderni-systems.jpg", :width "206", :height "119" :alt "logo" :style "margin-left: 19px;"}]]]]]

       ;; ----- MENU AND CONTENT ROW -----
       ;; TODO: Find a way to not have to pass the height.
       [:tr
        ;; Menu
        [:td.greybox.greyborder_r {:width "225" :style {:height "100%;" :vertical-align "top"}}
         [:ul.menu.menum (menu)]
         ;; Hack since the sidebar parameter used to be height.
         (when-not (number? sidebar)
           [:div {:style {:clear "left" :width "225px" :border-top "1px solid #3B3B3B" :padding-top "20px"}}
            [:div {:style {:margin-left "32px"}}
             sidebar]])]

        ;; ----- CONTENT -----
        [:td {:colspan "3" :width "675"}
         body]]

       ;; ----- RED BAR -----
       [:tr
        [:td#redbar {:colspan "4"}
         [:a.first {:href "#"}
          [:img {:src "/img/facebook.jpg", :border "0" :alt "facebook"}]]
         [:a { :href "#"}
          [:img {:src "/img/twitter.jpg", :border "0" :alt "twitter"}]]
         ;; commenting this out for now for privacy
         ;; [:g:plusone {:size "small" :annotation "none"}]
         ]]

       ;; ----- ADDRESS AND COPYRIGHT -----
       [:tr
        [:td.greybox
         [:div#footer.greyborder_r
          [:div#address "SISTEMI MODERNI"
           [:br] "St. Martin d&rsquo;Uriage, France"
           [:br] "M. +33 6 09 46 92 00"]
          [:div#copyright (interpose [:br] (tr/translate :copyright))]]]]]]])))
