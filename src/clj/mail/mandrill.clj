(ns mail.mandrill
  (:require [app.config :as c])
  (:use clj-mandrill.core))

(def key
  (c/conf :mandrill :api :key))

;; TODO: factor out the binding form
;; TODO: log the mail response
;; TODO: create mail templates
#_ (binding [*mandrill-api-key* key]
     (send-message {:text "Hi" :subject "Just a note" :from_email "alice@test.com" :from_name "Alice"
                    :to [{:email "jon@sistemimoderni.com" :name "Jon"}]})
     #_ (ping)
     )
