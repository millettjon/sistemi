(ns sistemi.site.contact-htm
  (:require [sistemi.translate :as tr]
            [sistemi.site.feedback-htm :as fb])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {})

(def strings
  fb/strings)

(defn handle
  [req]
  (fb/handle req))
