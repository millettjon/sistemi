(ns shipping.ups.response
  (:require [shipping.ups.util :as u]))

 (defn is-xml-element
  "Is the current object an XML Element defrecord?"
  [object]
  (if (nil? object)
    false
    (= clojure.data.xml.Element (class object)) ) )

; loop / recur through xml
;   -- reduce for each list of content at each element

; Element {:tag tag_name :content '()}

(defn get-content-for-tag
  [response tag]
  (loop [results '()
         current response]
    (let [current_tag (-> response :tag )
          content (-> response :content)
          first_value (first content)]
      (println "tag: " tag ", current_tag: " current_tag)
      (if (= current_tag tag)
        (if (not (is-xml-element first_value))
          (results conj content)
          ;(println "first_value: " first_value) )) )
          (recur results first_value)) ) )
          ;(println "Not another xml element")
          ;(println "It is another xml element"))
          ;(conj results content))
        ;(recur results first_value) ) )
    ) )

(defn get-response-status
  "Get the response status from a ShipmentConfirmResponse"
  [response]
  (when-not (nil? response)
    (-> response :ShipmentConfirmResponse :Response :ResponseStatus) ) )

(defn shipment-confirm-response
  "Build a map of results from a parsed XML response."
  [response]
  {:TransactionReference (-> response :TransactionReference)})