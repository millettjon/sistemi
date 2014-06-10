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

(def digits-26 "ABCDEFGHIJKLMNOPQRSTUVWXYZ")

(defn rand-26 [length]
  "Returns a cryptographically secure base 26 string of the given length."
  (apply str (repeatedly length #(char (nth digits-26 (.nextInt (SecureRandom.) 26))))))

#_ (defn rand-26
  "Returns a cryptographically secure base 26 string of the given length."
  [length]
  (apply str (repeatedly length #(char (nth digits (.nextInt (SecureRandom.) 26))))))

#_ (rand-26 6)

;; Formatting
#_ (let [id (rand-26 6)]
     (->> id
          (partition 3 3 nil)
          (map #(apply str %))
          (interpose "-")
          (apply str)))

;; Should random ids be unique across all sources?
;; ? Pre-generate for one time use?
;; - or, reserve 1 char for type?
;; 
;; BASE 26 ENTROPY
;; 6 308,915,776   SCD-ZYD   SCDZYD
;; 5  11,881,376   SC-DZY    SCDZY
;; 4     456,976   SCDZ      SCDZ
;;
;; TODO: Secure Order status confirmation pages from general browsing.
;;   - click link in email should work
;;     - link to re-send order status link to their email
;;   - should be able to find order based on confirming something about it (delivery address?)
