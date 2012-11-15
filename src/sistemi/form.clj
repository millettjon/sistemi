(ns sistemi.form)

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

;; List of all products and their design parameters.
(def shelf-params
  {:width {:type :bounded-number :units "cm" :min 64 :max 240 :default 120}
   :depth {:type :bounded-number :units "cm" :min 20 :max 39 :default 30}
   :finish {:type :set :options [:matte :satin {:disabled true} :glossy {:disabled true}] :default :matte}
   :color {:type :rgb :default "#AB003B"}})

(def items
  {:shelf shelf-params
   :shelving (merge shelf-params
                    {:height {:type :bounded-number :units "cm" :min 60 :max 240 :default 120}
                     :cutout {:type :set :options [:semplice :ovale :quadro] :default :semplice}})
   })

(def cart-item
  "Generic item, type, and quantity in a shopping cart."
  (merge-with merge cart-item-quantity
         {:type     {:type :set :options (keys items)}
          :id       {:min -1 :default -1}})) ;; -1 means assign on add

(def cart-items
  "List of params for all possible types of cart-items."
  (reduce (fn [m [k v]] (assoc m k (merge cart-item v)))
          {} items))
