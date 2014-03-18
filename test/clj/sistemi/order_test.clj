(ns sistemi.order-test
  (:require [sistemi.order :as order]
            [sistemi.datomic :as d]
            [util.edn :as edn]
            [util.frinj :as f])
  (:use clojure.test))

(defn db-fixture
  "Initiaizes the datomic dev db."
  [f]
  (d/init-db)
  (f))

(use-fixtures :once db-fixture)

(def cart-data
  "Sample cart data to convert to an order."
  {:cart {:price {:total #frinj.core.fjv{:v 157.00M, :u {:EUR 1}}},
          :items #ordered/map ([0 {:price {:workbook "shelf/shelf-chain-france.xls", :total #frinj.core.fjv{:v 157.00M, :u {:EUR 1}},
                                           :unit #frinj.core.fjv{:v 157.00M, :u {:EUR 1}},
                                           :parts {:fabrication-stephane #frinj.core.fjv{:v 47.86M, :u {:EUR 1}},
                                                   :finishing-marques #frinj.core.fjv{:v 45.36M, :u {:EUR 1}},
                                                   :packaging-box #frinj.core.fjv{:v 16.32M, :u {:EUR 1}},
                                                   :subtotal #frinj.core.fjv{:v 109.54M, :u {:EUR 1}},
                                                   :margin #frinj.core.fjv{:v 21.91M, :u {:EUR 1}},
                                                   :tax #frinj.core.fjv{:v 25.76M, :u {:EUR 1}},
                                                   :adjustment #frinj.core.fjv{:v -0.21M, :u {:EUR 1}}}},
                                   :id 0,
                                   :type :shelf,
                                   :color {:rgb "#C51D34", :type :ral, :code 3027},
                                   :quantity 1, :finish :laquer-matte, :width 120, :depth 30}]), :counter 0, :status :cart, :taxable true}
   :shipping {:region "MI", :code "49091", :city "Sturgis", :address2 "", :address1 "23950 Butternut", :name "Jonathan Millett", :country "USA"},
   :contact {:email "jon@millett.net", :phone "7862068250", :name "Jonathan Millett"}})

(deftest create-order
  (let [order-id (order/create cart-data)
        order (order/lookup order-id)]
    (is order-id)
    (is order)
    ;;(prn "ORDER" order)
    (is (= (-> order :order/total) (-> cart-data :cart :price :total)))
    ;;(prn "TOTAL" (-> order :order/total))
    (is (-> order :order/status))
    (is (-> order :order/items))
    (is (-> order :order/contact))
    (is (-> order :order/shipping-address))
    ))
