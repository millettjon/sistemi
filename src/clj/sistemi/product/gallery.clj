(ns sistemi.product.gallery
  (:require [util.path :as p]
            [clojure.string :as s]
            [util.edn :as edn]
            [clojure.core.cache :as cache]
            [util.map :as map]))

(def ^:private image-cache
  "Cache product images files for 1 minute."
  (atom (cache/ttl-cache-factory {} :ttl (* 1                ; min
                                            60               ; sec/min
                                            1000             ; ms/sec
                                            ))))

(def image-extensions
  "List of image file extensions to look for."
  #{"jpg" "jpeg"})

(def root-dir
  "Root directory of where gallery files are stored."
  "www/raw/gallery")

(def default-data
  "Default image data."
  {:priority 5})

(defn gallery-dir
  "Returns the directory for images in category."
  [category]
  (p/join root-dir (name category)))

(defmulti filename-data
  "Returns metadata from file name."
  :type)

(defmethod filename-data :default
  [file] nil)

(defn item-name
  [item]
  (cond
   (instance? java.io.File item) (.getName item)
   :default item))

(defn mm->cm
  "Convert mm (string) to cm."
  [s]
  (/ (Integer. s) 10))

(defn bookcase-dimensions
  "Gets dimensions of an item based on Eric's naming
  convention. Split by '.' and map the first three segments that
  are digits only to length, height, and depth converting from mm to cm."
  [file]
  (let [parts (-> file
                  item-name
                  (s/split #"\."))
        numbers (->> parts
                     (filter #(re-matches #"\d+" %))
                     (map mm->cm))]
    (zipmap [:width :height :depth] numbers)))
#_ (bookcase-dimensions "130218sm.2400.1500.0350.gradi.dbl.b.w.jpeg")

(defmethod filename-data :bookcase
  [{:keys [file]}]
  {:params (bookcase-dimensions file)})

;; Format:  dateauthor.type.width-mm.depth-mm.color-name.(color-ral|V).quantity
;;   color-ral implies laquer
;;   V - valchromat
;; Examples:
;;   141103SM.Shelf.2000.0450.Wh.9010.5.jpg       # laquer ral 9010
;;   141102SM.Shelf.2000.0450.LtGrey.V.5.jpg      # valchromat
(defmethod filename-data :shelf
  [{:keys [file]}]
  (let [parts (-> file
                  item-name
                  (s/split #"\."))]
    {:params {:width (mm->cm (parts 2))
              :depth (mm->cm (parts 3))
;;              :finish
              :quantity (parts 6)}}))
#_ (filename-data {:file "141103SM.Shelf.2000.0450.Wh.9010.5.jpg"
                   :type :shelf})
;; ? How to specify finish?
;; ? How to specify color?
;; ? How to validate the color and finish etc?

(defn image-map
  "Returns a map of image data for an image file."
  [file default-data]
  (let [m (merge {:file file} default-data)
        m (merge m (filename-data m))
        image-data (edn/slurp (p/new-path (str file ".edn")))]
    (map/deep-merge m image-data)))

(defn compare-priority
  "Sort with higher priorities first."
  [a b]
  (compare (:priority b) (:priority a)))

(defn get-images*
  "Returns a seq of images for category."
  [category & {:keys [compare-fn]}]
  (let [dir (-> category name gallery-dir)
        default-data (edn/slurp (p/join dir "default.edn"))
        images (filter #(image-extensions (p/extension %))
                       (p/files dir))
        images (map #(image-map % default-data) images)]
    (->> images
         (sort (or compare-fn compare-priority)))))
#_ (get-images* :bookshelves)

(defn get-images
  "Returns a cached seq of images for category."
  [category & opts]
  (let [C @image-cache
        C (if (cache/has? C category)
            (cache/hit C category)
            (swap! image-cache #(cache/miss % category (apply get-images* category opts))))]
    (clojure.core/get C category)))

(defn furniture-volume
  "Calculates volume of a furntiure based on Eric's naming
  convention. Split by '.' and multiply the first three segments that
  are digits only."
  [{:keys [file] :as item}]
  (-> file
      item-name
      (s/split #"\.")
      (->> (filter #(re-matches #"\d+" %))
           (map #(Integer. %))
           (apply *))))
#_ (furniture-volume {:file (java.io.File. "130218sm.2400.1500.0350.gradi.dbl.b.w.jpeg")})
;; width, height, depth in mm

(defn compare-volume
  "Sort with lower volumes first."
  [a b]
  (compare (furniture-volume a) (furniture-volume b)))
