(ns paypal.core
  "Functions for working with Paypal Express Checkout."
  (:use paypal.config)
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [paypal.nvp :as nvp]
            [clj-http.client :as client]))

;; References:
;; - Getting Started: https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_ECGettingStarted
;; - Release Notes: https://www.x.com/community/ppx/release_notes/blog
;; - Sample Code:
;;   - https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/library_code
;;   - https://www.paypal-labs.com/integrationwizard/ecpaypal/cart.php
(defn get-version
  "Scrapes the paypal api version from a url."
  [conf]
  (let [url (:version-url ((:site conf) site-conf))
        resp (client/get url)]
    (last (re-find #"web version: (\S+)" (:body resp)))))

(defn xc-setup
  [conf params]
  (nvp/do-request conf "SetExpressCheckout" params))

(defn xc-details
  [conf params]
  (nvp/do-request conf "GetExpressCheckoutDetails" params))

(defn xc-pay
  [conf params]
  (nvp/do-request conf "DoExpressCheckoutPayment" params))
