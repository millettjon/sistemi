(ns www.wizard
  (:require [util.seq :as seq]))

(defn- wrap-a
  "Wraps a completed item in an anchor element."
  [completed? opts args]
  (if completed?
    [:a opts args]
    args))

(defn wizard
  [steps selected]
  (let [selected-index (seq/index-of (map first steps) selected)]
    #_ (prn "SELECTED-INDEX" selected-index)
    [:table.wizard {:style {:width "100%"
                            :table-layout "fixed"   ; make colums equal width
                            :margin-bottom "20px"
                            }}
     [:tr
      (->> steps
           (map-indexed
            (fn [index [name & args]]
              (let [[opts args] (if (map? (first args))
                                  [(first args) (rest args)]
                                  [{} args])
                    completed? (< index selected-index)]
                [:td
                 (wrap-a completed? opts 
                         [:div {:style {:text-align "center"}}
                          
                          [:div {:style {:text-alignz "center"}}
                           args]

                          ;; extra div required since position relative doesn't work in a td
                          [:div {:style {:position "relative"
                                         :text-alignz "center"}}

                           [;; Select the bullet type.
                            (cond completed? :i.fa.fa-check-circle.fa-lg ; completed step   -> circle w/ check
                                  (= index selected-index) :i.fa.fa-dot-circle-o.fa-2x ; current step     -> large circle w/ dot
                                  :default :i.fa.fa-circle-o.fa-lg) ; uncompleted step -> empty circle
                            
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
                           ]])]))))]]))
