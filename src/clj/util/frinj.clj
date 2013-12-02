(ns util.frinj
  (:import frinj.core.fjv))

(defn fj-eur
  "Builds a frinj euro value. Useful since fj autoconverts to the
fundamental currency unit which is USD."
  [amount]
  (fjv. amount {:EUR 1}))
