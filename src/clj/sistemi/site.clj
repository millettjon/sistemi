(ns sistemi.site
  "Root url and strings table."
  (:require [sistemi.layout :as layout]
            [sistemi.product.gallery :as g]
            [util.path :as p]
            [sistemi.translate :as tr])
  (:use [ring.util.response :only (response content-type)]
        [util.map :only (map-vals)]))

(def strings
  {:en {;; page
        :title "Modern Shelving Furniture : Sistemi Moderni"

        ;; layout
        :beta "beta"
        :webgl_recommended "This site works best if WebGL rendering is enabled."
        :canvas_required "This site requires html5 canvas or WebGL to function."
        :recommended_browsers "We recommend <a href='http://www.mozilla.org'>Firefox</a> or <a href='http://www.google.com/chrome'>Chrome</a>."
        :javascript_required "This site requires javascript to function."

        :account "My Account"
        :cart {:_ "Cart"
               :add "Add to cart"
               :update "Update cart"}
        :header {:signup "Sign Up For Emails"
                 :contact "Contact Us"
                 :careers "Careers"
                 :team "Team"}
        :menu {:home "home"
               :vision "our vision"
               :boutique  {:_ "on-line boutique"
                           :shelves "shelves"
                           :bookcases "Bookcases"
                           ;; TODO: How to indicate forthcoming products??
                           :tables "tables"
                           :lamps "lamps"
                           :chairs "chairs"
                           :paint "paint"}
               :service "at your service"
               :system "the system"
               :gallery "gallery"
               :blog "blog"
               :feedback "feedback"}
        :copyright ["Copyright 2014 SISTEMI MODERNI."
                    "All rights reserved."]

        ;; general misc
        :contact "contact"
        :email "email"
        :message "message"
        :tbd "TBD"

        ;; design
        :design {:toggle-background "background"}

        ;; cart/order related
        :total "total"
        :subtotal "subtotal"
        :shipping "shipping"
        :item "item"
        :quantity "quantity"
        :price "price"
        :unit_price "unit price"
        :copy "copy"
        :edit "edit"
        :delete "delete"
        :payment "Payment"
        :continue "Next"
        :pre-tax "pre tax"
        :tax-inc "tax inc"
        :tax "tax"

        ;; GALLERY
        :gallery {:intro "With Sistemi Moderni you can personalize everything we have to offer.  Why not start with our screwless shelving solutions?  601,920 dimensions and 213 lacquering colors gives you the power to fulfill all of your shelving needs!"}
        }

   :fr {;; page
        :title "Meubles Étagères Moderne : Sistemi Moderni"

        ;; layout
        :beta "beta"
        :webgl_recommended "Ce site est optimisé pour une visualisation avec rendu WebGL activé."
        :canvas_required "Le bon fonctionnement de ce site nécessite HTML5 canvas, ou WebGL."
        :recommended_browsers "Nous recommandons des navigateurs comme <a href='http://www.mozilla.org'>Firefox</a> ou <a href='http://www.google.com/chrome'>Chrome</a>."
        :javascript_required "Ce site nécessite JavaScript pour son fonctionnement."

        :account "Mon Compte"
        :cart {:_ "Panier"
               :add "Ajouter au panier"
               :update "Réactualiser panier"}

        :header {:signup "S’abonner aux news"
                 :contact "Nous contacter"
                 :careers "Les offres d’emploi"
                 :team "L’équipe"}
        :menu {
               :home "Accueil"
               :vision "Notre Vision"
               :boutique {:_ "Boutique En Ligne"
                          :shelves "Etagères"
                          :bookcases "Bibliothèques"
                          :tables "Tables"
                          :lamps "Eclairage"
                          :chairs "Chaises"
                          :paint  "Peinture"}
               :service "A Votre Service"
               :system "Le Système"
               :gallery "Gallérie"
               :blog "blog"
               :feedback "Vos Impressions"}
        :copyright ["© 2014 SISTEMI MODERNI."
                    "(all rights reserved)"]

        ;; general
        :contact "contacter"
        :email "email"
        :message "message"
        :tbd "Reste à voir."

        ;; design
        :design {:toggle-background "l’arrière-plan"}

        ;; cart/order related
        :subtotal "Sous-total"
        :item "article"
        :quantity "quantité"
        :price "prix"
        :unit_price "prix unitaire"
        :copy "copier"
        :edit "modifier"
        :delete "supprimer"
        :payment "Paiement"
        :continue "Suivant"
        :pre-tax "HT"
        :tax-inc "TTC"
        :shipping "livraison"
        :tax "T.V.A"
        }

   :it {;; page
        :title ""

        ;; layout
        :beta "beta"
        :webgl_recommended "Questo sito è ottimizzato per una visualizzazione con WebGL rendering attivato."
        :canvas_required "Il buon funzionamento di questo sito richiede HTML5 canvas, o WebGL."
        :recommended_browsers "Raccomandiamo browsers come <a href='http://www.mozilla.org'>Firefox</a> o <a href='http://www.google.com/chrome'>Chrome</a>."
        :javascript_required "Questo sito richiede JavaScript per funzionare."

        :account ""
        :cart {:_ ""
               :add "Aggiungere al carrello"}
        :header {:signup "Iscriversi per ricevere emails"
                 :contact "Contatti"
                 :careers "Opportunità di carriera"
                 :team "La squadra"}
        :menu {:home "Home"
               :vision "La nostra visione"
               :boutique {:_ "Boutique online"
                          :shelves "Scaffali"
                          :bookcases "Libreria"}
               :service ""
               :system "Il Sistema"
               :gallery ""
               :blog "blog"
               :feedback "Feedback"}
        :copyright ["@ 2014 SISTEMI MODERNI."
                    "(all rights reserved)"]

        ;; general - cart/order related
        :subtotal "totale parziale"
        :item "articolo"
        :quantity "quantità"
        :price "prezzo"
        :unit_price "prezzo unitario"
        :edit "modifica"
        :copy "copia"
        :delete "annulla"
        }

   :es {;; page
        :title "Muebles de Estantería Moderna : Sistemi Moderni"

        ;; layout
        :version "versión alpha"
        :account "Mi Cuenta"
        :cart "Carro"
        :signup "Recibir Correos"
        :contact "Contáctenos"
        :menu {:home "hogar"
               :vision "nuestro visión"
               :boutique {:_ "boutique en línea"
                          :bookcase "estantaría"
                          :tables "mesas"
                          :lamps "lámparas"
                          :chairs "sillas"
                          :paint "pintura"}
               :service "a su servicio"
               :system "el sistema"
               :gallery "galería"
               :blog "blog"
               :feedback "sugerencias"}
        :copyright ["© 2014 SISTEMI MODERNI."
                    "Todos los derechos reservados."]}
   })

