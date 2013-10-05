(ns color.ral
  "RAL color palette.")

(def ^:export palette
  (map #(assoc % :type :ral)
       [{:code 1000, :rgb "#BEBD7F"}
        {:code 1001, :rgb "#C2B078"}
        {:code 1002, :rgb "#C6A664"}
        {:code 1003, :rgb "#E5BE01"}
        {:code 1004, :rgb "#CDA434"}
        {:code 1005, :rgb "#A98307"}
        {:code 1006, :rgb "#E4A010"}
        {:code 1007, :rgb "#DC9D00"}
        {:code 1011, :rgb "#8A6642"}
        {:code 1012, :rgb "#C7B446"}
        {:code 1013, :rgb "#EAE6CA"}
        {:code 1014, :rgb "#E1CC4F"}
        {:code 1015, :rgb "#E6D690"}
        {:code 1016, :rgb "#EDFF21"}
        {:code 1017, :rgb "#F5D033"}
        {:code 1018, :rgb "#F8F32B"}
        {:code 1019, :rgb "#9E9764"}
        {:code 1020, :rgb "#999950"}
        {:code 1021, :rgb "#F3DA0B"}
        {:code 1023, :rgb "#FAD201"}
        {:code 1024, :rgb "#AEA04B"}
        {:code 1026, :rgb "#FFFF00"}
        {:code 1027, :rgb "#9D9101"}
        {:code 1028, :rgb "#F4A900"}
        {:code 1032, :rgb "#D6AE01"}
        {:code 1033, :rgb "#F3A505"}
        {:code 1034, :rgb "#EFA94A"}
        {:code 1035, :rgb "#6A5D4D"}
        {:code 1036, :rgb "#705335"}
        {:code 1037, :rgb "#F39F18"}
        {:code 2000, :rgb "#ED760E"}
        {:code 2001, :rgb "#C93C20"}
        {:code 2002, :rgb "#CB2821"}
        {:code 2003, :rgb "#FF7514"}
        {:code 2004, :rgb "#F44611"}
        {:code 2005, :rgb "#FF2301"}
        {:code 2007, :rgb "#FFA420"}
        {:code 2008, :rgb "#F75E25"}
        {:code 2009, :rgb "#F54021"}
        {:code 2010, :rgb "#D84B20"}
        {:code 2011, :rgb "#EC7C26"}
        {:code 2012, :rgb "#E55137"}
        {:code 2013, :rgb "#C35831"}
        {:code 3000, :rgb "#AF2B1E"}
        {:code 3001, :rgb "#A52019"}
        {:code 3002, :rgb "#A2231D"}
        {:code 3003, :rgb "#9B111E"}
        {:code 3004, :rgb "#75151E"}
        {:code 3005, :rgb "#5E2129"}
        {:code 3007, :rgb "#412227"}
        {:code 3009, :rgb "#642424"}
        {:code 3011, :rgb "#781F19"}
        {:code 3012, :rgb "#C1876B"}
        {:code 3013, :rgb "#A12312"}
        {:code 3014, :rgb "#D36E70"}
        {:code 3015, :rgb "#EA899A"}
        {:code 3016, :rgb "#B32821"}
        {:code 3017, :rgb "#E63244"}
        {:code 3018, :rgb "#D53032"}
        {:code 3020, :rgb "#CC0605"}
        {:code 3022, :rgb "#D95030"}
        {:code 3024, :rgb "#F80000"}
        {:code 3026, :rgb "#FE0000"}
        {:code 3027, :rgb "#C51D34"}
        {:code 3028, :rgb "#CB3234"}
        {:code 3031, :rgb "#B32428"}
        {:code 3032, :rgb "#721422"}
        {:code 3033, :rgb "#B44C43"}
        {:code 4001, :rgb "#6D3F5B"}
        {:code 4002, :rgb "#922B3E"}
        {:code 4003, :rgb "#DE4C8A"}
        {:code 4004, :rgb "#641C34"}
        {:code 4005, :rgb "#6C4675"}
        {:code 4006, :rgb "#A03472"}
        {:code 4007, :rgb "#4A192C"}
        {:code 4008, :rgb "#924E7D"}
        {:code 4009, :rgb "#A18594"}
        {:code 4010, :rgb "#CF3476"}
        {:code 4011, :rgb "#8673A1"}
        {:code 4012, :rgb "#6C6874"}
        {:code 5000, :rgb "#354D73"}
        {:code 5001, :rgb "#1F3438"}
        {:code 5002, :rgb "#20214F"}
        {:code 5003, :rgb "#1D1E33"}
        {:code 5004, :rgb "#18171C"}
        {:code 5005, :rgb "#1E2460"}
        {:code 5007, :rgb "#3E5F8A"}
        {:code 5008, :rgb "#26252D"}
        {:code 5009, :rgb "#025669"}
        {:code 5010, :rgb "#0E294B"}
        {:code 5011, :rgb "#231A24"}
        {:code 5012, :rgb "#3B83BD"}
        {:code 5013, :rgb "#1E213D"}
        {:code 5014, :rgb "#606E8C"}
        {:code 5015, :rgb "#2271B3"}
        {:code 5017, :rgb "#063971"}
        {:code 5018, :rgb "#3F888F"}
        {:code 5019, :rgb "#1B5583"}
        {:code 5020, :rgb "#1D334A"}
        {:code 5021, :rgb "#256D7B"}
        {:code 5022, :rgb "#252850"}
        {:code 5023, :rgb "#49678D"}
        {:code 5024, :rgb "#5D9B9B"}
        {:code 5025, :rgb "#2A6478"}
        {:code 5026, :rgb "#102C54"}
        {:code 6000, :rgb "#316650"}
        {:code 6001, :rgb "#287233"}
        {:code 6002, :rgb "#2D572C"}
        {:code 6003, :rgb "#424632"}
        {:code 6004, :rgb "#1F3A3D"}
        {:code 6005, :rgb "#2F4538"}
        {:code 6006, :rgb "#3E3B32"}
        {:code 6007, :rgb "#343B29"}
        {:code 6008, :rgb "#39352A"}
        {:code 6009, :rgb "#31372B"}
        {:code 6010, :rgb "#35682D"}
        {:code 6011, :rgb "#587246"}
        {:code 6012, :rgb "#343E40"}
        {:code 6013, :rgb "#6C7156"}
        {:code 6014, :rgb "#47402E"}
        {:code 6015, :rgb "#3B3C36"}
        {:code 6016, :rgb "#1E5945"}
        {:code 6017, :rgb "#4C9141"}
        {:code 6018, :rgb "#57A639"}
        {:code 6019, :rgb "#BDECB6"}
        {:code 6020, :rgb "#2E3A23"}
        {:code 6021, :rgb "#89AC76"}
        {:code 6022, :rgb "#25221B"}
        {:code 6024, :rgb "#308446"}
        {:code 6025, :rgb "#3D642D"}
        {:code 6026, :rgb "#015D52"}
        {:code 6027, :rgb "#84C3BE"}
        {:code 6028, :rgb "#2C5545"}
        {:code 6029, :rgb "#20603D"}
        {:code 6032, :rgb "#317F43"}
        {:code 6033, :rgb "#497E76"}
        {:code 6034, :rgb "#7FB5B5"}
        {:code 6035, :rgb "#1C542D"}
        {:code 6036, :rgb "#193737"}
        {:code 6037, :rgb "#008F39"}
        {:code 6038, :rgb "#00BB2D"}
        {:code 7000, :rgb "#78858B"}
        {:code 7001, :rgb "#8A9597"}
        {:code 7002, :rgb "#7E7B52"}
        {:code 7003, :rgb "#6C7059"}
        {:code 7004, :rgb "#969992"}
        {:code 7005, :rgb "#646B63"}
        {:code 7006, :rgb "#6D6552"}
        {:code 7008, :rgb "#6A5F31"}
        {:code 7009, :rgb "#4D5645"}
        {:code 7010, :rgb "#4C514A"}
        {:code 7011, :rgb "#434B4D"}
        {:code 7012, :rgb "#4E5754"}
        {:code 7013, :rgb "#464531"}
        {:code 7015, :rgb "#434750"}
        {:code 7016, :rgb "#293133"}
        {:code 7021, :rgb "#23282B"}
        {:code 7022, :rgb "#332F2C"}
        {:code 7023, :rgb "#686C5E"}
        {:code 7024, :rgb "#474A51"}
        {:code 7026, :rgb "#2F353B"}
        {:code 7030, :rgb "#8B8C7A"}
        {:code 7031, :rgb "#474B4E"}
        {:code 7032, :rgb "#B8B799"}
        {:code 7033, :rgb "#7D8471"}
        {:code 7034, :rgb "#8F8B66"}
        {:code 7035, :rgb "#D7D7D7"}
        {:code 7036, :rgb "#7F7679"}
        {:code 7037, :rgb "#7D7F7D"}
        {:code 7038, :rgb "#B5B8B1"}
        {:code 7039, :rgb "#6C6960"}
        {:code 7040, :rgb "#9DA1AA"}
        {:code 7042, :rgb "#8D948D"}
        {:code 7043, :rgb "#4E5452"}
        {:code 7044, :rgb "#CAC4B0"}
        {:code 7045, :rgb "#909090"}
        {:code 7046, :rgb "#82898F"}
        {:code 7047, :rgb "#D0D0D0"}
        {:code 7048, :rgb "#898176"}
        {:code 8000, :rgb "#826C34"}
        {:code 8001, :rgb "#955F20"}
        {:code 8002, :rgb "#6C3B2A"}
        {:code 8003, :rgb "#734222"}
        {:code 8004, :rgb "#8E402A"}
        {:code 8007, :rgb "#59351F"}
        {:code 8008, :rgb "#6F4F28"}
        {:code 8011, :rgb "#5B3A29"}
        {:code 8012, :rgb "#592321"}
        {:code 8014, :rgb "#382C1E"}
        {:code 8015, :rgb "#633A34"}
        {:code 8016, :rgb "#4C2F27"}
        {:code 8017, :rgb "#45322E"}
        {:code 8019, :rgb "#403A3A"}
        {:code 8022, :rgb "#212121"}
        {:code 8023, :rgb "#A65E2E"}
        {:code 8024, :rgb "#79553D"}
        {:code 8025, :rgb "#755C48"}
        {:code 8028, :rgb "#4E3B31"}
        {:code 8029, :rgb "#763C28"}
        {:code 9001, :rgb "#FDF4E3"}
        {:code 9002, :rgb "#E7EBDA"}
        {:code 9003, :rgb "#F4F4F4"}
        {:code 9004, :rgb "#282828"}
        {:code 9005, :rgb "#0A0A0A"}
        {:code 9006, :rgb "#A5A5A5"}
        {:code 9007, :rgb "#8F8F8F"}
        {:code 9010, :rgb "#FFFFFF"}
        {:code 9011, :rgb "#1C1C1C"}
        {:code 9016, :rgb "#F6F6F6"}
        {:code 9017, :rgb "#1E1E1E"}
        {:code 9018, :rgb "#D7D7D7"}
        {:code 9022, :rgb "#9C9C9C"}
        {:code 9023, :rgb "#828282"}]))

(defn ^:private get-color
  "Lookup a color by RAL number."
  [ral]
  (->> palette
       (filter #(= ral (:code %)))
       first))

(def ^:export default-color
  (-> (get-color 3027)
      clj->js))
