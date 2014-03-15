(ns util.id
  "Functions for working with ids expressed as base 36 and 62 strings."
  (:refer-clojure :exclude [rand])
  (:import java.security.SecureRandom))

(def digits "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")

(defn encode-62
  "Returns the base62 encoded form of the given integer."
  ([n] (encode-62 n (if (= n 0) "0" "")))
  ([n s]
     (if (= n 0)
       s
       (recur (int (/ n 62))
              (str (get digits (mod n 62)) s)))))

(defn rand-62 [length]
  "Returns a cryptographically secure base 62 string of the given length."
  (apply str (repeatedly length #(char (nth digits (.nextInt (SecureRandom.) 62))))))

(defn rand-36
  "Returns a cryptographically secure base 36 string of the given length."
  [length]
  (apply str (repeatedly length #(char (nth digits (.nextInt (SecureRandom.) 36))))))
