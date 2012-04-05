(ns sistemi.site.shelving-htm
  (:require [util.string :as stru])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {:es "visíon"
   :fr "vision"})

(def strings
  {:en {:title "Vision Of Modern Furniture : High Design, Resonably Priced, Sustainably."
        :shelving {:title "OUR VISION"
                   :text (stru/join-lines "<span class=\"company_name\">SistemiModerni</span> is founded to
                       bring you high design and high quality for a reasonable price. We believe that
                       the world must stop creating low quality disposable goods. We also believe that
                       you can and should have things your way. That is why we empower you to
                       personalize all of our offerings.")}}
   :es {}
   :fr {}})

(defn head
  []
  (seq [[:script {:type "text/javascript" :src "farbtastic/farbtastic.js"}]
        [:link {:rel "stylesheet" :href "farbtastic/farbtastic.css" :type "text/css"}]]))

(defn body
  []
  [:div.row
   [:div.span6
    [:img {:src "/img/0600.0600.0200.0018.S.427.png"}]]

   [:div.span3
    [:form {:style "margin-left: 10px; margin-top: 10px;"}
     [:label {:style "color: #FFF;"} "Length"]
     [:div.input-append
      [:input#length {:type "text" :data-provide "typeahead" :placeholder "(60-240)" :style "width: 50px;"}]
      [:span.add-on {:style "vertical-align: top;"} "cm"]]

     [:label {:style "color: #FFF;"} "Height"]
     [:div.input-append
      [:input#height {:type "text" :data-provide "typeahead" :placeholder "(60-240)" :style "width: 50px;"}]
      [:span.add-on {:style "vertical-align: top;"} "cm"]]

     [:label {:style "color: #FFF;"} "Width"]
     [:div.input-append
      [:input#width {:type "text" :data-provide "typeahead" :placeholder "(60-240)" :style "width: 50px;"}]
      [:span.add-on {:style "vertical-align: top;"} "cm"]]

     [:label {:style "color: #FFF;"} "Cutout"]
     [:select {:style "width: 100px;"}
      [:option "none"]
      [:option "oval"]
      [:option "rectangle"]
      [:option "heart"]]

     [:label {:style "color: #FFF;"} "Color"]
     [:input#color {:type "text" :style "width: 100px;" :value "#ffffff"}]
     [:div#colorpicker]

     ]

    [:script {:type "text/javascript"}
     "jQuery(document).ready(function() {
         var lengthList = [" (apply str (interpose "," (map #(str "'" % "'") (range 60 241)))) "];
            $('#length').typeahead({source: lengthList, items:5});
            $('#height').typeahead({source: lengthList, items:5});
            $('#width').typeahead({source: lengthList, items:5});
            $('#colorpicker').farbtastic('#color');
        });"
     ]]
   ])

(defn handle
  [req]
  (response (standard-page (head) (body) 544)))

(sistemi.registry/register)
