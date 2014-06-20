(ns sistemi.design
  "Shared UI elements for bookcase and shelf design pages."
  (require [sistemi.translate :as tr]))

(defn toolbar
  []
  ;; Note: This must be position: relative to allow the spin text to be positioned relative to it.
  [:div {:style "height: 50px; position: relative; margin-top: 20px;"}

   ;; Note: This must be positioned with z-index: 1 to be over the
   ;; spin div.
   [:button#toggle-background.btn.btn-inverse {:style "margin-left: 20px; position: relative; z-index: 1; outline: none;"}
    [:i.icon-white.icon-adjust {:style "margin-right: 10px;"}] (tr/translate :design :toggle-background)]

   [:div {:style "position: absolute; top: 5px; width: 100%; text-align: center; z-index: 0;"}
    [:span {:style "margin-right: 20px;"} (tr/translate :spin)]]])
