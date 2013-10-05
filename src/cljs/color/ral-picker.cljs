(ns color.ral-picker
  (:require [monet.canvas :as c]
            [html.canvas :as c2]
            [dommy.core :as d]
            util.map)
  (:use [jayq.util :only [log]])
  (:use-macros [dommy.macros :only [sel sel1 node]]))

(def defaults
  "Configuration defaults."
  {:outer-band {:color-fn :average
                :force-full-saturation false
                :force-midrange-brightness false}
   :band {:margin 0}
   :swatch {:min-width 25
            :border-width 0}
   :palette {:sort false}
   :inner-band {:sort false}
   :focus-bar-width 2})

(def ^:dynamic wheel)

(defn bucketize
  "Evenly partitions a items into buckets."
  [items num-buckets]
  (let [bucket-size (/ (count items) num-buckets)]
    (->> items
         (map vector (range))
         (partition-by (fn [[idx elm]] (Math/floor (/ idx bucket-size))))
         (map #(map second %)))))

(defn max-swatches
  "Returns the maximum number of swatches that fit in a band of the
given radius. This is set both for visibility and to allow selection
of a swatch by finger tap on touch devices."
  [radius]
  (-> radius
      (* 2 Math/PI)
      (/ (get-in wheel [:opts :swatch :min-width]))
      Math.floor))

(defn make-simple-swatch
  [color]
  {:type :swatch :color color})

(defn swatch-color
  "Pick a color to represent the swatch's palette."
  [palette]
  (let [color-fn (get-in wheel [:opts :outer-band :color-fn])]
    (case color-fn
      :first (first palette)
      :median (nth palette (Math/floor (/ (count palette) 2)))
      :average (color/average palette)
      :default (throw (str "bad color-fn: " color-fn)))))

(defn sort-by-saturation-and-brightness
  "Sort the pallet by saturation and brightness. When mapped onto
a circle, brighter colors should be on top, darker on bottom,
more saturated on the left, and less saturated on the right.

Steps:
- saturation and brightness range from 0 to 1
- re-normalize ranges to -0.5 to 0.5 for polar conversion to map onto circle
- convert to polar
- compare angle"
  [palette]
  (let [f #(let [[_ s l] (-> % :rgb color/hsl)]
                          (Math/atan2 (- l 0.5) (- s 0.5)))]
    (sort-by f palette)))

(declare make-band)

(defn make-complex-swatch
  [palette parent-band]
  (let [color (swatch-color palette)
        hsl (color/hsl color)

        ;; Maximize saturation
        hsl (if (get-in wheel [:opts :outer-band :force-full-saturation])
              (assoc hsl 1 1.0)
              hsl)

        ;; Use mid-range brightness
        hsl (if (get-in wheel [:opts :outer-band :force-midrange-brightness])
              (assoc hsl 2 0.5)
              hsl)

        color (->> hsl color/hsl-to-rgb color/pack-rgb (assoc {} :rgb))

        ;; Sort by hue.
        ;; TODO: enable/disable from gui
        ;;(sort #(color/hue (:rgb %)) palette)

        ;; Sort by saturation and brightness.
        palette (if (get-in wheel [:opts :inner-band :sort])
                  (sort-by-saturation-and-brightness palette)
                  palette)
        ]
    
    ;; A margin of 1, makes for a cleaner redraw since the perimeter
    ;; stroke doesn't overlap the outer band.
    (let [margin (get-in wheel [:opts :band :margin])
          width  (:width parent-band)
          radius (- (:radius parent-band) width margin)
          ]
      {:type :swatch
       :color color
       :band (make-band radius width palette)})))


(defn make-band
"Makes a color band.
 Data Shape:
 band:
   radius
   width
   swatches

 swatch:
   color
   band"
  [radius, width, palette]

  (let [len  (count palette)
        max-swatches (max-swatches radius)
        band {:type :band, :radius radius, :width width}
        swatch (if (<= len max-swatches)
                 ;; entire palette fits
                 (map make-simple-swatch palette)
                 ;; if too large, divide into sub palettes
                 (map #(make-complex-swatch % band) (bucketize palette max-swatches)))]
    (assoc band :swatches swatch)))

(defn draw-color-band
  "Draws a color band a circular color band of the specified width and palette."
  [band]
  (let [{:keys [ctx center]} wheel
        inner-radius (- (:radius band) (:width band))
        palette (->> band :swatches (map :color))
        len (count palette)
        angle-fn #(-> % (/ len) (* 2 Math/PI))]

    ;; Loop over each section.
    (doseq [i (range len)]
      (let [;; Calcluate start and end angles.
            start-angle (angle-fn i)
            end-angle (angle-fn (inc i))
            ;; calcluate start point
            start (c2/point (-> inner-radius (* (Math/cos start-angle)) (+ (:x center)))
                         (-> inner-radius (* (Math/sin start-angle)) (+ (:y center))))]

        ;; Fill pie slice.
        (-> ctx
            c/begin-path
            (c2/move-to start)
            (c2/arc center inner-radius start-angle end-angle :cw) ; inner arc
            (c2/arc center (:radius band) end-angle start-angle :ccw) ; outer arc
            (c2/line-to start)
            (c/fill-style (-> palette (nth i) :rgb))
            c/fill)
        
        ;; Draw border.
        (let [bw (get-in wheel [:opts :swatch :border-width])]
          (when (> bw 0)
            (-> ctx
                (c/stroke-width bw)
                c/stroke)))
        ))))

(defn in-inner-band?
  "Returns true if the event coordinates are in the inner band."
  [e]
  (let [{:keys [band center]} wheel
        offset (c2/offset e)
        distance (c2/distance center offset)
        radius (- (:radius band) (:width band) (get-in wheel [:opts :band :margin]))]
    (and (<= distance radius)
         (>= distance (- radius (:width band))))))

(defn bucket-index
  "Returns the index of the swatch which the mouse is over."
  [e band]
  (let [offset (c2/offset e)
        center (c2/center e)

        ;; Find polar angle.
        p (c2/point-diff offset center)
        theta (Math/atan2 (:y p) (:x p))

        ;; Normalize angle to find arc length.
        arc-length (if (>= theta 0)
                     theta
                     (+ theta (* 2 Math/PI)))

        ;; Find bucket index.
        bucket-arc (-> Math.PI (* 2) (/ (-> band :swatches count)))
        ]
    (Math/floor (-> arc-length (/ bucket-arc)))))

(defn- cursor
  "Returns the current cursor."
  []
  (-> wheel :state deref :cursor))

(defn- inner-index
  "Returns the cursor index in the outer band."
  []
  (:inner (cursor)))

(defn- outer-index
  "Returns the cursor index in the outer band."
  []
  (:outer (cursor)))

(defn- set-cursor!
  [k v]
  (swap! (:state wheel) #(assoc-in % [:cursor k] v)))

(defn focus-swatch
  "Focuses or unfocuses a swatch"
  [band
   index
   focus?  ; focus if true, otherwise unfocus
   inner?  ; true if this is an inner swatch
   ]
  (let [{:keys [ctx center]} wheel
        radius (:radius band)
        radius (if inner?
                 (- radius (:width band) 2)
                 (+ radius 2))

        ;; find start and end angles for the slice
        palette-length (-> band :swatches count)
        start-angle (-> Math/PI (* 2) (* index) (/ palette-length))
        end-angle (-> Math/PI (* 2) (* (inc index)) (/ palette-length))

        ;; if unfocusing, user wider angle to make sure all traces get erased
        start-angle (- start-angle (if focus? 0 0.02))
        end-angle (+ end-angle (if focus? 0 0.02))
        ]
    ;; Draw focus arc.
    (-> ctx
        c/begin-path
        (c2/arc center radius start-angle end-angle :cw)
        (c/stroke-width (+ (get-in wheel [:opts :focus-bar-width]) (if focus? 0 1)))
        (c/stroke-style (if focus? "#fff" "#000"))
        c/stroke)))

(defn- get-color
  "Returns the color under point (if any)."
  [e]
  (when (in-inner-band? e)
    (let [band (:band wheel)
          oi (outer-index)
          ii (inner-index)]
      (if (and oi ii)
        (-> band :swatches (nth oi) :band :swatches (nth ii) :color)))))

(defn- set-color-label!
  [state & [color]]
  (let [text (case state
               :empty ""
               :palette "RAL"
               :color (:code color))]
    (-> wheel :color-label (d/set-text! text))))

(defn- clear-outer-focus
  "Clears the focus on the outer band."
  []
  (when-let [outer-index (outer-index)]
    (let [{:keys [ctx center band]} wheel]
      (focus-swatch band outer-index false false))
    (set-cursor! :outer nil)))

(defn- set-outer-focus
  "Sets the focus on the outer band."
  [e]
  (set-color-label! :empty)
  (let [{:keys [ctx center band]} wheel
        outer-bucket-index (bucket-index e band)]
    (focus-swatch band outer-bucket-index true false)
    (set-cursor! :outer outer-bucket-index)))

(defn- clear-inner-focus
  "Clear focus on inner band."
  []
  (when-let [outer-index (outer-index)]
    (when-let [inner-index (inner-index)]
      (let [{:keys [band ctx center]} wheel
            inner-band (-> band :swatches (nth outer-index) :band)]
        (focus-swatch inner-band inner-index false true)
        (set-cursor! :inner nil)
        (set-color-label! :empty)))))

(defn- set-inner-focus
  "Sets the focus on the inner band."
  [e]
  (let [{:keys [band ctx center]} wheel]
    (when-let [outer-index (outer-index)]
      (let [inner-band (-> band :swatches (nth outer-index) :band)
            inner-index (bucket-index e inner-band)]
        (focus-swatch inner-band inner-index true true)
        (set-cursor! :inner inner-index)
        (set-color-label! :color (get-color e))))))

(defn- redraw
  []
  (let [{:keys [canvas ctx band center]} wheel]
    (c2/clear ctx)
    (-> wheel :band draw-color-band)
    (set-color-label! :palette)))

(defn on-mousemove
  "Handle mouse move events."
  [e]
  (let [{:keys [band center]} wheel
        offset (c2/offset e)
        distance (c2/distance center offset)
        radius (:radius band)
        inner-index (inner-index)
        outer-index (outer-index)]

    (cond
     ;; IN THE OUTER BAND
     (and (<= distance radius)
          (>= distance (- radius (:width band))))
     (let [outer-bucket-index (bucket-index e band)]
       ;; If there was an inner focus, clear it.
       (if inner-index
         (clear-inner-focus))
       #_ (log "outer band index=" outer-bucket-index)
       ;; Update display if moving to a new bucket.
       (when (not= outer-bucket-index outer-index)
         (clear-outer-focus)
         (set-outer-focus e)
         (let [inner-band (-> band :swatches (nth outer-bucket-index) :band)]
           (draw-color-band inner-band))))

     ;; IN THE INNER BAND
     (in-inner-band? e)
     (when outer-index
       ;; Only draw if moving to a new bucket.
       (let [inner-band (-> band :swatches (nth outer-index) :band)
             inner-bucket-index (bucket-index e inner-band)]
         (when (not= inner-index inner-bucket-index)
           (clear-inner-focus)
           (set-inner-focus e))))

     ;; OUTSIDE THE OUTER BAND
     (> distance radius)
     (when outer-index
       (clear-inner-focus)
       (clear-outer-focus)
       (redraw))

     ;; IN THE INNER CIRCLE
     :default
     (clear-inner-focus))))

(defn on-mousedown
  [e]
  (when-let [color (get-color e)]
    ((:callback wheel) (clj->js color))))

(defn on-touchstart
  [e]
  (when (= 1 (-> e .-touches .-length))
    (.preventDefault e)
    (on-mousemove e) ; needed for firefox on android to focus a swatch on tap
    ))

(defn on-touchmove
  [e]
  (when (= 1 (-> e .-touches .-length))
    (.preventDefault e)
    (on-mousemove e)))

(defn on-touchend
  [e]
  (when (= 1 (-> e .-touches .-length))
    (.preventDefault e)
    (on-mousedown e)))

(defn on-mouseout
  [e]
  (clear-inner-focus)
  (clear-outer-focus)
  (redraw))

(defn wheel-fn
  "Returns a function that binds wheel to w and calls f inside that binding."
  [w f]
  (fn [& args]
    (binding [wheel w]
      (apply f args))))

(defn ^:export init [container, palette, callback]
  (log "initializing ral color wheel")
  ;;(c2/centerZ canvas)
  (let [options (util.map/deep-merge-with identity defaults {})
        canvas (node [:canvas {:width 195 :height 195}])
        color-label (node [:span {:style {:display "table-cell" :vertical-align "middle" :max-width "90px"}}])]
    (binding [wheel {:opts options}]
      ;; Make the graph data structure representing the color wheel.
      (let [margin  (+ (:focus-bar-width options) 1)
            radius  (-> (.-height canvas) (/ 2) (- margin))
            width   (/ radius 3)
            palette (if (get-in options [:palette :sort])
                      (sort-by color/hue palette)
                      palette)
            band (make-band radius width palette)]

        ;; Create the final wheel map to encapsulate all data.
        ;; :opts    rendering options
        ;; :band    color band tree
        ;; :state   mutable state (e.g., for cursor)
        (set! wheel (assoc wheel
                      :band band
                      :callback callback
                      :canvas canvas
                      :color-label color-label
                      :ctx (c/get-context canvas "2d")
                      :center (c2/center canvas)
                      :state (atom {:cursor {:outer nil :inner nil}})))

        ;; Add DOM elements to conatiner.
        (let [;; wrapper needs position relative so that color label div and be positioned absolutely
              wrapper [:div {:style {:position "relative" :height "195px" :width "195px"}}
                       canvas
                       ;; wrapper div to center color label in wheel
                       ;; note: z-index of -1 so that canvas doesn't get mouseout events when mouse is over label
                       [:div {:style {:position "absolute" :left "53px" :top "72px"
                                      :width "90px" :height "50px"
                                      ;;:font-weight "bold"
                                      :font-size "12px"
                                      :color "#AAA"
                                      :display "table"
                                      :text-align "center"
                                      :overflow "hidden"
                                      :z-index "-1"}}
                        color-label]]]
          (d/append! (sel1 (keyword container)) wrapper))

        ;; Draw the outer band of the wheel.
        (redraw)

        ;; Hookup event handlers.
        (d/listen! canvas :mousemove (wheel-fn wheel on-mousemove))
        (d/listen! canvas :mousedown (wheel-fn wheel on-mousedown))
        (d/listen! canvas :touchstart (wheel-fn wheel on-touchstart))
        (d/listen! canvas :touchmove (wheel-fn wheel on-touchmove))
        (d/listen! canvas :touchend (wheel-fn wheel on-touchend))
        (d/listen! canvas :mouseout (wheel-fn wheel on-mouseout))
        ))))
