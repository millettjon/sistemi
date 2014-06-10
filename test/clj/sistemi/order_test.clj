(ns sistemi.order-test
  (:require [sistemi.init]
            [sistemi.order :as o]
            [sistemi.datomic :as d]
            [util.edn :as edn]
            [clojure.pprint :as p])
  (:use clojure.test
        [frinj.ops :only [fj fj+ fj*]]
        [util.frinj :only [fj-eur fj-round fj-bd_]]))

(def cart-data
  "Sample cart data to convert to an order."
  {:cart {:shipping {:price {:total #frinj.core.fjv{:v 35.53M, :u {:EUR 1}}},
                     :boxes [{:Code "02", :Dimensions {:Width "39", :Height "10", :Length "120", :Code "CM"}, :PackageWeight {:Weight "4.96", :Code "KGS"}, :PackageServiceOptions {}}],
                     :address {:country :FR, :code "38410", :region "", :city "St. Martin d'Uriage", :address2 "", :address1 "130 route de la combette", :contact {:name "Jonathan Millett"}}},
          :contact {:name "Jonathan Millett", :phone "+34934021100", :email "jon@millett.net"},
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
                                                   :adjustment #frinj.core.fjv{:v 0.26M, :u {:EUR 1}}}}
                                   :quantity 1,
                                   :depth 30,
                                   :id 0,
                                   :finish :laquer-matte,
                                   :type :shelf,
                                   :width 120,
                                   :color {:rgb "#C51D34", :type :ral, :code 3027}
                                   }]),

          :counter 0,
          :status :cart,
          :taxable? true
          }
   })

#_ (o/create cart-data {:locale "fr" :payment-txn "<stripe details>"})

(deftest create-order
  (let [order-id (:id (o/create cart-data {:locale "fr" :payment-txn "<payment transaction details>"}))
        order (o/lookup order-id)
        ]
    (is order-id)
    (is order)
    (is (= (-> order :price :total) (-> cart-data :cart :price :total)))
    (is (-> order :status))
    (is (-> order :items))
    (is (-> order :contact))
    (is (-> order :shipping))
    ))

(defn- check-fudge
  [total]
  (let [tax-rate      o/france-tax-rate
        total         (fj-eur total)
        desired-total (fj-round total 0)
        subtotal      (fj-bd_ total (+ 1 tax-rate) 2)
        fudge         (o/fudge desired-total subtotal tax-rate)]
    (fj-round (fj* (fj+ subtotal fudge) (+ 1 tax-rate)) 2)))

;; Property based testing would be interesting here.
(deftest fudge
  (are [total desired-total] (= (check-fudge total) (-> desired-total fj-eur (fj-round 2)))
       10 10
       13 13
       21.3 21
       88.7 89
       1.29 1
       57.99 58
       58.01 58
       17.51 18
       137.49 137
       137.50 138
       137.51 138
       138.50 138))
