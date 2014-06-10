(ns mail.mandrill-test
  (:require [mail.mandrill :as m]
            [schema.core :as s]
            [sistemi.order :as o]
            [sistemi.order-test :as ot])
  (:use clojure.test))

(deftest schema
  (s/validate m/Message
              {:from {:email "finn@human.com" :name "Finn the Human"}
               :to [{:email "jake@dog.com" :name "Jake the Dog"} {:email "peppermint@butler.com" :name "Peppermint Buttler"}]
               :subject "Adventure Time"
               :text "Okay, just this once, we'll be vigilantes!"
               :html "<p>Okay, just this once, we'll be vigilantes!</p>"}))

#_ (deftest ^:integration ^:online ping
  (is (= (m/ping) "PONG!")))

(def message
  {:from {:email "jarvis@sistemimoderni.com" :name "Jarvis"}
   :to [{:email "jon@sistemimoderni.com" :name "Jonathan Millett"}]
   :subject "Test - Mandrill"
   :text "Okay, just this once, we'll be vigilantes!"
   :html "<p>Okay, just this once, we'll be vigilantes!</p>"})

#_ (deftest ^:integration ^:online send-basic
  (let [result @(m/send message)]
    (is (= "sent" (-> result first :status)))))

#_ (deftest ^:integration ^:online send-error
  (with-bindings {#'clj-mandrill.core/*mandrill-api-key* "xyzzy"}
    (let [result @(m/send message)]
      (is (instance? java.lang.Exception result)))))

#_ (deftest ^:integration ^:online send-delivery-error
  (let [message (assoc-in message [:to 0 :email] "bad-address")
        result @(m/send message)]
    (is (= "invalid" (-> result first :status)))))

#_ ot/cart-data
#_ (o/create ot/cart-data "--stripe data--")

#_ (let [order (o/create ot/cart-data {:locale "en" :payment-txt "--stripe data--"})]
     ;; Create a stub request so that the translations work.
     ;; TODO: Create a macro to render html using a specific locale and string translation path.
     (with-bindings {#'www.request/*req* {:locale "fr" ; can be looked up from order, or passed in as argument
                                          :uri "/order/confirmation.htm"}}
       (let [html (-> (sistemi.site.order.confirmation-htm/body {:params {:id (:id order)}})
                      hiccup.core/html)
             message (assoc message :html html
                            :text m/text-header)]
         @(m/send message)
         )))

#_ (o/lookup "78PCMR")


;; Enlive Transformations:
;; - qualify all relative urls
;; - use target _blank to force all links to open in a new window
;;
;; - Create a layout suitable for email.
;; - strip out the head
;; - replace css links with their contents
;;
;; - Inline all CSS.
;; 
;; inline styles
;; https://github.com/radkovo/CSSBox/blob/master/src/main/java/org/fit/cssbox/demo/ComputeStyles.java
