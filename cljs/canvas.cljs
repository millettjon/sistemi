(ns canvas
  (:require [monet.canvas :as c])
  (:use [jayq.util :only [log]]))

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
  (let [])
  
  (if (-> e .-offsetX nil? not)
    ;; mouse event
    (point (.-offsetX e) (.-offsetY e))

    (let [ox (-> e .-target .-offsetLeft)
          oy (-> e .-target .-offsetTop)]

      (if (-> e .-pageX nil? not)
        ;; firefox mouse event
        (point (- (.-pageX e) ox) (- (.-pageY e) oy))

        ; touch event
        (let [t (-> e .-touches first)]
          (point (- (.-pageX t) ox) (- (.-pageY t) oy)))))))

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
