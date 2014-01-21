(ns harpocrates.core
  "Harpocrates the god of silence."
  (:require [clojure.edn :as edn])
  (:use clojure.walk
        [clojure.java.shell :only [sh]]
        [util.pojometa :only [meta* with-meta*]]))

(defn secret?
  "Returns true if the object is secret."
  [o]
  (:secret (meta* o)))

(defn redact
  "Recursively walks a map and redacts secret values."
  [m]
  (let [f (fn [[k v]] (if (secret? v) [k "-redacted-"] [k v]))]
    ;; only apply to maps
    (postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn classify
  "Recursively walks a map and classifies leaf string literals as secret."
  [m]
  (let [f (fn [[k v]] (if (instance? String v) [k (with-meta* v (assoc (meta* v) :secret true))] [k v]))]
    ;; only apply to maps
    (postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn decrypt
  "Decrypts a file using gpg and returns the result.
   The passphrase can be supplied via the :passphrase option and will be passed to gpg on stdin.
   The gpg home directory can be overriden by passing the :home option.
   The decrypted text is read as edn. Classify is then called to tag leaf literals as secret."
  [file {:keys [passphrase home]}]
  (let [args ["gpg"
              (if home ["--home" home])
              "--decrypt"
              (if passphrase "--passphrase-fd=0" "--use-agent")
              "--quiet"
              "--no-tty"
              (str file)
              (and passphrase [:in passphrase])]
        result (apply sh (filter identity (flatten args)))]
    (if (= 0 (:exit result))
      (-> result :out edn/read-string classify)
      (throw (RuntimeException. ^String (:err result))))))
