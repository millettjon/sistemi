(ns sistemi.form)

;; TODO: Merge this with datomic model?
;; TODO: In what namespace should the select keywords be qualified? sistemi.model?
(def shelving
  "The design parameters for a shelving unit."
  {:width {:type :bounded-number :units "cm" :min 64 :max 240 :default 120}
   :height {:type :bounded-number :units "cm" :min 60 :max 240 :default 120}
   :depth {:type :bounded-number :units "cm" :min 20 :max 39 :default 30}
   :cutout {:type :set :options [::semplice ::ovale ::quadro] :default ::semplice}
   :finish {:type :set :options [::matte ::satin {:disabled true} ::glossy {:disabled true}] :default ::matte}
   :color {:type :rgb :default "#AB003B"}})

(def feedback
  "A customer feedback message."
  {:message {:type :string :max 4096 :default ""}})

(def cart-item-id
  "Id of a shopping cart item."
  {:id {:type :bounded-number :min 0 :max 100}})

(def cart-item-quantity
  "Generic item and quantity in a shopping cart."
  (merge cart-item-id
         {:quantity {:type :bounded-number :min 0 :max 100 :default 1}}))

(def cart-item
  "Generic item, type, and quantity in a shopping cart."
  (merge-with merge cart-item-quantity
         {:type     {:type :set :options [:shelf :shelving]}
          :id       {:min -1 :default -1}})) ;; -1 means assign on add

;; List of all products and their design parameters.
(def items
  {:shelf {:width {:type :bounded-number :units "cm" :min 64 :max 240 :default 120}
           :depth {:type :bounded-number :units "cm" :min 20 :max 39 :default 30}
           :finish {:type :set :options [::matte ::satin {:disabled true} ::glossy {:disabled true}] :default ::matte}
           :color {:type :rgb :default "#AB003B"}}})

(def cart-items
  "List of all possible types of cart-items."
  {:shelf (merge cart-item {:type {:type :set :options [:shelf] :default :shelf}} (:shelf items))})
