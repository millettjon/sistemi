(ns ship-test
  (:require ship)
  (:use clojure.test))

;; ? What does the order look like?
;; ? Where is the order schema defined?
;; ? How does schema play with datomic?

;; {:cart
;;  {:price {:total #frinj.core.fjv{:v 751.00M, :u {:EUR 1}}},
;;   :items #ordered/map ([0 {:depth 30,
;;                            :width 120,
;;                            :finish :laquer-matte,
;;                            :quantity 1,
;;                            :color {:code 3027, :type :ral, :rgb "#C51D34"},
;;                            :type :bookcase,
;;                            :cutout :semplice,
;;                            :price {:workbook "bookcase/bookcase-chain-france.xls",
;;                                    :total #frinj.core.fjv{:v 751.00M, :u {:EUR 1}},
;;                                    :unit #frinj.core.fjv{:v 751.00M, :u {:EUR 1}},
;;                                    :parts {:fabrication-stephane #frinj.core.fjv{:v 120.70M, :u {:EUR 1}},
;;                                            :finishing-marques #frinj.core.fjv{:v 299.38M, :u {:EUR 1}},
;;                                            :packaging-box #frinj.core.fjv{:v 61.64M, :u {:EUR 1}},
;;                                            :subtotal #frinj.core.fjv{:v 481.71M, :u {:EUR 1}},
;;                                            :margin #frinj.core.fjv{:v 144.51M, :u {:EUR 1}},
;;                                            :tax #frinj.core.fjv{:v 125.25M, :u {:EUR 1}},
;;                                            :adjustment #frinj.core.fjv{:v -0.48M, :u {:EUR 1}}}},
;;                            :id 0,
;;                            :height 120}]),
;;   :counter 0,
;;   :status :cart,
;;   :taxable true}}


(deftest estimate*
  (let [order {}
        estimate (ship/estimate* order)]
    (prn "ESTIMATE" estimate))
  (is true ))
