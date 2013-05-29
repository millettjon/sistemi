(ns color.val-picker
  (:require [monet.canvas :as c]
            [canvas :as c2]
            [dommy.core :as d]
            util.map
            [image :as img])
  (:use [jayq.util :only [log]])
  (:use-macros [dommy.macros :only [sel sel1]]
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

(defn- clear-color-label
  []
  (let [{:keys [ctx center opts]} wheel
        ;; Calculate a bounding radius of inner circle and clear it.
        radius (- (:radius opts)
                  (:width opts)
                  3  ; width of focus arc
                  1  ; safety margin
                  )]

    ;; Fill inner circle with black.
    (-> ctx
        c/begin-path
        (c2/arc center radius 0 (* 2 Math/PI) :cw)
        (c/fill-style "black")
        c/fill)))

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

(defn- redraw
  []
  (let [{:keys [ctx]} wheel]
    (-> ctx
        c2/clear

        ;; Draw label at upper left.
        (c/fill-style :#777)
        (c/font-style "bold 12px Arial")
        (c/text-align :left)
        (c/text {:text "VAL" :x 0 :y 15})

        ;; Draw background wheel image.
        ;;draw-static-wheel
        draw-wheel)

    (clear-color-label)))

;; Adjust angle to match start of pie slices.
;; Note:
;; - the pie chart is aligned at 6 o'clock and
;; - 3 pie slices are just greater than 90 degrees
(defn- adjust-angle
  "Adjusts an angle for swatch offset."
  ([angle] (adjust-angle angle :cw))
  ([angle direction]
  (let [offset (- (* (/ (* 2 Math/PI) 11) 3) ; 3 pie sections
                    (/ Math/PI 2))
        f (get {:cw + :ccw -} direction)
        ;angle (f angle offset)
        ]

    ;; Normalize and returnl
    (if (>= angle 0)
      angle
      (+ angle (* 2 Math/PI))))))

(defn- bucket-index
  "Returns the index of the swatch which the mouse is over."
  [e]
  (let [offset (c2/offset e)
        center (c2/center e)

        ;; Find polar angle.
        p (c2/point-diff offset center)
        theta (Math/atan2 (:y p) (:x p))

        arc-length (adjust-angle theta)

        ;; Find bucket index.
        bucket-arc (-> Math.PI (* 2) (/ (-> wheel :swatches count)))
        ]
    (Math/floor (-> arc-length (/ bucket-arc)))))

(defn- focus-swatch
  "Focuses or unfocuses a swatch"
  [index focus?]   ; focus if focus? is true, otherwise unfocus
  (let [{:keys [ctx center swatches]
         {:keys [radius]} :opts} wheel
        radius (+ radius 2) ; add a margin to the radius

        ;; find start and end angles for the slice
        palette-length (count swatches)
        start-angle (-> Math/PI (* 2) (* index) (/ palette-length))
        end-angle (-> Math/PI (* 2) (* (inc index)) (/ palette-length))

        ;; if unfocusing, user wider angle to make sure all traces get erased
        start-angle (- start-angle (if focus? 0 0.02))
        end-angle (+ end-angle (if focus? 0 0.02))

        ;; adjust for offset of pie slices in image
        start-angle (adjust-angle start-angle :ccw)
        end-angle (adjust-angle end-angle :ccw)]

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
    (log "set-focus: index: " index)
    (focus-swatch index true)
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
        #_(log "outer band: " index)
        ;; Update display if moving to a new bucket.
        (when (not= @cursor index)
          (clear-focus)
          (set-focus e)
          ))

     ;; OUTSIDE THE OUTER BAND
     (> distance radius)
     (when @cursor
       (log "outside")
       (clear-focus)
       (redraw))

     ;; IN THE INNER CIRCLE
     :default
     (when @cursor
       (log "inside")
       (clear-focus)))))

(defn on-mousedown
  [e]
  (log "mousedown")
  ;; (when-let [color (get-color e)]
  ;;   ((:callback wheel) (clj->js color)))
  )

(defn on-touchstart
  [e]
  (let [oe (.-originalEvent e)]
    (when (= 1 (.. oe touches length))
      (.-preventDefault e)
      (on-mousemove e) ; needed for firefox on android to focus a swatch on tap
      )))

(defn on-touchmove
  [e]
  (let [oe (.-originalEvent e)]
    (when (= 1 (.. oe touches length))
      (.-preventDefault e)
      (on-mousemove e))))

(defn on-touchend
  [e]
  (let [oe (.-originalEvent e)]
    (when (= 1 (.. oe touches length))
      (.-preventDefault e)
      (on-mousedown e))))

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

(defn ^:export init [canvas callback]
  (log "initializing valchromat wheel raw")

  (onload-let [wheel-raw "valchromat-wheel-raw.png"
               palette-raw "valchromat-raw-palette-64.jpg"
               palette-oiled "valchromat-oiled-palette-64.jpg"]

              ;; draw raw palette
              (d/append! (sel1 :body) [:br])
              (d/append! (sel1 :body) [:br])
              (doseq [i (range 11)]
                (let [swatch (img/get-img palette-raw 64 i)]
                  (d/append! (sel1 :body) swatch)))

              ;; draw oiled palette
              (d/append! (sel1 :body) [:br])
              (doseq [i (range 11)]
                (let [swatch (img/get-img palette-oiled 64 i)]
                  (d/append! (sel1 :body) swatch)))

              ;; (c2/centerZ canvas)
              (set! wheel (assoc wheel
                            :opts defaults
                            :swatches [1 2 3 4 5 6 7 8 9 10 11]
                            :textures (for [i (range 11)] (img/get-img palette-raw 64 i))
                            :canvas canvas
                            :callback callback
                            :background wheel-raw
                            :ctx (c/get-context canvas "2d")
                            :center (c2/center canvas)
                            :cursor (atom nil)))
              (redraw)

              ;; Hookup event handlers.
              (d/listen! canvas :mousemove (wheel-fn wheel on-mousemove))
              (d/listen! canvas :mousedown (wheel-fn wheel on-mousedown))
              (d/listen! canvas :touchstart (wheel-fn wheel on-touchstart))
              (d/listen! canvas :touchmove (wheel-fn wheel on-touchmove))
              (d/listen! canvas :touchend (wheel-fn wheel on-touchend))
              (d/listen! canvas :mouseout (wheel-fn wheel on-mouseout))
              ))
