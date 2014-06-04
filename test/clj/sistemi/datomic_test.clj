(ns sistemi.datomic-test
  (:require [sistemi init]
            [sistemi.datomic :as sd]
            [frinj.ops :as f])
  (:use clojure.test))

(deftest tid
  (sd/tid -1))

(deftest attribute
  (is (sd/attribute :order/status))
  (is (nil? (sd/attribute :order/foo))))

#_ (deftest qualify1
  (is (= (sd/qualify1 {:city "Sturgis" :country :US} :address)
         {:address/city "Sturgis" :address/country :US}))

  ;; Don't qualify already qualified keywords.
  (is (= (sd/qualify1 {:contact/name "JTDawgzone"} :address)
         {:contact/name "JTDawgzone"}))

  ;; Don't qualify submap.
  (is (= (sd/qualify1 {:contact {:name "JTDawgzone"}} :address)
         {:address/contact {:name "JTDawgzone"}})))

#_ (deftest qualify-children
  (are [x y] (= (sd/qualify-children x) y)
       3 3
       {:foo "FOO"} {:foo "FOO"} 
       {:foo {:bar "BAR"}} {:foo {:foo/bar "BAR"}}))

(deftest qualify
  (are [x y z] (= (sd/qualify x y) z)
       ;; basic tests
       {} :foo {}
       {} :order {}
       
       ;; non-attributes are not qualified
       {:foo "FOO"} :order {:foo "FOO"}

       ;; non-components are not recursed
       {:items {:foo "FOO"}} :order {:order/items {:foo "FOO"}}

       ;; components are recursed
       {:payment {:transaction "fubar"}} :order {:order/payment {:payment/transaction "fubar"}}

       ;; deeper nesting
       {:shipping {:address {:city "Sturgis" :country :US}} :price {:total 43}} :order
       {:order/shipping {:shipping/address {:address/city "Sturgis" :address/country :US}} :order/price {:price/total 43}}
       ))

(deftest pack
  (are [x y] (= (into #{} (sd/pack x)) y)

       ;; one simple attribute
       {:order/status :delivered}
       #{{:order/status :delivered, :db/id (sd/tid -1)}}

       ;; one edn attribute
       {:price/total (f/fj 1 :USD)}
       #{{:price/total (-> (f/fj 1 :USD) pr-str), :db/id (sd/tid -1)}}

       ;; two attributes
       {:order/status :delivered :order/taxable? true}
       #{{:order/status :delivered, :order/taxable? true :db/id (sd/tid -1)}}

       ;; one component
       {:order/payment {:payment/transaction "xyzzy"}}
       #{{:order/payment #db/id[:db.part/user -2], :db/id #db/id[:db.part/user -1]}
         {:payment/transaction "\"xyzzy\"", :db/id #db/id[:db.part/user -2]}}

       ;; deeply nested components
       {:order/shipping {:shipping/address {:address/contact {:contact/name "JTDawgzone"}}}}
       #{{:order/shipping #db/id[:db.part/user -2], :db/id #db/id[:db.part/user -1]}
         {:shipping/address #db/id[:db.part/user -3], :db/id #db/id[:db.part/user -2]}
         {:address/contact #db/id[:db.part/user -4], :db/id #db/id[:db.part/user -3]}
         {:contact/name "JTDawgzone", :db/id #db/id[:db.part/user -4]}}
       ))
