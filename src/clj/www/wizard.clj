(ns www.wizard
  (:require [util.seq :as seq]))

(defn wizard
  [steps selected]
  (let [selected-index (seq/index-of (map first steps) selected)]
    (prn "SELECTED-INDEX" selected-index)
    [:table {:style {:width "100%"
                     :table-layout "fixed"   ; make colums equal width
                     :margin-bottom "20px"
                     }}
     [:tr
      (->> steps
           (map-indexed
            (fn [index [name & label]]
              [:td
               [:div {:style {:text-align "center"}} label]

               ;; extra div required since position relative doesn't work in a td
               [:div {:style {:position "relative"
                              :text-align "center"}}

                [;; Select the bullet type.
                 (cond (< index selected-index) :i.fa.fa-check-circle.fa-lg ; completed step   -> circle w/ check
                       (= index selected-index) :i.fa.fa-dot-circle-o.fa-2x ; current step     -> large circle w/ dot
                       :default :i.fa.fa-circle-o.fa-lg)                    ; uncompleted step -> empty circle
                  
                  {:style {:position "relative" ; position relative needed for z-index to work
                           :z-index "2"
                           :background-color "#000" ; needed to blank out edge of connecting line
                           }}]
                ;; Add center line.
                ;; Note the 2% overlap to cover a gap between table cells. Not sure why that happens.
                (cond (= 0 index)
                      [:div {:style {:position "absolute" :top "50%" :left "50%" :height "2px" :width "52%" :background-color "#999" :z-index "1"}}]

                      (= (-> steps count dec) index)
                      [:div {:style {:position "absolute" :top "50%" :height "2px" :width "50%" :background-color "#999" :z-index "1"}}]

                      :default
                      [:div {:style {:position "absolute" :top "50%" :height "2px" :width "102%" :background-color "#999" :z-index "1"}}]
                      )
                ]])))]]))
