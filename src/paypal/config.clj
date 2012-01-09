(ns paypal.config
  "Paypal configuration settings.")

;; References:
;; - API Endpoints are defined here: https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/howto_api_endpoints
;; - How to determine the active API version number:
;;   - https://www.x.com/people/PP_MTS_Chad/blog/2010/06/22/checking-for-the-most-recent-version
;;   - https://www.x.com/thread/36729

(def site-conf
  "Configuration for sandox and production sites."
  {:sandbox
   {:url "https://api-3t.sandbox.paypal.com/nvp"
    :version "84.0"
    :version-url "https://www.sandbox.paypal.com"
    :xc-url "https://www.sandbox.paypal.com/webscr"}

   :production
   {:url "https://api-3t.paypal.com/nvp"
    :version "84.0"
    :version-url "https://www.paypal.com"
    :xc-url "https://www.paypal.com/webscr"}})
