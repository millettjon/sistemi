(ns sistemi.site.bookcase-htm
  (:require [util.string :as stru]
            [sistemi.form :as sf]
            [www.request :as r]
            [www.form :as f]
            [sistemi.translate :as tr])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {:es "estantaría"
   :fr "etagères"})

(def strings
  {:en {;;:title "Vision Of Modern Furniture : High Design, Resonably Priced, Sustainably."
        :spin "drag to spin"
        :cutout "Cutout"
        :width "Length"
        :depth "Depth"
        :height "Height"
        :finish "Finish"
        :color "Color"
        :quantity "Quantity"
        :background-color "background"}
   :es {}
   :it {;; :title ""
        ;; :spin "Per ruotare l'oggetto,‭ ‬muovere il mouse mantenendone premuto il pulsante"
        :spin "Clic e Tira"
        :cutout "Ritaglio"
        :width "Lunghezza"
        :depth "Profondità"
        :height "Altezza"
        :finish "Finitura"
        :color "Colore"
        :quantity "Quantità"
        :background-color "sfondo"}
   :fr {
        :spin "Cliquez pour pivoter"
        :width "Largeur"
        :depth "Profondeur"
        :height "Hauteur"
        :finish "Finition"
        :color "Couleur"
        :cutout "Découpe"
        :quantity "Quantité"
        :background-color "l’arrière-plan"}

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
        [:script {:type "text/javascript" :src "/3d/bookcase.js"}]

        ;; styles
        [:style "#shelf-form {margin-left: 10px; margin-top: 10px;}"]

        [:style "#shelf-form .control-group {margin-bottom: 12px}"]
        [:style "#shelf-form .control-label {width: 45px; color: #FFF; padding-top: 4px}"]
        [:style "#shelf-form .controls {margin-left: 55px;}"]

        [:style "#shelf-form .chzn-select {width: 160px;}"]
        [:style "#shelf-form input {border-radius: 5px;}"]
        [:style "#shelf-form select {height: 23px; line-height: 24px;}"]

        [:style "#shelf-form #color {width: 90px;}"]
        [:style "#shelf-form #submit {margin-top: 31px;}"]
        ]))

(defn body
  []
  [:table {:cellspacing "0" :cellpadding "0" :width "675"}
   [:tr
    [:td#design-box {:style {:vertical-align "top" :background-color "#000"}}
    ;; 3d model
    [:div#model {:style {:width "450px"}}]

    ;; Toolbar
    ;; Note: This must be position: relative to allow the spin text to be positioned relative to it.
    [:div {:style "height: 50px; position: relative; margin-top: 20px;"}

     ;; Note: This must be positioned with z-index: 1 to be over the
     ;; spin div.
     [:button#toggle-background.btn.btn-inverse {:style "margin-left: 20px; position: relative; z-index: 1; outline: none;"}
      [:i.icon-white.icon-adjust {:style "margin-right: 10px;"}] (tr/translate :background-color)]

     [:div {:style "position: absolute; top: 5px; width: 100%; text-align: right; z-index: 0;"}
      [:span {:style "margin-right: 20px;"} (tr/translate :spin)]]

     ]]

   [:td {:width "225" :style {:height "225px"}}

    [:form#shelf-form.form-horizontal {:method "get" :action "cart/add"}
     (f/hidden :id)
     (f/hidden :quantity)
     (f/hidden {:type :bookcase})
     (f/hidden :color)

     [:fieldset

      [:div.control-group
       [:label.control-label {:for "width"} (tr/translate :width)]
       [:div.controls
        (f/select :width {:class "chzn-select" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "height"} (tr/translate :height)]
       [:div.controls
        (f/select :height {:class "chzn-select" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "depth"} (tr/translate :depth)]
       [:div.controls
        (f/select :depth {:class "chzn-select" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "cutout"} (tr/translate :cutout)]
       [:div.controls
        (f/select :cutout {:class "customStyleSelectBox" :style "width: 165px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "finish"} (tr/translate :finish)]
       [:div.controls
        (f/select :finish {:class "customStyleSelectBox" :style "width: 165px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "color"} (tr/translate :color)]
       [:div.controls
        [:div {:style {:height "25px"}}
         [:div#color-swatch {:style {:width "25px" :height "100%" :display "inline-block" :border-radius "5px" :vertical-align "middle"}}]
         [:div#color-text {:style {:margin-left "10px" :display "inline-block" :height "100%" :vertical-align "middle"
                                   :padding-top "4px" :color "white"}}]]]]

      ;; TODO: Factor this out?
      [:div {:style {:position :relative
                     :height "195px"        ; manual height since children are absolute
                     :margin-left "25px"}}  ; manual margin to right align wheels with other fields
       [:div#wheel-ral {:style "position: absolute; top: 0px; left: 0px;"}]
       [:div#wheel-val {:style "position: absolute; top: 0px; left: 0px; visibility: hidden;"}]
       [:div#wheel-val-oiled {:style "position: absolute; top: 0px; left: 0px; visibility: hidden;"}]]

      [:div {:style "text-align: right"}
       [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} (if (= -1 (f/default :id)) (tr/translate :cart :add)
                                                                        (tr/translate :cart :update))]]]]

    [:script {:type "text/javascript"}
     ;; Initialize bookcase from defaults.
     "\n"
     "var bookcase = {};\n"
     "bookcase.height=" (f/default :height) ";\n"
     "bookcase.width=" (f/default :width) ";\n"
     "bookcase.depth=" (f/default :depth) ";\n"
     "bookcase.cutout='" (str (f/default :cutout)) "';\n"

     "jQuery(document).ready(function() {
         // Hookup the form controls.
         $('.chzn-select').chosen();
         $('.customStyleSelectBox').customSelect();

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

         // Hookup on change events to update the model.
         $('#width').chosen().change(function() {
           bookcase.width = $(this).val();
           updateAnimation(bookcase);
         });
         $('#height').chosen().change(function() {
           bookcase.height = $(this).val();
           updateAnimation(bookcase);
         });
         $('#depth').chosen().change(function() {
           bookcase.depth = $(this).val();
           updateAnimation(bookcase);
         });
         $('#cutout').change(function() {
           bookcase.cutout = $(this).val();
           updateAnimation(bookcase);
         });

         // --------------------
         // Setup the color picker.
         function onColor(color) {
           // TODO: Update hidden form field w/ EDN value.
           var text;
           var rgb = color.rgb;
           var t = color.type;
           if (t == 'ral') {
             text = t.toUpperCase() + ' ' + color.code;
             lastRAL = color;
           }
           else {
             lastVAL = color;
             text = color.name;
           }

           // Update text of color select box.
           $('#color-text').text(text);

           // Update background color of color swatch.
           // TODO: Use texture if available.
           $('#color-swatch').css('background-color', rgb);

           // Update the model.
           var hex = rgbHexToInt(rgb);
           if (bookcase.color != hex) {
             bookcase.color = hex;
             updateAnimation(bookcase);
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
         drawBookcase(bookcase, model[0]);
         onColor(defaultRAL);
         startAnimation();
     });"
     ]]]
   ])

(defn handle
  [req]
  (response (standard-page (head) (f/with-form (:bookcase sf/cart-items) (:params req) (body)) 520)))
