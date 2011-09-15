(ns util.string)

(defn starts-with?
  "Returns true if string starts with substring."
  [string substring]
  (.startsWith string substring))
