(ns sistemi.site.order.payment-test
  (:require [sistemi.config :as cf]
            [util.frinj :as fj]
            [clj-stripe.common :as common]
            [clj-stripe.cards :as cards])
  (:use clojure.test
        sistemi.site.order.payment))

(defn config-fixture
  "Initiaizes the configuration system."
  [f]
  (cf/init!)
  (f))

(use-fixtures :once config-fixture)

(deftest key-not-nil
  (is (get-key)))

(deftest test-->cents
  (is (= (->cents 13.47M) 1347)))

(deftest test-->amount
  (is (= (->amount (fj/fj-eur 45.99M))
         {"amount" 4599 "currency" "EUR"})))

(deftest cart-description-test
  (is (= (cart-description {:cart {:items [{:type :shelf :quantity 2}]}}) "shelf(2)")))

(deftest ^{:online true} fail-bad-token
  (let [response (charge-card
                  (:stripe-token "badtoken")
                  (fj/fj-eur 45.99M)
                  "shelf(1)")]
    (is "invalid_request_error" (-> response :error :type))))

;; (deftest fail-cant-connect
;;   (binding [clj-stripe.common/*api-root* "https://127.0.0.1:666/dontexist"]
;;     (let [response (charge-card
;;                     (:stripe-token "badtoken")
;;                     (fj/fj-eur 45.99M)
;;                     "shelf(1)")]
;;       (is (-> response class (isa? Exception))))))

(def card
  "Test credit card."
  (common/card
   (common/number "4242424242424242")
   (common/expiration "05" "18")
   (common/cvc "123")))

(defn create-card-token
  []
  (common/with-token (get-key)
    (let [response (common/execute (cards/create-card-token card))]
      (:id response)
       )))

(deftest ^{:online true} test-charge-card
  (common/with-token (get-key)
     (let [card-token (create-card-token)
           {:keys [object failure_code] :as response} (charge-card
                        card-token
                        (fj/fj-eur 45.99M)
                        "shelf(1)")]
       (is (= object "charge"))
       (is (nil? failure_code)))))
