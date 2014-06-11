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
            [analytics.google :as g]
            [www.event :as e]
            [sistemi.order :as order]
            [util.net :as net]
            [util.calendar :as cal]
            [app.config :as c])
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
  (let [cur-page (str "/" (path/first (:uri req/*req*)))]
    (for [item menu-data]
      (if (vector? item)
        ;; submenu
        (let [key (first item)
              label (tr/translate :menu key)]
          [:li.menui [:a.menui {:href "#" :tabindex "-1" :style {:height "20px"}} [:span label]]
           [:ul.menum.submenu
            (for [item (rest item)]
              (let [page (if (map? item) (:page item)  (str "/" (name item) ".htm"))
                    label (tr/translate :menu key (if (map? item) (:label item) item))]
                [:li.menui
                 [(keyword (str "a" (if (= page cur-page) "#current_item" "") ".menui")) {:href (tr/localize page) :tabindex "-1" :style {:height "20px"}} label]]
                ))]])
        ;; regular item
        (let [page (if (= item :home)
                     "/"
                     (str "/" (name item) ".htm"))
              label (tr/translate :menu item)]
          [:li.menui
           [(keyword (str "a" (if (= page cur-page) "#current_item" "") ".menui")) {:href (tr/localize page) :tabindex "-1" :style {:height "20px"}} label]]
          )))))

(defn cart-block
  []
  (let [cart (-> req/*req* cart/get)]
    (if (cart/empty? cart)
      [:li [:i.fa.fa-shopping-cart.fa-lg.fa-fw] " (0)"]
      [:li 
       [:a {:href (tr/localize "/cart.htm") :tabindex "-1"}
        [:i.fa.fa-shopping-cart.fa-lg.fa-fw] " (" (order/total-items cart) ") "
        (-> cart :price :total fmt/eur-short)
        [:br]
        [:i.fa.fa-truck.fa-lg.fa-fw {:style {:margin-top "10px"}}] " " (-> (order/delivery-date) cal/format-date-dMMM)

        #_ (tr/translate :cart)]])))

(defn doctype-html5
 [html]
 (str "<!doctype html>" html))

;; Note: Social icon colors don't work well in the red footer bar so leaving black and white for now.
;; Share link generator: http://www.sharelinkgenerator.com/
(defn social
  []
  [:span {:style {:letter-spacing "5px" :color "#bbb"}}

   ;; http://business.pinterest.com/widget-builder/#do_pin_it_button
   [:a.social {:onclick "window.open(this.href,'_blank'); return false;"
               :href "//www.pinterest.com/sistemimoderni"
               :tabindex "-1"}
    [:i.fa.fa-pinterest.fa-lg.social]]

   [:a.social {:onclick "window.open(this.href,'_blank'); return false;"
               :href "//www.facebook.com/sistemimoderni.france"
               :tabindex "-1"}
    [:i.fa.fa-facebook.fa-lg.social]]

   [:a.social {:onclick "window.open(this.href,'_blank'); return false;"
               :href "//twitter.com/SistemiModerni"
               :tabindex "-1"}
    [:i.fa.fa-twitter.fa-lg]]
   ])

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

      ;; social buttons styles
      [:style "a.social {color: #bbb;} a.social:hover {color: #fff;}"]

      (e/script req/*req*)

      [:script {:src "/js/jquery-1.7.1.min.js" :type "text/javascript"}]

      [:script {:src "/bootstrap/js/bootstrap.js", :type "text/javascript"}]
      [:script {:type "text/javascript" :src "/3d/detector.js"}]

      [:link {:href "/fonts/stylesheet.css", :rel "stylesheet", :type "text/css"}]
      [:meta {:name "keywords", :content "modern furniture, modern shelves, shelving, shelf, book case, mod furniture, contemporary shelf"}]
      [:meta {:name "description", :content "Modern shelving in Europe."}]

      head

      (g/analytics (c/conf :google :analytics))]

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
          [:span#header_message {:style {:display "none"}} (tr/translate :beta)]
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
                      [:a.select {:href "#" :tabindex "-1"} locale]
                      [:a {:href (tr/localize "/profile/locale" :query {:lang locale}) :tabindex "-1"} locale])))
             (interpose [:span.line "|"]))]

           [:img {:src "/img/block-logo.gif" :alt "logo" :style "margin-bottom: 7px;"}]]]]

        [:td {:width "225"}
         [:div#shortcuts.greyborder_b {:style "height: 135px"}
          [:ul {:style {:padding-top "28px" :margin-left "25px" :padding-left "0px"}}
           [:li 
            [:a {:href "#" :tabindex "-1"} (tr/translate :header :signup)]]
           [:li 
            [:a {:href (tr/localize "/contact.htm") :tabindex "-1"} (tr/translate :header :contact)]]
           [:li 
            [:a {:href (tr/localize "/team.htm") :tabindex "-1"} (tr/translate :header :team)]]
           [:li 
            [:a {:href (tr/localize "/careers.htm") :tabindex "-1"} (tr/translate :header :careers)]]]]]

        [:td
         [:div#shortcuts.greyborder_br {:style "height: 135px"}
          [:ul {:style {:padding-top "28px" :margin-left "25px" :padding-left "0px"}}
           (cart-block)]]]

        [:td
         [:div.greyborder_b {:style "height: 135px"}
          [:a {:href (tr/localize "/" #_"home.htm") :tabindex "-1"}
           [:img {:src "/img/sistemi-moderni-systems.jpg", :width "206", :height "119" :alt "logo" :style "margin-left: 19px;"}]]]]]

       ;; ----- MENU AND CONTENT ROW -----
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
        [:td {:colspan "3" :width "675" :style {:vertical-align "top" :text-align "justify"}}
         body]]

       ;; ----- RED BAR -----
       [:tr
        [:td#redbar {:colspan "4" :style {:padding-left "30px"}}
         (social)
         ]]

       ;; ----- ADDRESS AND COPYRIGHT -----
       [:tr
        [:td.greybox
         [:div#footer.greyborder_r
          [:div#address "SISTEMI MODERNI"
           [:br] "St. Martin d&rsquo;Uriage, France"
           [:br] "M. +33 6 09 46 92 00"]
          [:div#copyright (interpose [:br] (tr/translate :copyright))]]]]]]])))

(defn doctype-xhtml-strict
 [html]
 (str "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
      html))

;;
;; Ref: http://htmlemailboilerplate.com/
;; 
(defn email-page
  [body]
  (doctype-xhtml-strict
   (hcp/html
    [:html {:xmlns "http://www.w3.org/1999/xhtml"
            :lang (req/*req* :locale)}
     [:head
      [:meta {:http-equiv "Content-Type", :content "text/html; charset=utf-8"}]
      [:meta {:name "viewport", :content "width=device-width, initial-scale=1.0"}]
      [:title  (tr/translate :title)]

      [:style {:type "text/css"}
       (slurp "www/raw/css/email.css")]]

     [:body
      ;; Outer table to set the background (100% width).
      [:table#backgroundTable {:style {:cellpadding "0" :cellspacing "0" :border "0"}}
       [:tr
        [:td {:valign "top"}
         
         ;; Inner table to force width to 600px.
         [:table#bodyTable {:style {:width "600px"}}
          [:tr
           [:td body]]]]]]]])))
