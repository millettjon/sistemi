(ns mail.mandrill
  (:require [app.config :as c]
            [clj-mandrill.core :as m]
            [schema.core :as s]
            [schema.core :as sm]
            [taoensso.timbre :as log]
            [net.cgrand.enlive-html :as html]
            [util.except :as ex]
            [mail.css :as css]
            [sistemi.translate :as tr]
            [sistemi.order :as o]
            [sistemi.registry :as registry]
            [www.url :as url])
  (:refer-clojure :exclude [send]))

;; REFENCES
;; sending messages:      https://mandrillapp.com/api/docs/messages.html#method=send
;; html email guidelines: http://blog.mailchimp.com/css-fixer-for-html-email/
;; css client support:    http://www.campaignmonitor.com/css/
;; css inlining:          https://github.com/dlanger/inlinestyler
;;                        http://inlinestyler.torchboxapps.com/
;; previewing:            http://litmus.com/email-testing
;;                        https://www.emailonacid.com/
;;                        http://www.contactology.com/email-view.php
;; 
;; https://github.com/advancedrei/BootstrapForEmail
;; https://news.ycombinator.com/item?id=3972571
;;
;; CONSTRAINTS
;; - should work with or without images
;; - should have consistent look as the main site and any mailchimp templates
;; - css should be inlined (mandrill can do this for < 256k emails)
;; - should have a link to view in the website
;; - should be printable
;;
;; QUESTIONS
;; ? Should we create templates in mandrill/mailchimp?
;; ? Can html be inserted into the templates? e.g., use the template as a frame and we insert content as a block.
;; ? Do transactional emails need an unsub link?
;; ? Should we send in text format as well as html?
;;
;; TODO
;; - Log the mail response.
;; - Setup an email alias to handle responses: support
;; - Translate the subject and support international chars.
;; - Use a future to send and log with a timeout.
;; - Test to test sending a message (how to capture message? external mail receiver? gmail?)
;; - Send mail using html.
;; - Handle mandrill outages by buffering to a persistent queueing mechanism.
;;   - message queue
;;   - use clojurewerkz/mailer to send to local mail server that sends to Mandril via SMTP/TLS
;;

(def ^:private api-key
  (c/conf :mandrill :api :key))

