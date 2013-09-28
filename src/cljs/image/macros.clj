(ns image.macros)

(defmacro onload-let
  "Loads a set of images, binds them to the specified symbols, and then executes body.

Example:
  (onload-let [wheel-raw \"valchromat-wheel-raw.png\"]
              (-> (c/get-context canvas \"2d\")
                  (.drawImage wheel-raw 0 0)))
"
  [bindings & body]
  (let [;; Convert the bindings to a map.
        bm (->> bindings
                (partition 2)
                (map (fn [[k v]] [(keyword k) v]))
                (into {}))
        images-sym (gensym "images")]
    `(image/onload ~bm (fn [~images-sym]
                         (let [~@(->> bindings
                                      (partition 2)
                                      (mapcat (fn [[sym _]] `(~sym (~(keyword sym) ~images-sym)))))]
                           ~@body)))))
