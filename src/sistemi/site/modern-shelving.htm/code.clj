(ns sistemi.site.modern-shelving-html
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response content-type)])
  (:import java.io.File))

(deftemplate html
  (File. "www/raw/modern-shelving.htm")
  [req strings]
  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (req :locale))
  )

(defn handle
  [req]
  (-> (html req (req :strings))
      (response)
      ))
