(ns color.valchromat
  "Valchromat colors.")

;; Official list of colors - http://www.valchromat.pt/gama.aspx?menuid=963&eid=3058

(def palette-raw
  (map #(assoc % :type :valchromat)
       [{:name :light-grey :code :SLG}
        {:name :grey :code :SCZ}
        {:name :black :code :SBL}
        {:name :chocolate-brown :code :SCB}
        {:name :brown :code :SBR}
        {:name :red :code :SSC}
        {:name :orange :code :SOR}
        {:name :yellow :code :SYW}
        {:name :green :code :SGR}
        {:name :blue :code :SRB}
        {:name :violet :code :SVI}]))

(def palette-oiled
  (map #(assoc % :finish :oiled) palette-raw))
