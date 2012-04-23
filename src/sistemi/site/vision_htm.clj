(ns sistemi.site.vision-htm
  (:require [util.string :as stru])
  (:use [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {:es "visíon"
   :fr "vision"})

(def strings
  {:en {:title "Vision Of Modern Furniture : High Design, Resonably Priced, Sustainably."
        :vision {:title "OUR VISION"
                 :text (stru/join-lines "<span class=\"company_name\">SistemiModerni</span> is founded to
                       bring you high design and high quality for a reasonable price. We believe that
                       the world must stop creating low quality disposable goods. We also believe that
                       you can and should have things your way. That is why we empower you to
                       personalize all of our offerings.")}}
   :es {}
   :fr {:vision {:title "NOTRE VISION"
                 :text (stru/join-lines "<span class=\"company_name\">SistemiModerni</span> a été créée
                       pour vous offrir design et qualité au meilleur prix. Nous estimons qu’il faut
                       arrêter de fabriquer des produits jetables de qualité médiocre. Nous
                       pensons aussi que vous  devriez pouvoir disposer des  produits tels 
                       que vous les imaginez.  C’est pour cela que nous tenons à vous donner
                       les moyens de personnaliser toute les gammes de nos produits.")}}})

(defn body
  []
  [:div.text_content
   [:p.title (translate :vision :title)]
   [:p (translate :vision :text)]])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))

(sistemi.registry/register)
