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
  { :user_id (access_config_info :user-id)
    :password (access_config_info :password)
    :license_number (access_config_info :access-key)
    :account_number (access_config_info :account-number)
    :lang_locale "en-US"} )
