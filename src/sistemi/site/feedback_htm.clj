(ns sistemi.site.feedback-htm
  (:require [util.string :as stru]
            [hiccup.core :as hcp]
            [sistemi.layout :as layout])
  (:use [ring.util.response :only (response)]
        [sistemi translate]))

(def names
  {:es "sugerencias"
   :fr "impressions"})

(def strings
  {:en {:title "Vision Of Modern Furniture : High Design, Resonably Priced, Sustainably."
        :feedback {:title "YOUR FEEDBACK - TELL US"
                   :call-head "Call"
                   :call-body "us at: +33 06 09 46 92 00"
                   :write-head "Write"
                   :write-body "us a note at: "
                   :questionaire-head "Questionaire"
                   :questionaire-body "we are giving out feedback codes\nwhich entitle you to a discount."
                   :text1 [:p  (stru/join-lines "Thanks for navigating to our Questionaire page. Please take
                                 a moment to fill out our form. Once you submit it, we will give you a feedback code
                                 which entitles you to a ¤ 10 discount on your next purchase. If you are sufficiently
                                 impressed with what we are doing and refer us to a friend, make sure you give them
                                 your code as each time a friend makes a purchase using your code you will receive
                                 additional ¤ 10 of savings. This can add up quickly and probably will allow you to receive ")
                           [:span.graytxt "SistemiModerni"] " products for free."]
                   :text2  [:p "We really want your comments good and bad so we can provide you with the best service and
                                products. If there is anything you would like to see " [:span.graytxt "SistemiModerni"]
                            " make or change, do not hesitate to contact us. We are always here for you."]
                   :text (stru/join-lines "\"Your product begins with you. The moment your choice is
                          submitted, the commands to run the machines at the factory are created.
                          The factory produces the sheets of top grade \"multiplex\" plywood at the
                          core of your shelves. Then they cut the sheets according to your requests with
                          unbelievable accuracy and speed. (Click here to see a video.) Once your shelves
                          are cut, they are inspected for potential imperfections and hand sanded in
                          preparation for painting. Once in the paint booth, your shelves are sealed and
                          coated with an automotive grade lacquer in your choice of color. Your shelves
                          are then carefully wrapped and boxed in a custom designed heavy duty cardboard
                          package in preparation for rapid delivery to your doorstep. You are going to
                          love opening your gift when it arrives.\"")}}
   :es {}
   :fr {}})

(defn body
  []
  (seq 
   [[:div.title (translate :feedback :title)]
    [:p 
     [:span.calltoaction (translate :feedback :call-head)] (translate :feedback :call-body)
     [:br ]
     [:span.calltoaction (translate :feedback :write-head)] (translate :feedback :write-body)
     [:a { :href "mailto:feedback@sistemimoderni.com?subject=From%20Website"} "feedback@sistemimoderni"]
     [:br]
     [:span.calltoaction (translate :feedback :questionaire-head)] (translate :feedback :questionaire-body)]
    (translate :feedback :text1)
    (translate :feedback :text2)
    [:form#feedback {:method "post", :action "", :name "feedback"}
     [:textarea.greytextarea {:name "service"}]
     [:br]
     [:input#submitbtn.submitbtn {:type "image", :name "submitbtn", :src "graphics/submitbtn.gif"}]]]))

(defn handle
  [req]
  (response (layout/standard-page (body))))

(sistemi.registry/register)