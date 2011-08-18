(ns paypal.express-checkout
  "Functions for working with Paypal Express Checkout."
  (:require [clojure.tools.logging :as log]
            [paypal.nvp :as nvp]))

;; TODO: Figure out how to load configuration settings from files and db.

;; TODO: Figure out how to securely store passwords.
;;  - keys are stored in the database
;;    - production keys in production database
;;    - dev keys in dev database
;;  - cipher text is stored in git along w/ regular dev/staging/prod configuration data
;;  - make sure keys can be rotated and have a policy
;;  - thread model: trust database more than github and developers etc
;;  - sysadmin maintains the keys to the castle
;;  - decryption is done in java/clojure (what libs exist for this?)
;;    - http://www.wikijava.org/wiki/Secret_key_cryptography
;;    - review colin percival's presentation (all you need to know about crypto in one hour)
;;    - http://stackoverflow.com/questions/992019/java-256bit-aes-encryption

;; TODO: Why isn't PAYMENTREQUEST_0_PAYMENTACTION required?
;; TODO: Add unit tests.

;; ? Where can the latest version number be found?
;; - https://www.x.com/people/PP_MTS_Chad/blog/2010/06/22/checking-for-the-most-recent-version
;; - https://www.x.com/thread/36729
;; - the live and test sites may be on different versions
;; - view the source and search for "web version:" on:
;;   - https://www.paypal.com          live
;;   - https://www.sandbox.paypal.com  test
;; ? Where are the release notes?
;; https://www.x.com/community/ppx/release_notes/blog


(defn set-express-checkout
  [params]
  (nvp/client "SetExpressCheckout" params))

(def sale-data {:PAYMENTREQUEST_0_AMT "19.95"
                :RETURNURL "https://www.sistemimoderni.com/en/confirm.htm"
                :CANCELURL "https://www.sistemimoderni.com/en/cancle.htm"})



;; <form method=post action=https://api-3t.sandbox.paypal.com/nvp> 
;; 		<input type=hidden name=USER value=API_username> 
;; 		<input type=hidden name=PWD value=API_password> 
;; 		<input type=hidden name=SIGNATURE value=API_signature> 
;; 		<input type=hidden name=VERSION value=XX.0> 
;; 		<input type=hidden name=PAYMENTREQUEST_0_PAYMENTACTION 
;; 			value=Sale> 
;; 		<input name=PAYMENTREQUEST_0_AMT value=19.95> 
;; 		<input type=hidden name=RETURNURL 
;; 			value=https://www.YourReturnURL.com> 
;; 		<input type=hidden name=CANCELURL 
;; 			value=https://www.YourCancelURL.com> 
;; 		<input type=submit name=METHOD value=SetExpressCheckout> 
;; 	</form>
