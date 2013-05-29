(ns color.valchromat
  "Valchromate colors.")

;; Official list of colors - http://www.valchromat.pt/gama.aspx?menuid=963&eid=3058

;; ? where can i get samples from for rendering?
;; - http://www.privatelabel.co.za/valcromat/html/downloads/Valchromat-Colour-Chart.pdf
;; - http://www.polytecdesign.com.au/architectural/valchromat/
;;   - large samples

(def palette-raw
  (map #(assoc % :type :valchromat)
       [{:name :light-grey :code :SLG}
        {:name :grey :code :SCZ}
        {:name :black :code :SBL}
        {:name :chocolate-brown :code :SCB}
        {:name :brown :code :SBR}
        {:name :yellow :code :SYW}
        {:name :orange :code :SOR}
        {:name :red :code :SSC}
        {:name :violet :code :SVI}
        {:name :blue :code :SRB}
        {:name :green :code :SGR}]))

(def palette-oil
  (map #(assoc % :finish :oil) palette-raw))
