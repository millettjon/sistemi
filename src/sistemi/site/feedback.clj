(ns sistemi.site.feedback
  (:require [clojure.tools.logging :as log]
            [ring.util.response :as resp]
            [sistemi.translate :as tr]
            [sistemi.form :as sf]
            [www.form :as f]
            [postal.core :as postal])
  (:use [locale.core :only (full-locale)]
        [ring.util.response :only (redirect)]
        app.config))

(def feedback-mailer
  "Agent to forward feedback email in the background."
  (agent nil
         :error-handler (fn [a x]
                          (log/error "Exception while forwarding feedback email: " x))))


;; TODO: Store Heroku system properties for staging in encrypted gpg.
;;       ? what gpg keys are needed?
;;           - production sysadmin key
;;           - staging sysadmin key
;;           - test shared key for developers
;;             - to where? test sink address?

;; ? what should go in the conf?
;;   - password
;; redacted/harpocrates
;; ? how can the passwords be redacted when configuration gets logged at startup?
;;   metadata :private? :secure
;; ? switch conf back to clojure literals and drop yaml?



(defn mail-feedback
  "Forwards a feedback email."
  [a message]
  (let [result (postal/send-message ^{:host "smtp.gmail.com"
                                      :user "jarvis@sistemimoderni.com"
                                      :pass (conf :jarvis :password)
                                      :ssl true}
                                    {:from "jarvis@sistemimoderni.com"
                                     :to "jon@sistemimoderni.com"
                                     :subject "Feedback submission from website"
                                     :body message})]
    (case (:error result)
      :SUCCESS (log/info "Forwarded form feedback via email.")
      (log/error "Error forwarding feedback mail: " result))))

(def names
  {:es "sugerencias"})

(defn handle
  [req]
  (f/with-form sf/feedback (:params req)
    (if-not (f/errors?)
      (let [message (:message (f/values))]
        (log/info {:feedback message})
        ;; TODO: Persist feedback to database.
        (send-off feedback-mailer mail-feedback message))
      (log/warn "Ignored feedback message (too long)."))
    (resp/redirect (tr/localize "feedback/thanks.htm"))))

(sistemi.registry/register)
