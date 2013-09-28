(ns util.pojometa
"Add metadata to pojos. Implementation based on this conversation:
https://groups.google.com/forum/?fromgroups=#!topic/clojure/9zCGdW_Q7o8

Here's an approach that may be of use: store the metadata in a
Map instead of decorating the Object with it. This map should use
object identity, not equality and should hold its keys weakly so that
it prevent collection of objects that otherwise would be garbage."
    (:import [com.google.common.collect MapMaker]
             [java.util.concurrent ConcurrentMap]))


;; Caveats
;; (def o (Object.))
;;
;; (def om (with-meta* o {:foo true}))
;;
;; (def whatever (with-meta* o {:foo false}))
;;
;; (meta* om) ;=> {:foo false}
;;
;; Doesn't really support Clojure's concept of metadata if it's shared
;; global mutable state.

(def ^ConcurrentMap meta-map
    (-> (MapMaker.) .weakKeys .makeMap))

(defn meta* [o]
    (if (instance? clojure.lang.IMeta o)
        (clojure.core/meta o)
        (.get meta-map o)))

(defn with-meta* [o m]
    (if (instance? clojure.lang.IMeta o)
        (clojure.core/with-meta o m)
        (do (.put meta-map o m)
            o)))
