(ns www.response
  "Utilities for working with ring responses.")

(defn swap
  "Reads a value from the request map, applies it and the args to
function f and stores the result into the response map. Analogous to
core.swap!."
  [resp req keys f & args]
  (assoc-in resp keys (apply f (get-in req keys) args)))
