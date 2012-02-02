(ns sistemi.site.vision-htm
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        [sistemi translate layout]))

(defn vision
  "Applies vision-page transformations."
  [node]
  (at node
      [:div#col3b :div.title] (content (translate :vision :title))
      [:div#col3b :> (nth-child 3)] (html-content (translate :vision :text))))

(defn handle
  [req]
  (response (standard-page vision)))
