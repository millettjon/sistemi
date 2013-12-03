(ns sistemi.format
  (:require color 
            [sistemi.translate :as tr])
  (:use [frinj ops]
        util.frinj))

;; WTF? joda money has the wrong format for france... the € should be
;; a postfix not a prefix
;; java appears to do it right http://stackoverflow.com/questions/9777689/how-to-get-numberformat-instance-from-currency-code
;; Note: this is locale dependent
;; Note: the use of comma, period, and space can differ by locale. 
(defn eur
  "Formats a number as euro currency to the given number of decimal places (2 by default)."
  ([v] (eur v 2))
  ([v places]
     (format (str "%." places "f€")
             (-> v
                 (fj-round places)
                 ;; (to :EUR)
                 :v
                 bigdec))))
(defn eur-short
  [v]
  (eur v 0))

(defn cm
  [v]
  (let [v (if (instance? frinj.core.fjv v)
            (:v (frinj.ops/to v :cm))
            v)]
    (str v " cm")))

;; TODO: move to product namespace
(def parameter-orders
  "display order of design paramters"
  {:shelf [:width :depth :finish :color]
   :bookcase [:width :height :depth :cutout :finish :color]})

;; TODO: move to product namespace
(def parameter-formats
  "Formatting functions for parameters."
  {:bookcase {:height #(cm %)
              :width #(cm %)
              :depth #(cm %)
              :cutout #(tr/translate "/product" :bookcase :cutout %)
              :color #(color/format-html %)
              :finish #(tr/translate "/product" :bookcase :finish %)
              }
   :shelf {:width #(cm %)
           :depth #(cm %)
           :color #(color/format-html %)
           :finish #(tr/translate "/product" :shelf :finish %)
           }})

(defn translate-param
  [item param]
  (tr/translate "/product" (:type item) param))

(defn format-value
  [item param]
  (let [value (param item)]
    (if-let [format-fn (get-in parameter-formats [(:type item) param])]
      (format-fn value)
      value)))
