(ns util.net
  "Network utilities."
  (:require [clojure.core.cache :as cache]))

(defn- ping-google
  "Returns true if google's public dns server can be pinged. Note:
ICMP echo requests require root access so shell out to ping which runs
as suid root."
  []
  (= 0 (:exit (clojure.java.shell/sh "ping" "-c" "1" "8.8.8.8"))))

(def cache
  "TTL Cache with a timeout of 1 minute."
  (atom (cache/ttl-cache-factory {} :ttl (* 60 1000))))

(defn online?
  "Returns true if connected to the internet."
  []
  (let [cache (if (cache/has? @cache :online)
                (cache/hit @cache :online)
                (swap! cache #(cache/miss % :online (ping-google))))]
    (:online cache)))

(defn offline?
  "Returns true if not connected to the internet."
  []
  (not (online?)))
