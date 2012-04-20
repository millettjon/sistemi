(ns sistemi.site.shelving-htm
  (:require [util.string :as stru])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {:es "estantaría"
   :fr "etagères"})

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

(defn shelf-range
  [min max]
  (apply str (interpose "," (map #(str "'" % "'") (range min (inc max))))))

(defn dimension-options
  [min max]
  (map
   (fn [i] [:option {:value i} (str i " cm")])
   (range min (inc max))))

(defn head
  []
  (seq [;; combo boxes
        [:script {:type "text/javascript" :src "bootstrap/js/bootstrap-combobox.js"}]
        [:link {:rel "stylesheet" :href "bootstrap/css/bootstrap-combobox.css" :type "text/css"}]

        ;; color picker
        [:script {:type "text/javascript" :src "farbtastic/farbtastic.js"}]
        [:link {:rel "stylesheet" :href "farbtastic/farbtastic.css" :type "text/css"}]

        ;; styles
        [:style "#shelf-form {margin-left: 10px; margin-top: 10px;}"]

        #_[:style "#shelf-form .control-label {width: 60px; color: #FFF;}"]
        #_[:style "#shelf-form .controls {margin-left: 70px;}"]
        [:style "#shelf-form .control-label {width: 100px; color: #FFF;}"]
        [:style "#shelf-form .controls {margin-left: 115px;}"]

        [:style "#shelf-form input, #shelf-form select {width: 60px;}"]
        [:style "#shelf-form #color {width: 87px;}"]
        [:style ".dropdown-menu {min-width: 95px;}"]
        [:style "#shelf-form #submit {margin-top: 20px;}"]
        #_[:style ".combobox-container:after {clear: none;}"]
]))

(defn body
  []
  [:div.row
   [:div.span6
    [:img {:src "/img/0600.0600.0200.0018.S.427.png"}]]

   [:div.span3
    
    [:form#shelf-form.form-horizontal {:method "get" :action "order/review.htm"}
     [:fieldset

      [:div.control-group
       [:label.control-label {:for "width"} "Width"]
       [:div.controls
        [:select#width.combobox {:name "width"}
         [:option {:value ""} ""]
         (dimension-options 60 240)]]]

      [:div.control-group
       [:label.control-label {:for "height"} "Height"]
       [:div.controls
        [:select#height.combobox {:name "height"}
         [:option {:value ""} ""]
         (dimension-options 60 240)]]]

      [:div.control-group
       [:label.control-label {:for "depth"} "Depth"]
       [:div.controls
        [:select#depth.combobox {:name "depth"}
         [:option {:value ""} ""]
         (dimension-options 20 39)]]]

      [:div.control-group
       [:label.control-label {:for "cutout"} "Cutout"]
       [:div.controls
        [:select#cutout.combobox {:name "cutout"}
         [:option ""]
         #_[:option "none"]
         [:option "oval"]
         [:option "rectangle"]
         [:option "heart"]]]]

      [:div.control-group
       [:label.control-label {:for "color"} "Color"]
       [:div.controls
        [:input#color {:type "text" :value "#AB003B" :name "color"}]]]
      [:div#colorpicker {:style "margin-left: 20px;"}]

      [:div {:style "text-align: right"}
       [:button#submit.btn.btn-inverse {:type "submit"} "Submit"]]]]

    #_[:form {:style "margin-left: 10px; margin-top: 10px;"}
     [:label {:style "color: #FFF;"} "Height"]
     [:div.input-append
      [:input#height {:type "text" :data-provide "typeahead" :placeholder "(60-240)" :style "width: 50px;"}]
      [:span.add-on {:style "vertical-align: top;"} "cm"]]

     ;; [:label {:style "color: #FFF;"} "Width"]
     ;; [:div.input-append
     ;;  [:input#width {:type "text" :data-provide "typeahead" :placeholder "(60-240)" :style "width: 50px;"}]
     ;;  [:span.add-on {:style "vertical-align: top;"} "cm"]]

     [:style "input {width: 75px;}"]
     [:label {:style "color: #FFF;"} "Width"]
     [:select.combobox {:style "width: 75px;"}
      [:option {:value "60" :style "width: 75px;"} "60 cm"]
      ]

     [:label {:style "color: #FFF;"} "Depth"]
     [:div.input-append
      [:input#depth {:type "text" :data-provide "typeahead" :placeholder "(20-39)" :style "width: 50px;"}]
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
         //var heightList = [" (shelf-range 60 240) "];
         //var depthList = [" (shelf-range 20 39) "];
         //$('#height').typeahead({source: heightList, items:5});
         //$('#width').typeahead({source: heightList, items:5});
         //$('#depth').typeahead({source: depthList, items:5});
         $('#colorpicker').farbtastic('#color');
         $('.combobox').combobox();         
     });"
     ]]
   ])

(defn handle
  [req]
  (response (standard-page (head) (body) 544)))

(sistemi.registry/register)
