(ns sistemi.order-test
  (:require [sistemi.order :as order]
            [sistemi.datomic :as d]
            [util.edn :as edn]
            [util.frinj :as f]
            [clojure.pprint :as p])
  (:use clojure.test))

(defn db-fixture
  "Initiaizes the datomic dev db."
  [f]
  (d/init-db)
  (f))

(use-fixtures :once db-fixture)

(def cart-data
  "Sample cart data to convert to an order."
  {:cart {:shipping {:price {:total #frinj.core.fjv{:v 35.53M, :u {:EUR 1}}},
                     :boxes '({:Code "02", :Dimensions {:Width "39", :Height "10", :Length "120", :Code "CM"}, :PackageWeight {:Weight "4.96", :Code "KGS"}, :PackageServiceOptions {}}),
                     :address {:country :FR, :code "38410", :region "", :city "St. Martin d'Uriage", :address2 "", :address1 "130 route de la combette", :name "Jonathan Millett"}},
          :contact {:email "jon@millett.net", :name "Jonathan Millett"},
          :price {:sub-total #frinj.core.fjv{:v 131.66666666666669, :u {:EUR 1}},
                  :tax #frinj.core.fjv{:v 26.33333333333334, :u {:EUR 1}},
                  :total #frinj.core.fjv{:v 193.53M, :u {:EUR 1}}},
          :items #ordered/map ([0 {:price {:workbook "shelf/shelf-chain-france.xls",
                                           :total #frinj.core.fjv{:v 158.00M, :u {:EUR 1}},
                                           :unit #frinj.core.fjv{:v 158.00M, :u {:EUR 1}},
                                           :parts {:fabrication-stephane #frinj.core.fjv{:v 47.86M, :u {:EUR 1}},
                                                   :finishing-marques #frinj.core.fjv{:v 45.36M, :u {:EUR 1}},
                                                   :packaging-box #frinj.core.fjv{:v 16.32M, :u {:EUR 1}},
                                                   :subtotal #frinj.core.fjv{:v 109.54M, :u {:EUR 1}},
                                                   :margin #frinj.core.fjv{:v 21.91M, :u {:EUR 1}},
                                                   :tax #frinj.core.fjv{:v 26.29M, :u {:EUR 1}},
                                                   :adjustment #frinj.core.fjv{:v 0.26M, :u {:EUR 1}}}},
                                   :id 0,
                                   :type :shelf,
                                   :color {:rgb "#C51D34", :type :ral, :code 3027},
                                   :quantity 1,
                                   :finish :laquer-matte,
                                   :width 120, :depth 30}]),
          :counter 0,
          :status :cart,
          :taxable? true}})

(deftest create-order
  (let [order-id (order/create cart-data {:stripe "<payment transaction details>"})
        order (order/lookup order-id)]
    (is order-id)
    (is order)
    (prn "----------------------------------------")
    (p/pprint order)
    (is (= (-> order :order/total) (-> cart-data :cart :price :total)))
    ;;(prn "TOTAL" (-> order :order/total))

    (is (-> order :order/status))
    (is (-> order :order/items))
    (is (-> order :order/contact))
    (is (-> order :order/shipping))
    ))
