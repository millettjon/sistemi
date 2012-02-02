(ns sistemi.site.modern-shelving-htm
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        [sistemi translate layout]))

(defn handle
  [req]
  (response (standard-page identity)))
