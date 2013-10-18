(ns color)

(defmulti format-swatch
  "Returns an html formatted color swatch."
  :type)
(defmethod format-swatch :default
  [{:keys [rgb]}]
  [:span.label {:style {:background-color rgb}} "&nbsp;&nbsp"])

(defmulti format-name
  "Returns a html formatted color name."
  :type)

(defn format-html
  "Formats a color selection in html."
  [color]
  [:span (format-swatch color) "&nbsp;&nbsp;" (format-name color) ])
