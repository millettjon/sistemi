(ns www.cart
  "Shopping cart."
  (:require [clojure.contrib.core :as contrib]
            [clojure.tools.logging :as log]
            [www.session :as sess])
  (:use ordered.map)
  (:refer-clojure :exclude [get empty?]))

;; What does the data look like?
;; - should preserve order of addtion (for sorting on cart page)
{:items [{:id -1                  ; unique id for line item within the cart
          :item ::shelf         ; item type (must be a valid product id)
          :quantity 1

          ;; item customization attributes
          :attributes {:width 120 ; should this be a frinj #?
                       :depth 30
                       :finish :laquer-matte
                       :color "#AB003B"}}]
 :count 1  ;; counter that increments for each line item added to the cart
 }

(defn- new-cart
  "Returns a new empty shopping cart."
  []
  {:items (ordered-map)
   :counter -1})

(defn get
  "Gets the cart from the current request."
  [req]
  (get-in req [:session :cart]))

(defn empty?
  "Returns true if the cart is empty."
  [cart]
  (-> cart :items clojure.core/empty?))

(defn add
  "Updates an item in a cart. If cart is nil, a new empty cart will be
created. If the item id is -1, a new item will be added. Returns the
updated cart."
  [cart item]
  (let [cart (or cart (new-cart))
        new-item? (= -1 (:id item))
        counter (let [c (:counter cart)]
                  (if new-item? (inc c) c))
        item (if new-item? (assoc item :id counter) item)
        cart (if new-item? (assoc cart :counter counter) cart)]
    (assoc-in cart [:items (:id item)] item)))

(defn delete
  "Deletes an item with the given id from the cart."
  [cart id]
  (contrib/dissoc-in cart [:items id]))

(defn update
  "Updates the quantity for a cart item."
  [cart id quantity]
  (if (= quantity 0)
    (delete cart id)
    (assoc-in cart [:items id :quantity] quantity)))

;; TODO: is this more analogous to update-in?
;; TODO: find a better name that makes it clear there is a handoff from req to resp as well as an update
(defn swap
  "Reads the cart value from the session, applies it and the args to
function f and saves the result in the session. Analogous to
core.swap!."
  [resp req f & args]
  (apply sess/swap resp req :cart f args))

#_ (cart/swap resp req cart/add (f/values))
