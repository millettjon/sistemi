(ns sistemi.site.order.view
  (:use paypal
        app.config
        [ring.util.response :only (response)]
        [hiccup core]))

;; view.htm?id=xyz
;; The .htm extension is superflous...
;;   - dynamic responses include a content-type...
;;   - slightly useful for editing template files?
;;   - distinguishes between urls w/ views and actions only
;; ? Should restful urls be used?
;;   - /order/1

;; TODO: Translate strings.
(defn view
  [details]
  (html [:html
         [:head
          [:title "Order Details"]]
         [:body
          [:h2 "Order Details"]
          [:table (map (fn [k] [:tr [:td k] [:td (k details)]])
                       (sort (keys details)))]]]))

;; http://localhost:5000/en/order/view.htm?token=EC-2CX87631XB186964J
(defn handle
  [req]
  (with-conf (conf :paypal)
    (let [qp (-> req :query-params normalize-keys)
          details (xc-details {:token (qp :id)})]
      (response (view details)))))

;; http://localhost:5000/en/order/view.htm?id=EC-2CX87631XB186964J

