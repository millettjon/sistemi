(ns sistemi.site.development-htm
  (:require [ring.util.response :as ru]
            [sistemi.layout :as l]
            [sistemi.translate :as tr]))
#_ (remove-ns 'sistemi.site.development-htm)

(def names
  {:es "estantaría"
   :fr "etagères"})

(def strings
  {:en {:credenza {:copy "Looks simple.  Does it not?  The only thing simple about Credenza is its assembly.  There are no screws, nails, dowels or glue necessary to put her together.  The ingenuity is in the patent pending connection system that we cleverly hid from view.  You can choose almost any dimension and color just like the bookcases and shelves.  However, you will need to call on us to make it and give you a cost.  Credenza will become part of our automated systems in the near future.<br><br>

If you want to find out more about this innovative product, click on the contact button below."}
        :credenza-classic {:copy "We call this Classic because this cabinet is assembled in a traditional manner from parts that are made with a CNC machine.  Modern systems today will soon be classics tomorrow like this beautiful example of Credenza.  Like her more modern cousin, Credenza Classic is assembled without fasteners and can be custom ordered in so many dimensions, colors and materials.<br><br>

If you want to find out how to get your hands on this product, click on the contact button below!
"}
        :cupboard {:copy "We are so busy that we have not even taken a moment to come up with a catchy name for this innovative storage solution.  Just like Credenza, Cupboard uses our patent pending connection system to hide the magic of its easy assembly.  The choices are endless.<br><br>

To find out just how personalized Cupboard can be, give us a ring by clicking on the contact button below!
"}
        :nata {:copy "The Nata sofa is truly amazing.  Like everything else we do, assembly of Nata is without fasteners.  But this time, you get to further personalize your furniture by choosing just the right fabric for your seating pleasure.  We have successfully prototyped her in New York.  There is still some work we have to do in order to bring Nata to Europe.  Perhaps you would like to be a part of that effort.<br><br>

If you find Nata to be exactly the solution you were looking for, reach out to us by clicking on the contact button below.  We would love to make Nata especially for you on this continent!
"}
        :office-library {:copy "This is the solution every office needs.  Gone are the days of being surrounded by cushiony cubicle walls that make us feel like patients in a sanatorium.  With Oasi we can surround ourselves not only with the accouterments of any professional office, but also your personal collection of favorite architecture books.  The beyond modular design allows you to choose a single or double sided shelving system with computer desks or without.  This design also allows you to hide cabling, pipes and wires.  If you want, you can even use Oasi as an architectural element by creating a room.  Simply give us your floor to ceiling dimension.  And yes, Oasi is assembled without any fasteners whatsoever.<br><br>

Does Oasi provide you the most amazing solution to your design problem?  If so, give us a call by clicking on the contact button below.
"}}
   :es {}
   :it {}
   :fr {}})

(defn body [req]
  (let [p (:params req)
        type (-> p :type keyword)
        image (:image p)]
    [:div {:style {:margin-left "25px" :margin-top "25px" :width "625px"}}
     [:img {:src image :style {:width "625px" :margin-bottom "15px"}}]
     [:p (tr/translate type :copy)]

     [:br]
     [:div {:style {:margin-bottom "15px"}}
      [:a {:href (tr/localize "/contact.htm")}
       ;; TODO Pass product information to the contact form.
       [:button#submit.btn.btn-inverse {:typez "submit" :tabindex 1} "Contact Us"]]
      [:a {:href (tr/localize "/")}
       [:button#submit.btn.btn-inverse {:typez "submit" :tabindex 2 :style {:margin-left "20px"}} "Back"]]]
]))

(defn handle
  [req]
  (ru/response (l/standard-page "" (body req) 544)))
