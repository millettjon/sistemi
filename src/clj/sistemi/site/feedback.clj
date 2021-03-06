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

(defn mail-feedback
  "Forwards a feedback email."
  [a {:keys [email message] :as feedback}]
  (let [email (if-not (empty? email) email)
        jarvis (conf :jarvis :user)
        result (postal/send-message ^{:host (conf :jarvis :host)
                                      :user (conf :jarvis :user)
                                      :pass (conf :jarvis :password)
                                      :ssl true}
                                    {:from jarvis
                                     :to (conf :email :info)
                                     :reply-to (or email jarvis)
                                     :subject (str "Feedback from " (or email "website"))
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
      (let [feedback (f/values)]
        (log/info {:feedback feedback})
        (send-off feedback-mailer mail-feedback feedback))
      (log/warn "Ignored feedback message (too long)."))
    (resp/redirect (tr/localize "feedback/thanks.htm"))))
