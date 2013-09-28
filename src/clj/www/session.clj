(ns www.session
  "Utitlies for working with ring sessions."
  (:require [www.response :as resp]))

(defn swap
  "Reads a value from the session map, applies it and the args to
function f and saves the result in the session. Analogous to
core.swap!."
  [resp req key f & args]
  (apply resp/swap resp req [:session key] f args))
