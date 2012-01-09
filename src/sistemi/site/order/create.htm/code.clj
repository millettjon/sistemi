(ns sistemi.site.order.create
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        locale.core)
  (:import java.io.File))

(deftemplate html
  (File. "www/raw/order/create.html")
  [req strings]
  ;; Set the lang attribute for the document.
  [:html] (set-attr :lang (req :locale))

  ;; Set the page title.
  [:title] (content (strings :title))

  ;; Localize the form target url.
  [:form] (set-attr :action ((req :luri) "/order/checkout"))

  ;; Localize the checkout button.
  [:img] (set-attr :src (str "https://www.paypal.com/" (full-locale (req :locale)) "/i/btn/btn_xpressCheckout.gif"))

  ;; Set the Content-Type to utf-8
  [:head (attr= :http-equiv "Content-Type")] (set-attr :content "text/html; charset=utf-8"))

(defn handle
  [req]
  (-> (html req (req :strings))
      (response)))
