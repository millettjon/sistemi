(ns sistemi.site.order.create
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response content-type)]
        locale.core)
  (:import java.io.File))

;; TODO: Move this code into locale. Where should it be initialized from?
(use 'app.config)
(defn full-locale
  [locale]
  (let [territory (conf :internationalization :default-territories (keyword locale))]
    (str (name locale) "_" territory)))

(deftemplate html
  (File. "www/raw/order/create.html")
  [req strings]
  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (req :locale))

  ;; Set the page title.
  [:title] (content (strings :title))

  ;; Internationalize the checkout button.
  [:img] (set-attr :src (str "https://www.paypal.com/" (full-locale (req :locale)) "/i/btn/btn_xpressCheckout.gif"))

  ;; Set the Content-Type to utf-8
  [:head (attr= :http-equiv "Content-Type")] (set-attr :content "text/html; charset=utf-8"))

(defn handle
  [req]
  (-> (html req (req :strings))
      (response)))
