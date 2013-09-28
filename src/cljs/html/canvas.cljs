(ns html.canvas
  (:require [monet.canvas :as c])
  (:use [jayq.util :only [log]]))


;;
;; Note: Degrees proceed in counter clockwise fashion (as y axis is reversed).
;;
;;         -pi/2
;;   pi/-pi      0
;;          pi/2

(defn point
  [x y]
  {:x x :y y})

(defn move-to [ctx {:keys [x y]}]
  "Like monet.canvas/move-to except takes a point as a argument instead of x y."
  (. ctx (moveTo x y))
  ctx)

(defn line-to [ctx {:keys [x y]}]
  "Like monet.canvas/line-to except takes a point as a argument instead of x y."
  (. ctx (lineTo x y))
  ctx)

(defn arc
  [ctx {:keys [x y]} radius start end direction]
  (. ctx (arc x y radius start end (direction {:cw false, :ccw true})))
  ctx)

(defn offset
  "Returns the x, y offset of an event in its target."
  [e]
  (if-let [ox (.-offsetX e)]
    ;; mouse event
    (point ox (.-offsetY e))

    ;; firefox mouse event
    ;; See: http://www.jacklmoore.com/notes/mouse-position/
    (let [rect (-> e .-target .getBoundingClientRect)
          item (if-let [touches (-> e .-touches)] ; touch event
                 (.item touches 0)
                 e)]
      (point (- (.-clientX item) (.-left rect)) (- (.-clientY item) (.-top rect))))))

(defn center
  "Returns the center of an element or event."
  [item]
  (let [e (if (.-nodeType item)
            item ; dom element
            (.-target item) ; event target
            )]
    (point (-> e .-width (/ 2))
           (-> e .-height (/ 2)))))

(defn distance
  "Returns the distance between two points."
  [a b]
  (let [xd (- (:x a) (:x b))
        yd (- (:y a) (:y b))]
    (Math/sqrt (+ (* xd xd) (* yd yd)))))

(defn point-diff
  "Difference between two points."
  [p1 p2]
  (point (- (:x p1) (:x p2)) (- (:y p1) (:y p2)))) 

(defn extent [ctx]
  "Returns the extent of a canvas as a rect."
  (let [canvas (.-canvas ctx)]
    {:x 0 :y 0 :w (.-width canvas) :h (.-height canvas)}))

(defn clear [ctx]
  "Clears a canvas."
  (c/clear-rect ctx (extent ctx)))

(defn image
  [ctx img {:keys [x y]}]
  (.drawImage ctx img x y)
  ctx)

