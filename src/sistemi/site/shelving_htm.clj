(ns sistemi.site.shelving-htm
  (:require [util.string :as stru])
  (:use [ring.util.response :only (response)]
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
  ([min max] (dimension-options min max nil))
  ([min max selected]
     (map
      (fn [i]
        (let [attrs {:value i}
              attrs (if (= selected i)
                     (assoc attrs :selected "true")
                     attrs)]
          [:option attrs (str i " cm")]))
      (range min (inc max)))))

(defn head
  []
  (seq [;; chosen
        [:link {:rel "stylesheet" :href "chosen/chosen.css" :type "text/css"}]
        [:script {:type "text/javascript" :src "chosen/chosen.jquery.js"}]

        ;; customSelect
        [:script {:type "text/javascript" :src "jquery.customSelect/customSelect.jquery.js"}]
        [:style "span.customStyleSelectBox {height: 23px; line-height: 24px; width: 88px; background-color: #fff; color:#555; padding:0px 3px 0px 7px; border:1px solid #e7dab0; -moz-border-radius: 5px; -webkit-border-radius: 5px;border-radius: 5px 5px; }"]
        [:style ".customStyleSelectBoxInner {background:url(jquery.customSelect/arrow.png) no-repeat center right; }"]

        ;; color picker
        [:script {:type "text/javascript" :src "farbtastic/farbtastic.js"}]
        [:link {:rel "stylesheet" :href "farbtastic/farbtastic.css" :type "text/css"}]

        ;; styles
        [:style "#shelf-form {margin-left: 10px; margin-top: 10px;}"]

        [:style "#shelf-form .control-group {margin-bottom: 12px}"]
        [:style "#shelf-form .control-label {width: 100px; color: #FFF; padding-top: 4px}"]
        [:style "#shelf-form .controls {margin-left: 115px;}"]

        [:style "#shelf-form .chzn-select {width: 100px;}"]
        [:style "#shelf-form input {border-radius: 5px;}"]
        [:style "#shelf-form select {height: 23px; line-height: 24px;}"]

        [:style "#shelf-form #color {width: 90px;}"]
        [:style "#shelf-form #submit {margin-top: 20px;}"]
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
        [:select#width.chzn-select {:name "width" :tabindex 1}
         (dimension-options 60 240 120)]]]

      [:div.control-group
       [:label.control-label {:for "height"} "Height"]
       [:div.controls
        [:select#height.chzn-select {:name "height" :tabindex 1}
         (dimension-options 60 240 120)]]]

      [:div.control-group
       [:label.control-label {:for "depth"} "Depth"]
       [:div.controls
        [:select#depth.chzn-select {:name "depth" :tabindex 1}
         (dimension-options 20 39 30)]]]

      [:div.control-group
       [:label.control-label {:for "cutout"} "Cutout"]
       [:div.controls
        [:select#cutout.customStyleSelectBox {:name "cutout" :style "width: 100px" :tabindex 1}
         [:option {:selected "true"} "semplice"]
         [:option "ovale"]
         [:option "quadro"]]]]

      [:div.control-group
       [:label.control-label {:for "finish"} "Finish"]
       [:div.controls
        [:select#finish.customStyleSelectBox {:name "finish" :style "width: 100px" :tabindex 1}
         [:option {:selected true} "matte"]
         [:option {:disabled true} "satin"]
         [:option {:disabled true} "glossy"]]]]

      [:div.control-group
       [:label.control-label {:for "color"} "Color"]
       [:div.controls
        [:input#color {:type "text" :value "#AB003B" :name "color" :tabindex 1}]]]
      [:div#colorpicker {:style "margin-left: 20px;"}]

      [:div {:style "text-align: right"}
       [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} "Submit"]]]]

    [:script {:type "text/javascript"}
     "jQuery(document).ready(function() {
         $('#colorpicker').farbtastic('#color');
         $('.chzn-select').chosen();
         $('.customStyleSelectBox').customSelect();
     });"
     ]]
   ])

(defn handle
  [req]
  (response (standard-page (head) (body) 544)))

(sistemi.registry/register)
