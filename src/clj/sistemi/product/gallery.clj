(ns sistemi.product.gallery
  (:require [util.path :as p]
            [clojure.string :as s]
            [util.edn :as edn]
            [clojure.core.cache :as cache]))

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

(defn furniture-dimensions
  "Gets dimensions of an item based on Eric's naming
  convention. Split by '.' and map the first three segments that
  are digits only to length, height, and depth converting from mm to cm."
  [file]
  (prn "file" file)
  (let [parts (-> file
                  item-name
                  (s/split #"\."))
        numbers (->> parts
                     (filter #(re-matches #"\d+" %))
                     ;; convert from mm to cm
                     (map #(/ (Integer. %) 10)))]
    (zipmap [:width :height :depth] numbers)))

#_ (furniture-dimensions "130218sm.2400.1500.0350.gradi.dbl.b.w.jpeg")

(defmethod filename-data :bookshelf
  [{:keys [file]}]
  {:params (furniture-dimensions file)})

(defn image-map
  "Returns a map of image data for an image file."
  [file default-data]
  (let [m (merge {:file file} default-data)
        m (merge m (filename-data m))
        image-data (edn/slurp (p/new-path (str file ".edn")))]
    (merge m image-data)))

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
#_ (get-images* :sofas)

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
