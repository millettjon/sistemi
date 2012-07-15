(ns sistemi.site.shelving-htm
  (:require [util.string :as stru]
            [sistemi.form :as sf]
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

        ;; 3d model
        [:script {:type "text/javascript" :src "/3d/Three.js"}]
        [:script {:type "text/javascript" :src "/3d/shelving.js"}]

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
    [:div#model]
    #_[:img {:src "/img/0600.0600.0200.0018.S.427.png"}]]

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
     "\n"
     "var shelving = {};\n"
     "shelving.height=" (f/default :height) ";\n"
     "shelving.width=" (f/default :width) ";\n"
     "shelving.depth=" (f/default :depth) ";\n"
     "shelving.cutout='" (f/default :cutout) "';\n"
     "shelving.color=parseInt('0x'+'" (f/default :color) "'.substring(1));\n"

     "jQuery(document).ready(function() {
         // Hookup the form controls.
         $('#colorpicker').farbtastic('#color');
         $('.chzn-select').chosen();
         $('.customStyleSelectBox').customSelect();

         // Hookup on change events to update the model.
         $('#width').chosen().change(function() {
           shelving.width = $(this).val();
           updateAnimation(shelving);
         });
         $('#height').chosen().change(function() {
           shelving.height = $(this).val();
           updateAnimation(shelving);
         });
         $('#depth').chosen().change(function() {
           shelving.depth = $(this).val();
           updateAnimation(shelving);
         });
         $('#cutout').change(function() {
           shelving.cutout = $(this).val();
           updateAnimation(shelving);
         });

         // Set the container div height to match the width.
         var model = $('#model');
         var height = model.width();
         model.css({height: height.toString() + 'px'});

         // Define the shelving unit.
         // var shelving = {height: 200, width: 150, depth: 50, color: 0xab003b, cutout: 'quaddro'};
         drawShelving(shelving, model[0]);
         startAnimation();

         function checkColor() {
           var color = $('#color').css('background-color');
           color = rgb2hex(color);
           if (shelving.color != color) {
             shelving.color = color;
             var bg = luminence(color) > 0.1 ? '#000' : '#666';
             $('#model').css({'background-color': bg});
             updateAnimation(shelving);
           }
           // Rate limit model updates since color changes spews events rapidly
           // and can cause slowness and/or webgl crashes.
           setTimeout(function() {requestAnimationFrame(checkColor);}, 1000);
         }
         checkColor();
     });"
     ]]
   ])

(defn handle
  [req]
  (response (standard-page (head) (f/with-form sf/shelving (:params req) (body)) 544)))

#_ (remove-ns 'sistemi.site.shelving-htm)
(sistemi.registry/register)
