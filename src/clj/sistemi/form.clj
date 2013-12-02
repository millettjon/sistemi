(ns sistemi.form
  (:require [sistemi.format :as fmt]
            [color.ral :as ral]))

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

;; TODO: Move to product.shelf ns.
(def shelf-params
  {:width {:type :bounded-number :units "cm" :min 64 :max 240 :default 120 :format fmt/cm}
   :depth {:type :bounded-number :units "cm" :min 20 :max 39 :default 30 :format fmt/cm}
   :finish {:type :set :options [:laquer-matte :laquer-satin :laquer-glossy {:disabled true} :valchromat-raw :valchromat-oiled] :default :laquer-matte
             :format (get-in fmt/parameter-formats [:shelf :finish])}
   :color {:type :color :default (ral/get-color 3027)}
   })

;; TODO: Should this go somewhere else? sistemi.product.catalog?
(def items
  "Map of all items and their design paramters."
  {:shelf shelf-params
   ;; TODO: factor out as own def? move to product.bookcase ns?
   :shelving (merge shelf-params
                    {:height {:type :bounded-number :units "cm" :min 60 :max 240 :default 120 :format fmt/cm}
                     :cutout {:type :set :options [:semplice :ovale :quadro] :default :semplice
                              :format (get-in fmt/parameter-formats [:shelving :cutout])}})})

(def cart-item
  "Generic item, type, and quantity in a shopping cart."
  (merge-with merge cart-item-quantity
         {:type     {:type :set :options (keys items)}
          :id       {:min -1 :default -1}})) ;; -1 means assign on add

(def cart-items
  "List of params for all possible types of cart-items."
  (reduce (fn [m [k v]] (assoc m k (merge cart-item v)))
          {} items))