(def head
  (seq []))

(defn scaled-image
  [px file]
  (let [base (-> file (p/unqualify "www/raw") (p/parent))
        name (p/name file)]
    (str (p/join base px name))))

(defn img-thumb
  [file]
  (scaled-image "200" file))

(defn img-large
  [file]
  (scaled-image "630" file))

(defmulti gallery-image
  "Returns an img element for a product image."
  :type)

(defmethod gallery-image :default
  [{:keys [type file] :as item}]
  ;; (prn "ITEM" item)
  [:a {:href (tr/localize "/development.htm" {:query {:type (name type)
                                                      :image (img-large file)}})}
   [:img.gallery-image {:src (img-thumb file)}]])

(defmethod gallery-image :bookcase
  [{:keys [file params]}]
  [:a {:href (tr/localize "/bookcase.htm" {:query (map-vals str params)})}
   [:img.gallery-image {:src (img-thumb file)}]])

(defmethod gallery-image :shelf
  [{:keys [file params]}]
  [:a {:href (tr/localize "/shelf.htm" {:query (map-vals str params)})}
   [:img.gallery-image {:src (img-thumb file)}]])

(defn gallery-section
  "Returns an h2 element for a product category."
  [section & opts]
  (list
   [:h2 section]
   (->> (apply g/get-images section opts)
        (partition 3)  ; truncate to 3 items per row
        (take 3)       ; truncate to 3 rows (9 items) total
        flatten
        (map gallery-image))))

(defn gallery-modulo-image
  [path]
   [:img.gallery-image {:src (p/unqualify path "www/raw")
                        :width "630px"}])

(defn gallery-section-modulo
  []
  (let [section "modulo"]
    (list
     [:h2 section]
     [:a {:href "modulo_v1/index_local.html"}
      (map gallery-modulo-image
           (-> section g/gallery-dir p/files))])))

(defn body []
  [:div#gallery

   [:div [:img.header-image {:src "graphics/gallery-intro.jpg"}]]

   (gallery-section :bookshelves :compare-fn g/compare-volume)
   (gallery-section :single-shelf-systems)
   (gallery-section-modulo)
   (gallery-section :credenzas)
   (gallery-section :credenzas-classic)
   (gallery-section :cupboards)
   (gallery-section :nata)
   (gallery-section :oasi)

   ])

(defn handle
  [req]
  (-> (response (layout/standard-page head (body) 544))
      ;; Set the content type explicitly since this is served from / and has no .htm extension.
      (content-type "text/html; charset=utf-8")))
