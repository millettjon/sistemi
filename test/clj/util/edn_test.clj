(ns util.edn-test
  (:require [util.edn :as edn]
            [frinj.ops :as f])
  (:use clojure.test
        ordered.map))

(deftest roundtrip
  ;; frinj literal
  (let [v (f/fj 1 :m)]
    (is (= v (-> v pr-str edn/read-string))))

  ;; ordered-map
  (let [m (ordered-map :foo "FOO" :bar "BAR")]
    (is (= m (-> m pr-str edn/read-string)))))

#_ (ordered-map :foo "FOO" :bar "BAR")
#_ (->>
    (ordered-map :foo "FOO" :bar "BAR")
    ;; type ; ordered.map.OrderedMap
    ;; map?  ; true
    ;; (instance? clojure.lang.IRecord) ; false
    )

#_ (ordered-map 0 {:foo "FOO"} 1 {:bar "BAR"})
#_ (pr (ordered-map :foo "FOO" :bar "BAR"))
#_ (pr-str (ordered-map :foo "FOO" :bar "BAR"))
#_ (print-str (ordered-map :foo "FOO" :bar "BAR"))
#_ (-> ;;{}
    (ordered-map :foo "FOO" :bar "BAR")
    (pr-str "FOO")
       ;; edn/read-string
        )

#_ (let [s (pr-str (ordered-map :foo "FOO" :bar "BAR"))]
     s
     )
;; WTF: pr-str doesn't work with ordered-map. It never returns or prints blank?
;; ? Is it printing to an invisible writer?
;; ? How does the test work?
;; hmm, it does work from the repl
;; probably a bug in cider
