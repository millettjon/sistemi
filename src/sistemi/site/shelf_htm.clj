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
        [:style "span.customStyleSelectBox {height: 23px; line-height: 24px; width: 88px; background-color: #fff; color:#555; padding:0px 3px 0px 7px; border:1px solid #e7dab0; -moz-border-radius: 5px; -webkit-border-radius: 5px;border-radius: 5px 5px; }"]
        [:style ".customStyleSelectBoxInner {background:url(jquery.customSelect/arrow.png) no-repeat center right; }"]

        ;; color picker
        [:script {:type "text/javascript" :src "farbtastic/farbtastic.js"}]
        [:link {:rel "stylesheet" :href "farbtastic/farbtastic.css" :type "text/css"}]

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
   [:div.span6
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
        (f/select :width {:class "chzn-select" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "depth"} (tr/translate :depth)]
       [:div.controls
        (f/select :depth {:class "chzn-select" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "finish"} (tr/translate :finish)]
       [:div.controls
        (f/select :finish {:class "customStyleSelectBox" :style "width: 100px" :tabindex 1})]]

      [:div.control-group
       [:label.control-label {:for "color"} (tr/translate :color)]
       [:div.controls
        (f/text :color {:tabindex 1})]]
      [:div#colorpicker {:style "margin-left: 20px;"}]

      [:div.control-group {:style "margin-top: 20px;"}
       [:label.control-label {:for "quantity"} (tr/translate :quantity)]
       [:div.controls
        (f/select :quantity {:class "chzn-select" :tabindex 1})
        [:div#numDisplayed {:style "text-align: center; visibility: hidden"} "(4 displayed)"]
        ]]

      [:div {:style "text-align: right"}
       [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} (if (= -1 (f/default :id)) (tr/translate :cart :add)
                                                                      (tr/translate :cart :update) )]]]]

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
         $('#colorpicker').farbtastic('#color');
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
         $('#quantity').chosen().change(function() {
           shelf.quantity = $(this).val();
           updateAnimation(shelf);
         });

         // Set the container div height to match the width.
         var model = $('#model');
         var height = model.width();
         model.css({height: height.toString() + 'px'});

         // Start animating.
         drawShelf(shelf, model[0]);
         startAnimation();

         // The color picker doesn't have a way to supply a callback in addition
         // to the synched form field. So, periodically check the background color
         // of the synched form field and re-render when it changes.
         function checkColor() {
           var color = $('#color').css('background-color');
           color = rgb2hex(color);
           if (shelf.color != color) {
             shelf.color = color;
             // For dark colors, use a gray background.
             var bg = luminence(color) > 0.1 ? '#000' : '#666';
             $('#model').css({'background-color': bg});
             updateAnimation(shelf);
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
  (response (standard-page (head) (f/with-form (:shelf sf/cart-items) (:params req) (body)) 544)))

#_ (remove-ns 'sistemi.site.shelf-htm)
(sistemi.registry/register)
