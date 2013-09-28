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
                       personalize all of our offerings.
                       <p/>
                       Through the use of today’s latest technologies, we are changing the very nature of online
                       commerce. Automation, interface and service are the driving forces that allow us to
                       provide you with personalized solutions in your home and office.  Whether you are searching
                       for that one item that fits just right in your client’s home or your own, Sistemi Moderni
                       will automatically set fabrication in motion from the nearest facility to your chosen
                       delivery destination.  Our extensive network of artisans will ensure that your order is
                       created with the greatest precision and care.  As our name implies, we will always use
                       the most modern of systems to deliver your personalized products to your doorstep.")}}
   :es {}
   :fr {:vision {:title "NOTRE VISION"
                 :text (stru/join-lines "<span class=\"company_name\">SistemiModerni</span> a été créée
                       pour vous offrir design et qualité au meilleur prix. Nous estimons qu’il faut
                       arrêter de fabriquer des produits jetables de qualité médiocre. Nous
                       pensons aussi que vous  devriez pouvoir disposer des  produits tels 
                       que vous les imaginez.  C’est pour cela que nous tenons à vous donner
                       les moyens de personnaliser toute les gammes de nos produits.")}}

   :it {:vision {:title "LA NOSTRA VISIONE"
                 :text (stru/join-lines "<span class=\"company_name\">Sistemi Moderni</span> è stata fondata
                        per offrirvi la più alta qualità ed un design prestigioso, a dei prezzi ragionevoli.
                        Noi siamo convinti che il commercio basato su merci usa-e-getta di bassa qualità non
                        sia positivo per il nostro pianeta, ed è inoltre nostra convinzione che il Cliente
                        possa e debba esigere prodotti di qualità, rifiniti secondo le sue aspettative.
                        Per questa ragione, a Sistemi Moderni vi offriamo la possibilità di personalizzare
                        ognuno dei nostri prodotti a seconda delle vostre preferenze.")}}

   })

(defn body
  []
  [:div.text_content
   [:p.title (translate :vision :title)]
   [:p (translate :vision :text)]])

(defn handle
  [req]
  (response (standard-page "" (body) 544)))
