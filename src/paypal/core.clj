(ns paypal.core
  "Functions for working with Paypal Express Checkout."
  (:use paypal.config)
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [paypal.nvp :as nvp]
            [clj-http.client :as client]))

;; References:
;; - Getting Started: https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_ECGettingStarted
;; - Sample Code:
;;   - https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/library_code
;;   - https://www.paypal-labs.com/integrationwizard/ecpaypal/cart.php
(defn get-version
  "Scrapes the paypal api version from a url."
  [conf]
  (let [url (:version-url ((:site conf) site-conf))
        resp (client/get url)]
    (last (re-find #"web version: (\S+)" (:body resp)))))

;; ===== EXPRESS CHECKOUT NVP WRAPPERS =====
(defn xc-setup
  "Creates a new express checkout transaction."
  [conf params]
  (nvp/do-request conf "SetExpressCheckout" params))

(defn xc-details
  "Returns the details of an express checkout transaction."
  [conf params]
  (nvp/do-request conf "GetExpressCheckoutDetails" params))

(defn xc-pay
  "Completes payment of an express checkout transaction."
  [conf params]
  (nvp/do-request conf "DoExpressCheckoutPayment" params))

;; ===== MISC =====
(defn xc-redirect-url
  "Returns a the redirect url for starting an express checkout interaction."
  [conf token]
  (let [site-conf ((keyword (:site conf)) site-conf)]
        (str (:xc-url site-conf) "?cmd=_express-checkout&token=" token)))

(defn normalize-keys
  "Converts map keys to lowercase keywords."
  [m]
  (reduce (fn [m [k v]] (assoc m (-> k name str/lower-case keyword) v)) {} m))
