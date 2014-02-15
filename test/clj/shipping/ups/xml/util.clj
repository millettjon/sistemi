(ns shipping.ups.xml.util
  (:require [clojure.string :as s]
            [clojure.data.xml :as x]) )

(defn strip-newlines
  "Strip newline characters from text -- "
  [& text]
  (s/replace (apply str text) "\n" ""))

(defn parse-xml-into-structure
  "Parsing the XML into a structure requires a Reader."
  [xml_text]
  (when-not (empty? xml_text)
    (x/parse (java.io.StringReader. xml_text)) ) )

(defn examine-content
  [element]
  (reduce )
  )

(def foo {:a "a" :b '({:a "a1" :b (list "b1")} {:a "a1a" :b (list {:a "a1a2" :b "b2"})} ) } )

;(defn test-foo
;  [bar]
;  (let [values (list foo)
;        results '()]
;    (loop [current (first values)]
;      (if (= bar (-> current :a))
;        (conj results (first (-> current :b)))
;        (recur)
;        ) )
;    ) )