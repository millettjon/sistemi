(ns paypal
  (:require [paypal.core :as p]
            [fidjet.core :as f])
  (:use [util.except :only (check)]))

(f/remap-ns-with-arg paypal.core conf)
