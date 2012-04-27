(ns sistemi.site.order.view-htm
  (:use paypal
        app.config
        [ring.util.response :only (response)]
        [hiccup core]
        [sistemi layout]))

(def names
  {:es "ver"})

(def strings
  {{:en { :title "Order Details"}}
   {:es { :title "Detaille de Orden"}}  
   })

(defn body
  [details]
  (html [:html
         [:head
          [:title "Order Details"]]
         [:body
          [:h2 "Order Details"]
          [:table (map (fn [k] [:tr [:td k] [:td (k details)]])
                       (sort (keys details)))]]]))

(defn handle
  [req]
  (with-conf (conf :paypal)
    (let [qp (-> req :query-params normalize-keys)
          details (xc-details {:token (qp :id)})]
      (response (standard-page "" (body details) 544)))))

(sistemi.registry/register)
