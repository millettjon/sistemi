(ns color.ral
  "RAL color palette.")

(def ^:export palette
  (map #(assoc % :type :ral)
       [{:ral 1000, :rgb "#BEBD7F"}
        {:ral 1001, :rgb "#C2B078"}
        {:ral 1002, :rgb "#C6A664"}
        {:ral 1003, :rgb "#E5BE01"}
        {:ral 1004, :rgb "#CDA434"}
        {:ral 1005, :rgb "#A98307"}
        {:ral 1006, :rgb "#E4A010"}
        {:ral 1007, :rgb "#DC9D00"}
        {:ral 1011, :rgb "#8A6642"}
        {:ral 1012, :rgb "#C7B446"}
        {:ral 1013, :rgb "#EAE6CA"}
        {:ral 1014, :rgb "#E1CC4F"}
        {:ral 1015, :rgb "#E6D690"}
        {:ral 1016, :rgb "#EDFF21"}
        {:ral 1017, :rgb "#F5D033"}
        {:ral 1018, :rgb "#F8F32B"}
        {:ral 1019, :rgb "#9E9764"}
        {:ral 1020, :rgb "#999950"}
        {:ral 1021, :rgb "#F3DA0B"}
        {:ral 1023, :rgb "#FAD201"}
        {:ral 1024, :rgb "#AEA04B"}
        {:ral 1026, :rgb "#FFFF00"}
        {:ral 1027, :rgb "#9D9101"}
        {:ral 1028, :rgb "#F4A900"}
        {:ral 1032, :rgb "#D6AE01"}
        {:ral 1033, :rgb "#F3A505"}
        {:ral 1034, :rgb "#EFA94A"}
        {:ral 1035, :rgb "#6A5D4D"}
        {:ral 1036, :rgb "#705335"}
        {:ral 1037, :rgb "#F39F18"}
        {:ral 2000, :rgb "#ED760E"}
        {:ral 2001, :rgb "#C93C20"}
        {:ral 2002, :rgb "#CB2821"}
        {:ral 2003, :rgb "#FF7514"}
        {:ral 2004, :rgb "#F44611"}
        {:ral 2005, :rgb "#FF2301"}
        {:ral 2007, :rgb "#FFA420"}
        {:ral 2008, :rgb "#F75E25"}
        {:ral 2009, :rgb "#F54021"}
        {:ral 2010, :rgb "#D84B20"}
        {:ral 2011, :rgb "#EC7C26"}
        {:ral 2012, :rgb "#E55137"}
        {:ral 2013, :rgb "#C35831"}
        {:ral 3000, :rgb "#AF2B1E"}
        {:ral 3001, :rgb "#A52019"}
        {:ral 3002, :rgb "#A2231D"}
        {:ral 3003, :rgb "#9B111E"}
        {:ral 3004, :rgb "#75151E"}
        {:ral 3005, :rgb "#5E2129"}
        {:ral 3007, :rgb "#412227"}
        {:ral 3009, :rgb "#642424"}
        {:ral 3011, :rgb "#781F19"}
        {:ral 3012, :rgb "#C1876B"}
        {:ral 3013, :rgb "#A12312"}
        {:ral 3014, :rgb "#D36E70"}
        {:ral 3015, :rgb "#EA899A"}
        {:ral 3016, :rgb "#B32821"}
        {:ral 3017, :rgb "#E63244"}
        {:ral 3018, :rgb "#D53032"}
        {:ral 3020, :rgb "#CC0605"}
        {:ral 3022, :rgb "#D95030"}
        {:ral 3024, :rgb "#F80000"}
        {:ral 3026, :rgb "#FE0000"}
        {:ral 3027, :rgb "#C51D34"}
        {:ral 3028, :rgb "#CB3234"}
        {:ral 3031, :rgb "#B32428"}
        {:ral 3032, :rgb "#721422"}
        {:ral 3033, :rgb "#B44C43"}
        {:ral 4001, :rgb "#6D3F5B"}
        {:ral 4002, :rgb "#922B3E"}
        {:ral 4003, :rgb "#DE4C8A"}
        {:ral 4004, :rgb "#641C34"}
        {:ral 4005, :rgb "#6C4675"}
        {:ral 4006, :rgb "#A03472"}
        {:ral 4007, :rgb "#4A192C"}
        {:ral 4008, :rgb "#924E7D"}
        {:ral 4009, :rgb "#A18594"}
        {:ral 4010, :rgb "#CF3476"}
        {:ral 4011, :rgb "#8673A1"}
        {:ral 4012, :rgb "#6C6874"}
        {:ral 5000, :rgb "#354D73"}
        {:ral 5001, :rgb "#1F3438"}
        {:ral 5002, :rgb "#20214F"}
        {:ral 5003, :rgb "#1D1E33"}
        {:ral 5004, :rgb "#18171C"}
        {:ral 5005, :rgb "#1E2460"}
        {:ral 5007, :rgb "#3E5F8A"}
        {:ral 5008, :rgb "#26252D"}
        {:ral 5009, :rgb "#025669"}
        {:ral 5010, :rgb "#0E294B"}
        {:ral 5011, :rgb "#231A24"}
        {:ral 5012, :rgb "#3B83BD"}
        {:ral 5013, :rgb "#1E213D"}
        {:ral 5014, :rgb "#606E8C"}
        {:ral 5015, :rgb "#2271B3"}
        {:ral 5017, :rgb "#063971"}
        {:ral 5018, :rgb "#3F888F"}
        {:ral 5019, :rgb "#1B5583"}
        {:ral 5020, :rgb "#1D334A"}
        {:ral 5021, :rgb "#256D7B"}
        {:ral 5022, :rgb "#252850"}
        {:ral 5023, :rgb "#49678D"}
        {:ral 5024, :rgb "#5D9B9B"}
        {:ral 5025, :rgb "#2A6478"}
        {:ral 5026, :rgb "#102C54"}
        {:ral 6000, :rgb "#316650"}
        {:ral 6001, :rgb "#287233"}
        {:ral 6002, :rgb "#2D572C"}
        {:ral 6003, :rgb "#424632"}
        {:ral 6004, :rgb "#1F3A3D"}
        {:ral 6005, :rgb "#2F4538"}
        {:ral 6006, :rgb "#3E3B32"}
        {:ral 6007, :rgb "#343B29"}
        {:ral 6008, :rgb "#39352A"}
        {:ral 6009, :rgb "#31372B"}
        {:ral 6010, :rgb "#35682D"}
        {:ral 6011, :rgb "#587246"}
        {:ral 6012, :rgb "#343E40"}
        {:ral 6013, :rgb "#6C7156"}
        {:ral 6014, :rgb "#47402E"}
        {:ral 6015, :rgb "#3B3C36"}
        {:ral 6016, :rgb "#1E5945"}
        {:ral 6017, :rgb "#4C9141"}
        {:ral 6018, :rgb "#57A639"}
        {:ral 6019, :rgb "#BDECB6"}
        {:ral 6020, :rgb "#2E3A23"}
        {:ral 6021, :rgb "#89AC76"}
        {:ral 6022, :rgb "#25221B"}
        {:ral 6024, :rgb "#308446"}
        {:ral 6025, :rgb "#3D642D"}
        {:ral 6026, :rgb "#015D52"}
        {:ral 6027, :rgb "#84C3BE"}
        {:ral 6028, :rgb "#2C5545"}
        {:ral 6029, :rgb "#20603D"}
        {:ral 6032, :rgb "#317F43"}
        {:ral 6033, :rgb "#497E76"}
        {:ral 6034, :rgb "#7FB5B5"}
        {:ral 6035, :rgb "#1C542D"}
        {:ral 6036, :rgb "#193737"}
        {:ral 6037, :rgb "#008F39"}
        {:ral 6038, :rgb "#00BB2D"}
        {:ral 7000, :rgb "#78858B"}
        {:ral 7001, :rgb "#8A9597"}
        {:ral 7002, :rgb "#7E7B52"}
        {:ral 7003, :rgb "#6C7059"}
        {:ral 7004, :rgb "#969992"}
        {:ral 7005, :rgb "#646B63"}
        {:ral 7006, :rgb "#6D6552"}
        {:ral 7008, :rgb "#6A5F31"}
        {:ral 7009, :rgb "#4D5645"}
        {:ral 7010, :rgb "#4C514A"}
        {:ral 7011, :rgb "#434B4D"}
        {:ral 7012, :rgb "#4E5754"}
        {:ral 7013, :rgb "#464531"}
        {:ral 7015, :rgb "#434750"}
        {:ral 7016, :rgb "#293133"}
        {:ral 7021, :rgb "#23282B"}
        {:ral 7022, :rgb "#332F2C"}
        {:ral 7023, :rgb "#686C5E"}
        {:ral 7024, :rgb "#474A51"}
        {:ral 7026, :rgb "#2F353B"}
        {:ral 7030, :rgb "#8B8C7A"}
        {:ral 7031, :rgb "#474B4E"}
        {:ral 7032, :rgb "#B8B799"}
        {:ral 7033, :rgb "#7D8471"}
        {:ral 7034, :rgb "#8F8B66"}
        {:ral 7035, :rgb "#D7D7D7"}
        {:ral 7036, :rgb "#7F7679"}
        {:ral 7037, :rgb "#7D7F7D"}
        {:ral 7038, :rgb "#B5B8B1"}
        {:ral 7039, :rgb "#6C6960"}
        {:ral 7040, :rgb "#9DA1AA"}
        {:ral 7042, :rgb "#8D948D"}
        {:ral 7043, :rgb "#4E5452"}
        {:ral 7044, :rgb "#CAC4B0"}
        {:ral 7045, :rgb "#909090"}
        {:ral 7046, :rgb "#82898F"}
        {:ral 7047, :rgb "#D0D0D0"}
        {:ral 7048, :rgb "#898176"}
        {:ral 8000, :rgb "#826C34"}
        {:ral 8001, :rgb "#955F20"}
        {:ral 8002, :rgb "#6C3B2A"}
        {:ral 8003, :rgb "#734222"}
        {:ral 8004, :rgb "#8E402A"}
        {:ral 8007, :rgb "#59351F"}
        {:ral 8008, :rgb "#6F4F28"}
        {:ral 8011, :rgb "#5B3A29"}
        {:ral 8012, :rgb "#592321"}
        {:ral 8014, :rgb "#382C1E"}
        {:ral 8015, :rgb "#633A34"}
        {:ral 8016, :rgb "#4C2F27"}
        {:ral 8017, :rgb "#45322E"}
        {:ral 8019, :rgb "#403A3A"}
        {:ral 8022, :rgb "#212121"}
        {:ral 8023, :rgb "#A65E2E"}
        {:ral 8024, :rgb "#79553D"}
        {:ral 8025, :rgb "#755C48"}
        {:ral 8028, :rgb "#4E3B31"}
        {:ral 8029, :rgb "#763C28"}
        {:ral 9001, :rgb "#FDF4E3"}
        {:ral 9002, :rgb "#E7EBDA"}
        {:ral 9003, :rgb "#F4F4F4"}
        {:ral 9004, :rgb "#282828"}
        {:ral 9005, :rgb "#0A0A0A"}
        {:ral 9006, :rgb "#A5A5A5"}
        {:ral 9007, :rgb "#8F8F8F"}
        {:ral 9010, :rgb "#FFFFFF"}
        {:ral 9011, :rgb "#1C1C1C"}
        {:ral 9016, :rgb "#F6F6F6"}
        {:ral 9017, :rgb "#1E1E1E"}
        {:ral 9018, :rgb "#D7D7D7"}
        {:ral 9022, :rgb "#9C9C9C"}
        {:ral 9023, :rgb "#828282"}]))

(defn ^:private get-color
  "Lookup a color by RAL number."
  [ral]
  (->> palette
       (filter #(= ral (:ral %)))
       first))

(def ^:export default-color
  (-> (get-color 3027)
      clj->js))







