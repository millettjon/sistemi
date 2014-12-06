(ns sistemi.product.gallery
  (:require [util.path :as p]
            [clojure.string :as s]
            [clojure.edn :as edn]
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

(defn image-map
  "Returns a map of image data for an image file."
  [file]
  (let [edn-file (p/new-path (str file ".edn"))
        edn-data (or (when (p/exists? edn-file)
                       (-> edn-file p/to-file slurp edn/read-string))
                     {})]
    (merge default-data edn-data {:file file})))

(defn compare-priority
  "Sort with higher priorities first."
  [a b]
  (compare (:priority b) (:priority a)))

(defn get-images*
  "Returns a seq of images for category."
  [category & {:keys [compare-fn]}]
  (let [images (filter #(image-extensions (p/extension %))
                       (-> category name gallery-dir p/files))
        images (map image-map images)
        ]
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

(defn item-name
  [item]
  (cond
   (instance? java.io.File item) (.getName item)
   :default item))

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

(defn compare-volume
  "Sort with lower volumes first."
  [a b]
  (compare (furniture-volume a) (furniture-volume b)))
