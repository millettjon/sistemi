(ns www.form
  "Functions for validating and rendering forms."
  (:require [clojure.string :as s]
            [clojure.tools.logging :as log])
  (:use ordered.map))

;; Parse errors derive from this symbol.
::parse-error

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
(derive ::invalid-number ::parse-error)

(defmethod parse :bounded-number
  [field value]
  (try
    (if (string? value)
      (Long/parseLong value)
      (long value))
    (catch Exception e
      ::invalid-number)))

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
(derive ::unknown-keyword ::parse-error)

(defn parse-keyword
  "Reads a keyword from a string. Splits the keyword into namespace and name parts and calls find-keyword."
  [s]
  (if-let [m (re-matches #":(([^/]+)/)?([^/]+)" s)]
    (apply find-keyword (rest (rest m)))))

(defmethod parse :set
  [field value]
  (if (string? value)
    (or (parse-keyword value)
        ::unknown-keyword)
    value))

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
(derive ::invalid-rgb ::parse-error)
(defmethod parse :rgb
  [field value]
  (if-let [rgb (second (re-matches #"#?([\dA-Fa-f]{6})" value))]
    (str "#" (s/upper-case rgb))
    ::invalid-rgb))

;; ===== string =====
(defmethod invalid? :string
  [field value]
  (cond
   (> (count value) (:max field)) :too-large))

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
      (if (and (keyword? result) (isa? result ::parse-error))
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

;; - need a way to nest a sub form under a key
;;   ? what if a field was a sub form?
;;     ? how to pass the type?
;;       - use :type :form
;;       - make validate recursive
;;     ? how to make other fns work?
;;       - make recursive to work on form tree
;;       - take multiple keys to lookup fields
;; - need a way to support sibling forms under the top level (or sub key)
;;   ? would a straightforward merge work?
;;     (with-form (merge f1 f2) values)

;; TODO: Does this need a render like enlive has to realize all seqs in the context of the form?
(defmacro with-form
  "Evaluates body in the context of the given form fields and values."
  [fields values & body]
  `(binding [*fields* (validate ~fields ~values)]
     ~@body))

(defmacro with-valid-form
  "Same as with-form, but throws an exception if there are validation errors."
  [fields values & body]
  `(with-form ~fields ~values
     (when (errors?)
       (log/error *fields*)
       (throw (Exception. (str "Form validation error: " (errors)))))
     ~@body))

;; ===== errors =====
(defn errors?
  "Returns true if there was a validation error."
  ([] (some #(errors? %) (keys *fields*)))
  ([k] (contains? (k *fields*) :errors)))

(defn errors
  "Returns a map of errors by field name."
  []
  (reduce (fn [m [k v]]
            (if-let [errors (:errors v)]
              (assoc m k errors)
              m))
          {} *fields*))
;; {:quantity {:default 1, :max 100, :errors [:nil], :type :bounded-number, :min 0}, :type {:errors [:nil], :type :set, :options [:shelf :shelving]}, :id {:default -1, :parsed-value -1, :max 100, :type :bounded-number, :min -1, :value -1}}

;; ===== values =====
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

              ;; If there is a translate function, apply it and use that as the text.
              (if-let [f (:translate field)]
                [:option (assoc m :value (str k)) (f k)]
                
                ;; If it is a keyword, put the keyword in the value and its name in the text.
                (if (keyword? k)
                  [:option (assoc m :value (str k)) (name k)]
                  [:option m k]))))))])))

(defn text
  [k opts]
  (let [n (name k)]
    [:input (merge {:type "text" :name n  :id n :value (default k)} opts)]))

(defn textarea
  [k opts]
  (let [field (k *fields*)
        n (name k)]
    [:textarea (merge {:name n, :id n, :maxlength (:max field)} opts) (default k)]))

(defmulti render
  "Renders a value to html."
  class
  )

(defmethod render :default
  [v]
  v)

(defmethod render clojure.lang.Keyword
  [v]
  (str v))

(defmulti hidden
  "Inserts hidden paramters."
  #(type %))

(defmethod hidden clojure.lang.Keyword
  [& args]
  (doall
   (map (fn [kw]
          [:input {:type "hidden" :name (name kw) :value (render (default kw))}])
        args)))

(defmethod hidden clojure.lang.PersistentArrayMap
  [m]
  (map (fn [[k v]] [:input {:type "hidden" :name (name k) :value (render v)}])
       m))

#_(defn hidden
  "Converts a map into a seq of hidden fields."
  [m-or-kw]
  (if (map? m-or-kw)
    (map
     (fn [[k v]] [:input {:type "hidden" :name (name k) :value (render v)}])
     m-or-kw)
    [:input {:type "hidden" :name (name m-or-kw) :value (render (default m-or-kw))}]))

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
