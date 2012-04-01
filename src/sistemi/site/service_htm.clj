(ns sistemi.site.service-htm
  (:require [util.string :as stru])
  (:use net.cgrand.enlive-html
        [ring.util.response :only (response)]
        [sistemi translate layout]))

(def names
  {:es "servicio"})

(def strings
  {:en {:title "Service : High Design, Resonably Priced, Sustainably."
        :service {:title "WITH YOU FOR YOU"
                  :text (stru/join-lines "&quot;We have been working hard to make
                         <span class=\"graytxt\">SistemiModerni</span>'s vision a reality. There is a road
                         ahead and we want to take it WITH YOU so we can create the best possible
                         experience and products FOR YOU. The fact that you are here tells us that you are
                         intrigued by what <span class=\"graytxt\">SistemiModerni</span> has to offer.
                         Do not stop here! Take a moment to really tell us how you feel about
                         <span class=\"graytxt\">SistemiModerni</span> and what you would like to see us do for you.&quot;")
                  :solicitation "Please take the time to write to us below."}}
   :es {}
   :fr {}})

(defn page
  "Applies page transformations."
  [node]
  (at node
      [:div#col3b :div.title] (content (translate :service :title))
      [:div#col3b :> (nth-child 3)] (html-content (translate :service :text))
      [:div#col3b :> (nth-child 4)] (html-content (translate :service :solicitation))))

(defn handle
  [req]
  (response (standard-page "" page 544)))

(sistemi.registry/register)






