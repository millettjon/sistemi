(ns sistemi.site.shelf-htm
  (:require [util.string :as stru]
            [sistemi.form :as sf]
            [www.request :as r]
            [www.form :as f]
            [sistemi.translate :as tr])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {})

(def strings
  {:en {:title "Vision Of Modern Furniture : High Design, Resonably Priced, Sustainably."
        :spin "drag to spin"
        :width "Length"
        :depth "Depth"
        :height "Height"
        :finish "Finish"
        :color "Color"
        :quantity "Quantity"}
   :es {}

   :it {;:title "TODO: Italian"
        :spin "Clic e Tira"
        :width "Lunghezza"
        :depth "Profondità"
        :height "Altezza"
        :finish "Finitura"
        :color "Colore"
        :quantity "Quantità"}

   :fr {;:title "TODO: French"
        :spin "cliquez pour tourner"
        :width "Largeur"
        :depth "Profondeur"
        :height "Hauteur"
        :finish "Finition"
        :color "Couleur"
        :quantity "Quantité"}
   })

(defn head
  []
  (seq [;; chosen
        [:link {:rel "stylesheet" :href "chosen/chosen.css" :type "text/css"}]
        [:script {:type "text/javascript" :src "chosen/chosen.jquery.js"}]

        ;; customSelect
        [:script {:type "text/javascript" :src "jquery.customSelect/customSelect.jquery.js"}]
        [:style "span.customStyleSelectBox {height: 23px; line-height: 24px; width: 150px; background-color: #fff;
                                            color:#555; padding:0px 3px 0px 5px; border:1px solid #e7dab0;
                                            -moz-border-radius: 5px; -webkit-border-radius: 5px;
                                            border-radius: 5px 5px; }"]
        [:style ".customStyleSelectBoxInner {background:url(jquery.customSelect/arrow.png) no-repeat center right; }"]

        ;; color picker
        [:script {:type "text/javascript" :src "js/main.js"}]

        ;; 3d model
        [:script {:type "text/javascript" :src "/3d/Three.js"}]
        [:script {:type "text/javascript" :src "/3d/frame.js"}]
        [:script {:type "text/javascript" :src "/3d/shelf.js"}]

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
   [:div#design-box.span6  {:style "background-color: #000;"}
    [:div#model]

    [:div {:style "text-align: center;"} (tr/translate :spin)]]

   [:div.span3
    
    [:form#shelf-form.form-horizontal {:method "post" :action "cart/add"}
     (f/hidden :id)
     (f/hidden {:type :shelf})

     [:fieldset

      [:div.control-group
       [:label.control-label {:for "width"} (tr/translate :width)]
       [:div.controls
        (f/select :width {:class "chzn-select" :style "width: 160px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "depth"} (tr/translate :depth)]
       [:div.controls
        (f/select :depth {:class "chzn-select" :style "width: 160px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "finish"} (tr/translate :finish)]
       [:div.controls
        (f/select :finish {:class "customStyleSelectBox" :style "width: 165px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "color"} (tr/translate :color)]
       [:div.controls
        [:div {:style {:height "25px"}}
         [:div#color-swatch {:style {:width "25px" :height "100%" :background-color "blue" :display "inline-block" :border-radius "5px" :vertical-align "middle"}}]
         [:div#color-text {:style {:margin-left "10px" :display "inline-block" :height "100%" :vertical-align "middle" :padding-top "4px" :color "white"}}
          "RAL 8098"
          ]]]]

      ;; TODO: Pull request for hiccup to handle style as map.
      ;; TODO: Factor this out?
      [:div {:style {:position :relative
                     :height "195px"        ; manual height since children are absolute
                     :margin-left "25px"}}  ; manual margin to right align wheels with other fields
       [:div#wheel-ral {:style "position: absolute; top: 0px; left: 0px;"}]
       [:div#wheel-val {:style "position: absolute; top: 0px; left: 0px; visibility: hidden;"}]
       [:div#wheel-val-oiled {:style "position: absolute; top: 0px; left: 0px; visibility: hidden;"}]]


      [:div.control-group {:style "margin-top: 20px;"}
       [:label.control-label {:for "quantity"} (tr/translate :quantity)]
       [:div.controls
        (f/select :quantity {:class "chzn-select" :tabindex 1})
        [:div#numDisplayed {:style "text-align: center; visibility: hidden"} "(4 displayed)"]
        ]]

      [:div {:style "text-align: center"}
       [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} (if (= -1 (f/default :id)) (tr/translate :cart :add)
                                                                      (tr/translate :cart :update) )]]
      ] ]

    [:script {:type "text/javascript"}
     ;; Initialize shelf from defaults.
     "\n"
     "var shelf = {};\n"
     "shelf.width=" (f/default :width) ";\n"
     "shelf.depth=" (f/default :depth) ";\n"
     "shelf.color=parseInt('0x'+'" (f/default :color) "'.substring(1));\n"
     "shelf.quantity=" (f/default :quantity) ";\n"

     "jQuery(document).ready(function() {
         // Hookup the form controls.
         $('.chzn-select').chosen();
         $('.customStyleSelectBox').customSelect();

         // Hookup on change events to update the model.
         $('#width').chosen().change(function() {
           shelf.width = $(this).val();
           updateAnimation(shelf);
         });

         $('#depth').chosen().change(function() {
           shelf.depth = $(this).val();
           updateAnimation(shelf);
         });

         // Handler - When the finish is changed, update the color wheel.
         var lastRAL;
         var lastVAL;
         $('#finish').change(function() {
           var clr;
           var finish = $(this).val();
           $('#wheel-ral').css('visibility', 'hidden');
           $('#wheel-val').css('visibility', 'hidden');
           $('#wheel-val-oiled').css('visibility', 'hidden');
           switch (finish) {
             case ':laquer-matte':
             case ':laquer-satin':
               c = typeof lastRAL == 'undefined' ? defaultRAL : lastRAL;
               onColor(c);
               $('#wheel-ral').css('visibility', 'visible');
               break;
             case ':valchromat-raw':
               c = typeof lastVAL == 'undefined' ? defaultVALraw : lastVAL;
               console.log(c);
               c = color.valchromat.get_by_name_js('raw', c.name);
               onColor(c);
               $('#wheel-val').css('visibility', 'visible');
               break;
             case ':valchromat-oiled':
               c = typeof lastVAL == 'undefined' ? defaultVALoiled : lastVAL;
               c = color.valchromat.get_by_name_js('oiled', c.name);
               onColor(c);
               $('#wheel-val-oiled').css('visibility', 'visible');
           }

         });

         // --------------------
         // Setup the color picker.
         function onColor(color) {
           pack = function(rgb) {
             var r = rgb[0];
             var g = rgb[1];
             var b = rgb[2];
             return '#' + (r < 16 ? '0' : '') + r.toString(16) +
                          (g < 16 ? '0' : '') + g.toString(16) +
                          (b < 16 ? '0' : '') + b.toString(16);
           }

           // TODO: Update hidden form field w/ EDN value.
           var rgb;
           var text;
           var t = color.type;
           if (t == 'ral') {
             text = t.toUpperCase() + ' ' + color.code;
             rgb = color.rgb;
             lastRAL = color;
           }
           else {
             lastVAL = color;
             text = color.name;
             rgb = pack(color.rgb);
           }

           // Update text of color select box.
           $('#color-text').text(text);

           // Update background color of color swatch.
           // TODO: Use texture if available.
           $('#color-swatch').css('background-color', rgb);

           // Update the model.
           var hex = rgbHexToInt(rgb);
           if (shelf.color != hex) {
             shelf.color = hex;
             updateAnimation(shelf);
           }
         }

         var defaultRAL = color.ral.default_color;
         var defaultVALraw = color.valchromat.default_raw_color;
         var defaultVALoiled = color.valchromat.default_oiled_color;

         var callback = onColor;
         color.ral_picker.init('#wheel-ral', color.ral.palette, callback);
         color.val_picker.init('#wheel-val', 'raw', callback);
         color.val_picker.init('#wheel-val-oiled', 'oiled', callback);
         // --------------------

         // Toggle background button handler.
         $('#toggle-background').click(function(e) {
           var box = $('#design-box');
           var bg = box.css('background-color');
           console.log(bg);
           if (bg === 'rgb(255, 255, 255)') {
             box.css('background-color', '#000');
           }
           else {
             box.css('background-color', '#FFF');
           }
         });

         // Set the container div height to match the width.
         var model = $('#model');
         var height = model.width();
         model.css({height: height.toString() + 'px'});

         // Start animating.
         drawShelf(shelf, model[0]);
         onColor(defaultRAL);
         startAnimation();
     });"

     ]]
   ])

(defn handle
  [req]
  (response (standard-page (head) (f/with-form (:shelf sf/cart-items) (:params req) (body)) 544)))