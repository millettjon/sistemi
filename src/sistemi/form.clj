(ns sistemi.form)

;; TODO: Merge this with datomic model?
;; TODO: In what namespace should the select keywords be qualified? sistemi.model?
(def shelving
  {:width {:type :bounded-number :units "cm" :min 64 :max 240 :default 120}
   :height {:type :bounded-number :units "cm" :min 60 :max 240 :default 120}
   :depth {:type :bounded-number :units "cm" :min 20 :max 39 :default 30}
   :cutout {:type :set :options [::semplice ::ovale ::quadro] :default ::semplice}
   :finish {:type :set :options [::matte ::satin {:disabled true} ::glossy {:disabled true}] :default ::matte}
   :color {:type :rgb :default "#AB003B"}})