;; Set the api key once at the top level.
(alter-var-root #'m/*mandrill-api-key* (constantly api-key))

(defn ping
  []
  (m/ping))

;; ? What response does mandrill return?
;;   ({:email "jon@sistemimoderni.com", :status "sent", :_id "ac47b4142038402a9f83008523a5af41", :reject_reason nil})
;; ? How to check for a successful result?
;; ? how should exceptions be handled?
;; log the mail response
;;   ? will this work if run in a future?
;;     ? does *req* binding get passed?

(def Email
  "schema - email address"
  {:email s/Str (s/optional-key :name) s/Str})

(def Message
  "schema - email message"
  {:from Email
   :to [Email]
   :subject s/Str
   (s/optional-key :text) s/Str
   :html s/Str})

(defn success?
  "Returns true if the response indicates a succesful request."
  [response]
  (not= "error" (:status response)))

(defn sent?
  "Returns true if the mail was delivered to a single address."
  [mail]
  (->> mail
      :status
      (contains? #{"sent" "queued" "scheduled"})))

(sm/defn ^:always-validate send
  "Sends a mail message using Mandrill."
  [{:as message {:keys [name email]} :from} :- Message]
  (let [message (-> message
                    (dissoc :from)
                    (assoc :from_email email
                           :from_name name)
                    (assoc :inline_css true)
                    )]
    (future
      (log/info {:event :mandrill/send-request :data message})
      (ex/swallow
        (let [result (m/send-message message)
              log-data {:event :mandrill/send-response :data result}]
          (if (success? result)
            (do (log/info log-data)
                (doseq [mail result]
                  (if-not (sent? mail)
                    (log/error {:event :mandrill/send-address :data mail}))))
            (log/error log-data))
          result)

        ;; log exception and return it
        (partial ex/log :mandrill/send-exception)))))


;; See: http://patorjk.com/software/taag/#p=display&f=Standard&t=SistemiModerni
(def text-header
"______________________________________________________________________
 ____  _     _                 _ __  __           _                 _ 
/ ___|(_)___| |_ ___ _ __ ___ (_)  \\/  | ___   __| | ___ _ __ _ __ (_)
\\___ \\| / __| __/ _ \\ '_ ` _ \\| | |\\/| |/ _ \\ / _` |/ _ \\ '__| '_ \\| |
 ___) | \\__ \\ ||  __/ | | | | | | |  | | (_) | (_| |  __/ |  | | | | |
|____/|_|___/\\__\\___|_| |_| |_|_|_|  |_|\\___/ \\__,_|\\___|_|  |_| |_|_|
______________________________________________________________________
                                                                      
")
#_ (println text-header)


(def base-url
  "Base url used to qualify links in email."
  (str "https://" (c/conf :host) "." (c/conf :domain))
  ;;(str "https://www.sm1.in")
  )

(defn qualify-href
  [n] (update-in n [:attrs :href] #(url/qualify % base-url)))

(defn qualify
  [node]
  (html/at node
           [:a] qualify-href
           [:link] qualify-href))

(defn a-target-blank
  [node]
  (html/at node
           [:a] (fn [n] (assoc-in n [:attrs :target] "_blank"))))

(defn render
  [nodes]
  (->> nodes
       html/emit*
       (apply str)))

#_ ((:handler (registry/get-handler "/order/confirmation.htm")) {})
#_ ((registry/get-handler "/order/confirmation.htm") {})
#_ (sistemi.site.order.confirmation-htm/handle {})

(defn page->email
  "Renders html for page using translations for locale and post processes the result for use in an email."
  [locale page & [params]]
  (tr/with-page locale page
    (-> {:params (or params {})}
        (assoc-in [:params :format] "email")

        ;; render html
        ((registry/get-handler page))
        :body
        hiccup.core/html

        ;; apply enlive transformations
        html/html-snippet  ; Parse html.
        a-target-blank     ; Use target _blank to open links in a new window.
        qualify            ; Qualify relative urls.
        render             ; Render back to html.

        css/inline
        sistemi.layout/doctype-xhtml-strict
        )))

(defn order-confirmation-subject
  [order]
  (str "SistemiModerni - Order #" (:id order)))

(defn send-order-confirmation
  "Sends an order confirmation email."
  [order]
  (let [message {:from {:email (c/conf :email :orders)}
                 :to [(select-keys (:contact order) [:name :email])]
                 :subject (order-confirmation-subject order)
                 :html (page->email (:locale order)
                                    "/order/confirmation.htm"
                                    {:id (:id order)})}]
    (send message)))

#_ (require 'sistemi.order-test)
#_ (time (-> (o/create sistemi.order-test/cart-data {:locale "fr" :payment-txn "--stripe data--"})
             send-order-confirmation
             deref))

;; https://localhost.sm1.in:/en/order/confirmation.htm?id=8BJLR0
;; https://localhost.sm1.in:/en/order/confirmation.htm?format=email&id=8BJLR0

;; - Use the correct doctype
;; - Use a CSS reset
;;   - http://htmlemailboilerplate.com/
;;   - http://templates.mailchimp.com/development/css/reset-styles/
;;   - http://www.emailology.org/#1
;; - Test
;;   - http://www.contactology.com/email-view.php
;;   - http://www.emailonacid.com/
;; - References
;;   - supported css http://www.campaignmonitor.com/css/
;;   - supported elements: http://www.emailology.org/#3
;;   - client tricks and tips: http://www.emailology.org/#2

;; Note: Mandrill encodes international chars in the subject.
;; Note: Mandrill modifies all links to redirect through their tracking.
;; Note: Mandrill adds a tracking pixel to the html content.
