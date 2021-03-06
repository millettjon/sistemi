(ns color.val-picker
  (:require [color.valchromat :as v]
            [monet.canvas :as c]
            [html.canvas :as c2]
            [dommy.core :as d]
            util.map
            [image :as img]
            [translate :as tr])
  (:use [jayq.util :only [log]])
  (:use-macros [dommy.macros :only [sel sel1 node]]
               [image.macros :only [onload-let]]))

(def ^:dynamic wheel)

(def defaults
  {;; radius of color wheel
   :radius 94

   ;; width of band
   :width #_ 58 ; inner radius (same width as inner ral picker circle)
          #_ 28 ; inner radius (same width as outer ral picker circle)
          43    ; split the difference

   :focus-bar-width 2
   })

(defn- draw-static-wheel
  [ctx]
  (let [{:keys [background center]
         {:keys [radius]} :opts} wheel]
    (-> ctx
        (c2/image (:background wheel) {:x 0 :y 0})

        ;; Stroke a black border to shrink the circle a few pix.
        c/begin-path
        (c2/arc center (- radius 0) 0 (* 2 Math/PI) :cw)
        (c/stroke-width 4)
        (c/stroke-style "#000")
        c/stroke)))

(defn- draw-wheel
  [ctx]
  (let [{:keys [center textures]
         {:keys [radius width]} :opts} wheel
         inner-radius (- radius width)
         len (count textures)
         angle-fn #(-> % (/ len) (* 2 Math/PI))]
    (doseq [i (range len)]
      (let [;; Calcluate start and end angles.
            start-angle (angle-fn i)
            end-angle (angle-fn (inc i))
            ;; Calcluate start point.
            start (c2/point (-> inner-radius (* (Math/cos start-angle)) (+ (:x center)))
                            (-> inner-radius (* (Math/sin start-angle)) (+ (:y center))))]

        ;; Setup the fill pattern.
        (let [texture (nth textures i)
              pattern (.createPattern ctx (nth textures i) "repeat")]
          (set! (.-fillStyle ctx) pattern))

        ;; Fill the pie slice.
        (-> ctx
            c/begin-path
            (c2/move-to start)
            (c2/arc center inner-radius start-angle end-angle :cw) ; inner arc
            (c2/arc center radius end-angle start-angle :ccw) ; outer arc
            (c2/line-to start)
            c/fill)))))

(defn- set-color-label!
  [state & [index]]
  (let [text (case state
               :empty ""
               :palette (tr/translate v/palette-strings (:label wheel))
               :color (->> index (nth (:palette wheel)) color/format-name))]
    (-> (:color-label wheel)
        (d/set-text! text))))

(defn- redraw
  []
  (let [{:keys [ctx]} wheel]
    (-> ctx
        c2/clear

        ;; Draw label at upper left.
        (c/fill-style :#777)
        (c/font-style "bold 12px Arial")
        (c/text-align :left)
        #_(c/text {:text "VAL" :x 0 :y 15})

        ;; Draw background wheel image.
        ;;draw-static-wheel
        draw-wheel)

    (set-color-label! :palette)))

(defn- normalize-angle
  [angle]
  (if (>= angle 0)
    angle
    (+ angle (* 2 Math/PI))))

(defn- bucket-index
  "Returns the index of the swatch which the mouse is over."
  [e]
  (let [offset (c2/offset e)
        center (c2/center e)

        ;; Find polar angle.
        p (c2/point-diff offset center)
        theta (-> (Math/atan2 (:y p) (:x p)) normalize-angle)

        ;; Find bucket index.
        bucket-arc (-> Math.PI (* 2) (/ (-> wheel :palette count)))
        ]
    (Math/floor (-> theta (/ bucket-arc)))))

    
(defn- focus-swatch
  "Focuses or unfocuses a swatch"
  [index focus?]   ; focus if focus? is true, otherwise unfocus
  (let [{:keys [ctx center palette]
         {:keys [radius]} :opts} wheel
        radius (+ radius 2) ; add a margin to the radius

        ;; find start and end angles for the slice
        palette-length (count palette)
        start-angle (-> Math/PI (* 2) (* index) (/ palette-length))
        end-angle (-> Math/PI (* 2) (* (inc index)) (/ palette-length))

        ;; if unfocusing, user wider angle to make sure all traces get erased
        start-angle (- start-angle (if focus? 0 0.02))
        end-angle (+ end-angle (if focus? 0 0.02))

        ;; adjust for offset of pie slices in image
        start-angle (normalize-angle start-angle)
        end-angle (normalize-angle end-angle)]

    ;; Draw focus arc.
    (-> ctx
        c/begin-path
        (c2/arc center radius start-angle end-angle :cw)
        (c/stroke-width (+ (get-in wheel [:opts :focus-bar-width]) (if focus? 0 1)))
        (c/stroke-style (if focus? "#fff" "#000"))
        c/stroke)))

(defn- clear-focus
  []
  (when-let [{:keys [cursor]} wheel]
    (let [{:keys [ctx center]} wheel]
      (focus-swatch @cursor false))
    (swap! cursor (constantly nil))))

(defn- set-focus
  [e]
  (let [{:keys [ctx center cursor]} wheel
        index (bucket-index e)]
    ;;(log "set-focus: index: " index)
    (focus-swatch index true)
    (set-color-label! :color index)
    (swap! cursor (constantly index))))

(defn on-mousemove
  "Handle mouse move events."
  [e]
  (let [{:keys [center opts cursor]} wheel
        offset (c2/offset e)
        distance (c2/distance center offset)
        radius (:radius opts)
        width (:width opts)]

    (cond
     ;; IN THE OUTER BAND
     (and (<= distance radius)
          (>= distance (- radius width)))
     (let [index (bucket-index e)]
       #_ (log "outer band: " index)
       ;; Update display if moving to a new bucket.
       (when (not= @cursor index)
         (clear-focus)
         (set-focus e)))

     ;; OUTSIDE THE OUTER BAND
     (> distance radius)
     (when @cursor
       #_ (log "outside")
       (clear-focus)
       (redraw))

     ;; IN THE INNER CIRCLE
     :default
     (when @cursor
       #_ (log "inside")
       (set-color-label! :empty)
       (clear-focus)))))

(defn on-mousedown
  [e]
  (log "mousedown")
  (when-let [index @(:cursor wheel)]
    (let [{:keys [callback palette]} wheel
          color (nth palette index)]
      (callback (clj->js color)))))

(defn on-touchstart
  [e]
  (when (= 1 (-> e .-touches .-length))
    (.preventDefault e)
    (on-mousemove e) ; needed for firefox on android to focus a swatch on tap
    ))

(defn on-touchmove
  [e]
  (when (= 1 (-> e .-touches .-length))
    (.-preventDefault e)
    (on-mousemove e)))

(defn on-touchend
  [e]
  (when (= 1 (-> e .-touches .-length))
    (.-preventDefault e)
    (on-mousedown e)))

(defn on-mouseout
  [e]
  (clear-focus)
  (redraw))

(defn wheel-fn
  "Returns a function that binds wheel to w and calls f inside that binding."
  [w f]
  (fn [& args]
    (binding [wheel w]
      (apply f args))))

(defn ^:export init [container palette-name callback]
  (log "initializing valchromat wheel " palette-name)

  ;; TODO: load textures based on current palette
  ;;(log (-> js/window .-location .-pathname))

  (let [canvas (node [:canvas {:width 195 :height 195}])
        color-label (node [:span {:style {:display "table-cell" :vertical-align "middle" :max-width "90px"}}])
        {:keys [colors textures-src label]} ((keyword palette-name) v/palettes)]
    (onload-let [textures textures-src]
                ;; Setup the dynamic var to use in event handlers.
                (set! wheel (assoc wheel
                              :opts defaults
                              :palette colors
                              :textures (for [i (range 11)] (img/get-img textures 64 i))
                              :canvas canvas
                              :color-label color-label
                              :label label
                              :callback callback
                              :ctx (c/get-context canvas "2d")
                              :center (c2/center canvas)
                              :cursor (atom nil)))

                (let [;; Add DOM elements to container
                      ;; wrapper needs position relative so that color label div and be positioned absolutely
                      wrapper [:div {:style {:position "relative"}}
                               canvas
                               ;; wrapper div to center color label in wheel
                               ;; note: z-index of -1 so that canvas doesn't get mouseout events when mouse is over label
                               [:div {:style {:position "absolute" :left "53px" :top "72px"
                                              :width "90px" :height "50px"
                                              :font-size "12px"
                                              :color "#AAA"
                                              :display "table"
                                              :text-align "center"
                                              :overflow "hidden"
                                              :z-index "-1"}}
                                color-label]]]

                  (d/append! (sel1 (keyword container)) wrapper)

                  (redraw)

                  ;; Hookup event handlers.
                  (d/listen! canvas :mousemove (wheel-fn wheel on-mousemove))
                  (d/listen! canvas :mousedown (wheel-fn wheel on-mousedown))
                  (d/listen! canvas :touchstart (wheel-fn wheel on-touchstart))
                  (d/listen! canvas :touchmove (wheel-fn wheel on-touchmove))
                  (d/listen! canvas :touchend (wheel-fn wheel on-touchend))
                  (d/listen! canvas :mouseout (wheel-fn wheel on-mouseout))
                  ))))
