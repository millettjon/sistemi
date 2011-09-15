(ns sistemi.site.profile.select-language-html
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)])
  (:import java.io.File))

(deftemplate html
  (File. "www/raw/profile/select-language.html")
  [req strings]
  [:title] (content (strings :title))
  [:span#prompt] (content (strings :prompt)))

(defn handle
  [req]
  (-> (html req (req :strings))
      (response)))

;; (prn "file" *file*)
;; TRY TO GET TO THIS
;; (my-deftemplate
;;  [:title] (content (strings :title))
;;  [:span#prompt] (content (strings :prompt)))
