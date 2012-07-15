(ns www.form
  "Functions for validating and rendering forms."
  (:require [clojure.string :as s])
  (:use ordered.map))

;; ===== multi methods =====

(defmulti parse
  :type)

(defmethod parse :default
  [field value]
  value)

(defmulti invalid?
  :type)

(defmethod invalid? :default
  [field value])

(defmulti options
  :type)

;; ===== bounded-number =====

(defmethod parse :bounded-number
  [field value]
  (try
    (Long/parseLong value)
    (catch Exception e
      :invalid-number-format)))

(defmethod invalid? :bounded-number
  [field value]
  (cond
   (< value (:min field)) :too-small
   (> value (:max field)) :too-large))

(defn units
  [field option]
  (if-let [u (:units field)]
    {:label (str option " " u)}
    {}))

(defmethod options :bounded-number
  [field]
  (reduce #(assoc % %2 (units field %2))
       (ordered-map)
       (range (:min field) (inc (:max field)))))

;; ===== set =====

(defn options-to-ordered-map
  "Coerces an options list to an ordered map."
  [options]
  (loop [om (ordered-map)
         l options]
    (if (empty? l)
      om
      (let [[k l] [(first l) (rest l)]]
        (if (map? (first l))
          (recur (assoc om k (first l)) (rest l))
          (recur (assoc om k {}) l))))))

#_(options-to-ordered-map
 [])

#_(options-to-ordered-map
 [1 2 {:label "two"} 3 4])

(defmethod invalid? :set
  [field value]
  (let [options (options-to-ordered-map (:options field))]
    (cond
     (not (contains? options value)) :option-unknown
     (get-in options [value :disabled]) :option-disabled)))

(defmethod options :set
  [field]
  (options-to-ordered-map (:options field)))

;; ===== rgb color =====
(defmethod parse :rgb
  [field value]
  (if-let [rgb (second (re-matches #"#?([\dA-Fa-f]{6})" value))]
    (str "#" (s/upper-case rgb))
    :invalid-rgb-format))

;; ===== validation pipeline =====

(defn add-error
  [m k error-key]
  (let [ks [k :errors]
        errors (get-in m ks [])]
    (assoc-in m ks (conj errors error-key))))

(defn check-nil
  [m k value]
  (if (nil? value)
    (add-error m k :nil)
    m))

(defn parse-field
  [m k v]
  (if (nil? v)
    m
    (let [result (parse (k m) v)]
      (if (keyword? result)
        (add-error m k result)
        (assoc-in m [k :parsed-value] result)))))

(defn validate-field
  [m k]
  (let [field (k m)
        value (:parsed-value field)]
    (if (nil? value)
      m
      (if-let [error (invalid? field value)]
        (add-error m k error)
        (assoc-in m [k :value] value)))))

(defn validate
  [fields values]
  (reduce (fn [m k]
            (let [v (k values)]
              (-> m
                  (check-nil k v)
                  (parse-field k v)
                  (validate-field k))))
          fields (keys fields)))

;; ===== public functions =====
(declare ^:dynamic *fields*)

;; TODO: Does this need a render like enlive has to realize all seqs in the context of the form?
(defmacro with-form
  [fields values & body]
  `(binding [*fields* (validate ~fields ~values)]
     ~@body))

;; ===== internal helpers =====
;; TODO: remove if possible
#_(defn- _default
  [m]
  (get m (if (_errors? m)
           :default
           :value)))

(defn errors?
  ([] (some #(errors? %) (keys *fields*)))
  ([k] (contains? (k *fields*) :errors)))

#_(defn default
  [k]
  (_default (k *fields*)))

(defn default
  "Returns the value for a field or the default if there is a validation error."
  [k]
  (if-let [fm (k *fields*)]
    (get fm (if (errors? k)
              :default
              :value))))

(defn values
  []
  (reduce
   (fn [m [k v]] (if (contains? v :value)
                  (assoc m k (:value v))
                  m))
   {}
   *fields*))

;; ===== rendering =====
(defn select
  [k opts]
  (let [field (k *fields*)]
    (let [selected (default k)
          n (name k)]
      [:select (merge {:name n :id n} opts)
       (doall
        (for [[k m] (options field)]
          (let [m (if (= k selected)
                    (assoc m :selected true)
                    m)]
            ;; If there is a label, put the key in the value and the label in the text.
            (if-let [label (:label m)]
              [:option (-> m (assoc :value k) (dissoc :label)) label]
              [:option m k]))))])))

(defn text
  [k opts]
  (let [n (name k)]
    [:input (merge {:type "text" :name n  :id n :value (default k)} opts)]))

(defn hidden
  "Converts a map into a seq of hidden fields."
  [m]
  (map
   (fn [[k v]] [:input {:type "hidden" :name (name k) :value v}])
   m))

#_ (def fields
  {:width {:type :bounded-number :min 60 :max 240 :default 120}})

#_ (with-form
     fields
;;     {:width "A"}
;;     {}
;;     {:width nil}
     {:width "33"}
;;     {:width "60"}

     *fields*
     ;;(default :width)
     ;;(errors? :width)
     )
