(ns sistemi.site.careers-htm
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def company
  [:span.company_name "SistemiModerni"])

(def names
  {:es "sugerencias"
   :fr "impressions"})

(def strings
  {:en {:title "SistemiModerni: Careers"
        :body ""

        }
   :es {}
   :fr {:title "SistemiModerni:Opportunités de Carrier"}})

(defn body
  []
  [:div.text_content
   "tbd content here..."
])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
