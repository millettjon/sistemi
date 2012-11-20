(ns sistemi.site.shelving-htm
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
  {:en {:title "Vision Of Modern Furniture : High Design, Resonably Priced, Sustainably."
        :spin "drag to spin"
        :cutout "Cutout"
        :width "Length"
        :depth "Depth"
        :height "Height"
        :finish "Finish"
        :color "Color"
        :quantity "Quantity"
        :cart_add "Add to cart"}
   :es {}
   :fr {;:title ""
        :spin "cliquez pour tourner"
        :width "Longeur"
        :depth "Profondeur"
        :height "Hauteur"
        :finish "Finition"
        :color "Couleur"
        :quantity "Quantité"
        :cart_add "Ajouter au panier"
        }

   })

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
        [:script {:type "text/javascript" :src "/3d/frame.js"}]
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
    ;; ? what should display if canvas is not available?
    #_[:img {:src "/img/0600.0600.0200.0018.S.427.png"}]
    ;; TODO: Remove this for static rendering. Use rotation icons?
    [:div {:style "text-align: center;"} (tr/translate :spin)]]

   [:div.span3
    
    [:form#shelf-form.form-horizontal {:method "get" :action "cart/add"}
     (f/hidden :id)
     (f/hidden :quantity)
     (f/hidden {:type :shelving})

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
        (f/select :cutout {:class "customStyleSelectBox" :style "width: 100px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "finish"} (tr/translate :finish)]
       [:div.controls
        (f/select :finish {:class "customStyleSelectBox" :style "width: 100px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "color"} (tr/translate :color)]
       [:div.controls
        (f/text :color {:tabindex 1})]]
      [:div#colorpicker {:style "margin-left: 20px;"}]

      [:div {:style "text-align: right"}
       [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} (if (= -1 (f/default :id)) (tr/translate :cart_add)
                                                                      (tr/translate :cart_update))]]]]

    [:script {:type "text/javascript"}
     ;; Initialize shelving from defaults.
     "\n"
     "var shelving = {};\n"
     "shelving.height=" (f/default :height) ";\n"
     "shelving.width=" (f/default :width) ";\n"
     "shelving.depth=" (f/default :depth) ";\n"
     "shelving.cutout='" (str (f/default :cutout)) "';\n"
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

         // Start animating.
         drawShelving(shelving, model[0]);
         startAnimation();

         // The color picker doesn't have a way to supply a callback in addition
         // to the synched form field. So, periodically check the background color
         // of the synched form field and re-render when it changes.
         function checkColor() {
           var color = $('#color').css('background-color');
           color = rgb2hex(color);
           if (shelving.color != color) {
             shelving.color = color;
             // For dark colors, use a gray background.
             var bg = luminence(color) > 0.1 ? '#000' : '#666';
             $('#model').css({'background-color': bg});
             updateAnimation(shelving);
           }
           // Rate limit model updates since the color picker spews events rapidly
           // and can cause slowness and/or webgl crashes.
           setTimeout(function() {requestAnimFrame(checkColor);}, 1000);
         }
         checkColor();
     });"
     ]]
   ])

(defn handle
  [req]
  (response (standard-page (head) (f/with-form (:shelving sf/cart-items) (:params req) (body)) 544)))

#_ (remove-ns 'sistemi.site.shelving-htm)
(sistemi.registry/register)
