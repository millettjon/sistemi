(ns www.user-agent
  (:use [clojure.core.memoize :only [memo]])
  (:require [clojure.tools.logging :as log])
  (:import #_ [nl.bitwalker.useragentutils UserAgent DeviceType Browser OperatingSystem]
           [eu.bitwalker.useragentutils UserAgent DeviceType Browser OperatingSystem]))
 
(defn str->features [string]
  (try
    (let [user-agent (UserAgent. (or string ""))]
      {:browser_group (-> user-agent .getBrowser .getGroup .getName)
       :os_group (-> user-agent .getOperatingSystem .getGroup .getName)
       :device_type (-> user-agent .getOperatingSystem .getDeviceType .getName)})
    (catch Exception e
      (log/error (str "Could not derive the user-agent from " string) e)
      (str->features nil))))

(defn req->features [req]
  (str->features (get-in req [:headers "user-agent"])))
 
(def possible-features
  (memo (fn []
          {:os_group (set (map #(-> % .getGroup .getName) (OperatingSystem/values)))
           :browser_group (set (map #(-> % .getGroup .getName) (Browser/values)))
           :device_type (set (map #(.getName %) (DeviceType/values)))})))

#_ (possible-features)
