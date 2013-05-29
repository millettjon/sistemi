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
               :palette "Valchromat"
               :color (->> index (nth (:palette wheel)) :name name))]
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
        bucket-arc (-> Math.PI (* 2) (/ (-> wheel :palette count)))
        ]
    (Math/floor (-> arc-length (/ bucket-arc)))))

    
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
    #_ (log "set-focus: index: " index)
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
        (log "outer band: " index)
        (log offset)
        ;; Update display if moving to a new bucket.
        (when (not= @cursor index)
          (clear-focus)
          (set-focus e)
          ))

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

(defn ^:export init [canvas palette callback]
  (log "initializing valchromat wheel raw")

  ;; TODO: load textures based on current palette
  ;;(log (-> js/window .-location .-pathname))

  ;; ? how to load image relative to namespace?
  ;;   - note: can't be relative since any page could be calling this
  ;; ns color.val-picker
  ;; path /color/val-picker/foo
  ;;
  ;; ? how to determine root path when loaded as a file?
  ;;   - search back up path for munged namespace file://foo/bar/baz/color/val-picker
  ;;
  ;; actual path is /pie-picker

  ;; ? how to determine location of image?
  ;; ? how to get this to work for both a file and a web page?
  ;; ? is there a way to determine the current root?
  ;; ? is there a way to load images from the directory in which we are?
  ;; /pie-picker/valchromat-raw-palette-64.jpg
  ;; /pie-picker/valchromat-raw-palette-64.jpg
  (let [[palette palette-src] (case palette
                                "raw"  [color.valchromat/palette-raw "valchromat-raw-palette-64.jpg"]
                                "oiled" [color.valchromat/palette-oiled "valchromat-oiled-palette-64.jpg"])]
    (onload-let [;;wheel-raw "valchromat-wheel-raw.png"
                 ;;palette-raw "valchromat-raw-palette-64.jpg"
                 ;;palette-oiled "valchromat-oiled-palette-64.jpg"
                 textures palette-src]

                ;; draw raw palette
                ;; (d/append! (sel1 :body) [:br])
                ;; (d/append! (sel1 :body) [:br])
                ;; (doseq [i (range 11)]
                ;;   (let [swatch (img/get-img palette-raw 64 i)]
                ;;     (d/append! (sel1 :body) swatch)))

                ;; draw oiled palette
                ;; (d/append! (sel1 :body) [:br])
                ;; (doseq [i (range 11)]
                ;;   (let [swatch (img/get-img palette-oiled 64 i)]
                ;;     (d/append! (sel1 :body) swatch)))

                (set! wheel (assoc wheel
                              :opts defaults
                              :palette palette
                              :textures (for [i (range 11)] (img/get-img textures 64 i))
                              :canvas canvas
                              :color-label (sel1 :#color-label)
                              :callback callback
                              ;;:background wheel-raw
                              :ctx (c/get-context canvas "2d")
                              :center (c2/center canvas)
                              :cursor (atom nil)))
                (log (c2/center canvas))
                (redraw)

                ;; Hookup event handlers.
                (d/listen! canvas :mousemove (wheel-fn wheel on-mousemove))
                (d/listen! canvas :mousedown (wheel-fn wheel on-mousedown))
                (d/listen! canvas :touchstart (wheel-fn wheel on-touchstart))
                (d/listen! canvas :touchmove (wheel-fn wheel on-touchmove))
                (d/listen! canvas :touchend (wheel-fn wheel on-touchend))
                (d/listen! canvas :mouseout (wheel-fn wheel on-mouseout))
                )))
