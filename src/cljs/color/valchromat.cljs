(ns color.valchromat
  "Valchromat colors."
  (:require color
            edn
            [translate :as tr]))

;; Official list of colors - http://www.valchromat.pt/gama.aspx?menuid=963&eid=3058

(let [raw (map #(assoc % :type :valchromat)
               [{:name :light-grey :code :SLG :rgb "#8F8F8F"}
                {:name :grey :code :SCZ :rgb "#5A5A5A"}
                {:name :black :code :SBL :rgb "#595853"}
                {:name :chocolate-brown :code :SCB :rgb "#5C4136"}
                {:name :brown :code :SBR :rgb "#9F7769"}
                {:name :red :code :SSC :rgb "#CF4E61"}
                {:name :orange :code :SOR :rgb "#DF784D"}
                {:name :yellow :code :SYW :rgb "#CBA047"}
                {:name :green :code :SGR :rgb "#549185"}
                {:name :blue :code :SRB :rgb "#4E657E"}
                {:name :violet :code :SVI :rgb "#654E6A"}])

      rgb-oiled {:chocolate-brown "#2D201A"
                 :yellow "#A7742F"
                 :red    "#A02E3E"
                 :violet "#47344A"
                 :orange "#AB522D"
                 :green "#192C28"
                 :grey "#2C2C2C"
                 :brown "#694133"
                 :blue "#1A232B"
                 :light-grey "#656565"
                 :black "#151516"}

      oiled (map #(assoc %
                    :finish :oiled
                    :rgb (-> :name % rgb-oiled)) raw)]

  (def palettes
    {:raw   {:colors raw
             :textures-src "/pie-picker/valchromat-raw-palette-64.jpg"
             :label :valchromat}

     :oiled {:colors oiled
             :textures-src "/pie-picker/valchromat-oiled-palette-64.jpg"
             :label :valchromat-oiled}}))

(def palette-strings
  {:en {:valchromat "Valchromat"
        :valchromat-oiled "Oiled Valchromat"}

   :fr {:valchromat "Valchromat"
        :valchromat-oiled "Valchromat Huilé"}})

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

(defmethod edn/keywordize-vals :valchromat
  [m]
  (reduce (fn [m k] (if-let [v (m k)]
                     (assoc m k (keyword v))
                     m))
          m
          [:finish :code :name]))

(def color-strings
  {:en {:light-grey "light grey"
        :grey "grey"
        :black "black"
        :chocolate-brown "chocolate brown"
        :brown "brown"
        :red "red"
        :orange "orange"
        :yellow "yellow"
        :green "green"
        :blue "blue"
        :violet "violet"}

   :fr {:light-grey "gris clair"
        :grey "gris"
        :black "noir"
        :chocolate-brown "marron chocolat"
        :brown "marron"
        :red "rouge"
        :orange "orange"
        :yellow "jaune"
        :green "vert"
        :blue "bleu"
        :violet "violet"}})

(defmethod color/format-name :valchromat
  [{:keys [name]}]
  (tr/translate color-strings name))
