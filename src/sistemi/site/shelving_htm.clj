(ns sistemi.site.shelving-htm
  (:require [util.string :as stru]
            [www.request :as r]
            [www.form :as f])
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
        (f/select :width {:class "chzn-select" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "height"} "Height"]
       [:div.controls
        (f/select :height {:class "chzn-select" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "depth"} "Depth"]
       [:div.controls
        (f/select :depth {:class "chzn-select" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "cutout"} "Cutout"]
       [:div.controls
        (f/select :cutout {:class "customStyleSelectBox" :style "width: 100px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "finish"} "Finish"]
       [:div.controls
        (f/select :finish {:class "customStyleSelectBox" :style "width: 100px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "color"} "Color"]
       [:div.controls
        (f/text :color {:tabindex 1})]]
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

(def fields
  {:width {:type :bounded-number :units "cm" :min 60 :max 240 :default 120}
   :height {:type :bounded-number :units "cm" :min 60 :max 240 :default 120}
   :depth {:type :bounded-number :units "cm" :min 20 :max 39 :default 30}
   :cutout {:type :set :options ["semplice" "ovale" "quadro"] :default "semplice"}
   :finish {:type :set :options ["matte" "satin" {:disabled true} "glossy" {:disabled true}] :default "matte"}
   :color {:type :rgb :default "#AB003B"}
   })

(defn handle
  [req]
  (response (standard-page (head) (f/with-form fields (:params req) (body)) 544)))

#_ (remove-ns 'sistemi.site.shelving-htm)
(sistemi.registry/register)
