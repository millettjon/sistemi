(ns shipping.ups.tools
  (:import [java.io ByteArrayInputStream]
           [javax.crypto KeyGenerator])
  (:require [clojure.string :as str]) )

(defn text-in-bytestream
  "Turn text into ByteArrayInputStream (trimmed text)."
  [text]
  (ByteArrayInputStream. (.getBytes (str/trim text))) )

(defn access-data-from-config
  "Pulled from our encrypted config"
  [access_config_info]
  (println "access_config_info:" access_config_info)
  { :UserId (:UserId access_config_info)
    :Password (:Password access_config_info)
    :AccessLicenseNumber (:AccessLicenseNumber access_config_info)
    :AccountNumber (:AccountNumber access_config_info)
    :lang_locale "en-US"} )
