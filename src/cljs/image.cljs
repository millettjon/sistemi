(ns image
  (:require [monet.canvas :as c])
  (:use [jayq.util :only [log]]))

(defn onload
  "Loads a set of images specified in a map and then calls f when all
images have been loaded. The map values should contain image sources.
The callback function f will be passed a map with the same keys as the
initial map but with the values set to the images.

Example:
 (img/onload {:wheel-img \"valchromat-wheel-raw.png\"}
              #(-> (c/get-context canvas \"2d\")
                   (.drawImage (:wheel-img %) 0 0)))
"
  [m f]
  (when-not (empty? m)
    (let [len (count m)
          ;; Create an atom to hold the loaded images.
          a (atom {})

          ;; Create a list of images and there sources to later load.
          ;; Set up the onload hander for each image to add it to the atom.
          m (into {} (for [[k src] m]
                       [src (let [img (js/Image.)
                                  f   (fn [] (swap! a #(assoc % k img)))]
                              (set! (.-onabort img) f)
                              (set! (.-onerror img) f)
                              (set! (.-onload img) f)
                              img)]))]

          ;; Create a watcher function to call the callback once all images are loaded.
          (add-watch a :onload (fn [k a old-val new-val]
                                 (when (= len (count new-val))
                                   (f new-val))))

          ;; Set image sources to start loading.
          (doseq [[src img] m] (set! (.-src img) src))

          true)))

(defn get-img
  "Returns an image from an image array."
  [src-img size index]
  (let [width       (.-width src-img)
        num-columns (/ width size)
        row (quot index num-columns)
        col (rem index num-columns)
        canvas (.createElement js/document "CANVAS")]
    (set! (.-width canvas) size)
    (set! (.-height canvas) size)
    (-> (c/get-context canvas "2d")
        (.drawImage src-img
                    (* col size) (* row size) size size ; source
                    0 0 size size ; destination
                    ))
    canvas))
