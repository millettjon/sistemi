(ns util.base62
  "Functions for working with base62 strings."
  (:refer-clojure :exclude [rand])
  (:import java.security.SecureRandom))

(def digits "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")

(defn encode
  "Returns the base62 encoded form of the given integer."
  ([n] (encode n (if (= n 0) "0" "")))
  ([n s]
     (if (= n 0)
       s
       (recur (int (/ n 62))
              (str (get digits (mod n 62)) s)))))

(defn rand [length]
  "Returns a cryptographically secure base62 string of the given length."
  (apply str (repeatedly length #(char (nth digits (.nextInt (SecureRandom/getInstance "SHA1PRNG") 62))))))
