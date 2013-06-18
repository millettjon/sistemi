(ns color.valchromat
  "Valchromat colors.")

;; Official list of colors - http://www.valchromat.pt/gama.aspx?menuid=963&eid=3058

(let [raw (map #(assoc % :type :valchromat)
               [{:name :light-grey :code :SLG :rgb [143 143 143]}
                {:name :grey :code :SCZ :rgb [90 90 90]}
                {:name :black :code :SBL :rgb [89 88 93]}
                {:name :chocolate-brown :code :SCB :rgb [92 65 54]}
                {:name :brown :code :SBR :rgb [159 119 105]}
                {:name :red :code :SSC :rgb [207 78 97]}
                {:name :orange :code :SOR :rgb [223 120 77]}
                {:name :yellow :code :SYW :rgb [203 160 71]}
                {:name :green :code :SGR :rgb [84 145 133]}
                {:name :blue :code :SRB :rgb [78 101 126]}
                {:name :violet :code :SVI :rgb [101 78 106]}])

      rgb-oiled {:chocolate-brown [45 32 26]
                 :yellow [167 116 47]
                 :red [160 46 62]
                 :violet [71 52 74]
                 :orange [171 82 45]
                 :green [25 44 40]
                 :grey [44 44 44]
                 :brown [105 65 51]
                 :blue [26 35 43]
                 :light-grey [101 101 101]
                 :black [21 21 22]}

      oiled (map #(assoc %
                    :finish :oiled
                    :rgb (-> :name % rgb-oiled)) raw)]

  (def palettes
    {:raw   {:colors raw
             :textures-src "/raw/pie-picker/valchromat-raw-palette-64.jpg"
             :label "Valchromat"}

     :oiled {:colors oiled
             :textures-src "/raw/pie-picker/valchromat-oiled-palette-64.jpg"
             :label "Oiled Valchromat"}}))

(defn ^:private get-by-name
  "Gets a color object by name."
  [palette name]
  (->> palette
       :colors
       (filter #(= name (:name %)))
       first))

(defn ^:export get-by-name-js
  "Gets a color object by name."
  [palette name]
  (-> palette
      keyword
      palettes
      (get-by-name (keyword name))
      clj->js))

(def ^:export default-raw-color
  (-> palettes
      :raw
      (get-by-name :red)
       clj->js))

(def ^:export default-oiled-color
  (-> palettes
      :oiled
      (get-by-name :red)
       clj->js))



