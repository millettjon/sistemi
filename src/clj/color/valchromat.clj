(ns color.valchromat
  "Valchromat colors.")

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
             :label "Valchromat"}

     :oiled {:colors oiled
             :textures-src "/pie-picker/valchromat-oiled-palette-64.jpg"
             :label "Oiled Valchromat"}}))

(defn get-color
  "Gets a color object by code and finish"
  [code finish]
  (->> ((or finish :raw) palettes)
       :colors
       (filter #(= code (:code %)))
       first))

