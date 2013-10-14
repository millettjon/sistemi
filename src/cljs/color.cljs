(ns color
  (:use [jayq.util :only [log]]))

(defn pack-rgb
  "Packs an rgb array into an #RRGGB string."
  [rgb]
  (apply str "#"
         (->> rgb
              (map #(Math/round (* % 255)))
              (map #(.toString % 16))
              (map #(if (< (count %) 2) (str "0" %) %)))))

(defn unpack-rgb
  "Unpacks an rgb string #RRGGBB into an array."
  [rgb]
  (->> (rest rgb)             ; remove leading #
       (partition 2)
       (map #(-> (apply str "0x" %)
                 js/parseInt
                 (/ 255)))))

(defn rgb-to-hsl [rgb]
  (let [[r g b] (unpack-rgb rgb)
        min (min r g b)
        max (max r g b)
        delta (- max min)
        l (-> min (+ max) (/ 2))
        s (if (and (> l 0) (< l 1))
            (/ delta (if (< l 0.5)
                       (* 2 l)
                       (- 2 (* 2 l))))
            0)
        h (if (> delta 0)
            (/ (+ (if (and (= max r) (not= max g)) (/ (- g b) delta) 0)
                  (if (and (= max g) (not= max b)) (+ 2 (/ (- b r) delta)) 0)
                  (if (and (= max b) (not= max r)) (+ 4 (/ (- r g) delta)) 0))
               6)
            0)]
    [h s l]))

(defn hue-to-rgb
  [m1 m2 h]
  (let [h (cond (< h 0) (inc h)
                (> h 1) (dec h)
                :default h)]
    (cond (< (* h 6) 1)   (+ m1 (* (- m2 m1) h 6))
          (< (* h 2) 1)   m2
          (< (* h 3) 2)   (+ m1 (* (- m2 m1) (- 0.66666 h) 6))
          :default        m1)))

(defn hsl-to-rgb
  [[h s l]]
  (let [m2 (if (<= l 0.5) (* l (inc s)) (- (+ l s) (* l s)))
        m1 (- (* l 2) m2)
        f  #(hue-to-rgb m1 m2 (+ h %))]
    (map f [0.33333 0 -0.33333])))

(defn hsl [color]
  (rgb-to-hsl (:rgb color)))

(defn hue [color]
  (first (hsl color)))

(defn average
  "Returns the average of an array of colors."
  [colors]
  (let [len (count colors)]
    (->> colors
         (map :rgb)
         (map unpack-rgb)
         (reduce #(map + %1 %2) [0 0 0])
         (map #(/ % len))
         pack-rgb
         (assoc {} :rgb))))

(defmulti format-name
  "Returns a html formatted color name."
  :type)

(defn ^:export formatName
  [color]
  (-> color edn/convert format-name))

