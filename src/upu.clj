(ns upu
  "Format international addresses according to Universal Postal Union (UPU) recommendations.
   International Address Formats: http://bitboost.com/ref/international-address-formats.html
   UPU Home: http://www.upu.int/
   Country Codes: http://www.nationsonline.org/oneworld/country_code_list.htm"
  (require [clojure.string :as str]))

(defn format
  "Formats an address by its country."
  [m]
  ((ns-resolve 'upu
               (symbol (name (:country m)))) m))

(defn FR
  "Formats an address for France.
See: http://bitboost.com/ref/international-address-formats/france/
See: http://www.laposte.fr/courrierinternational/index.php?id=407"
  [m]
  [(:name m)
   (str/upper-case (:street m))
   (str/upper-case (str (:code m) " " (:city m)))
   "FRANCE"])

(defn GB
  "Formats an address for the United Kingdom.
See: http://bitboost.com/ref/international-address-formats/united-kingdom/"
  [m]
  (filter identity
          [(:name m)
           (:street m)
           (str/upper-case (:city m))
           (if-let [s (:county m)] (str/upper-case s))  ;; optional
           (str/upper-case (:code m))
           "UNITED KINGDOM"]))

#_ (format {:name "Jonathan Millett"
            :street "1 Main Terrace"
            :city "Wolverhampton"
            :county "West Midlands"
            :code "W12 4LQ"
            :country "GB"})

#_(GB {:name "Jonathan Millett"
       :street "1 Main Terrace"
       :city "Wolverhampton, West Midlands "
       :code "W12 4LQ"
       })

#_(FR {:name "jonathan millett"
     :street "23950 Butternut"
     :code "49091"
     :city "Sturgis"})
