(ns analytics.google
  (:require [selmer.parser :as selmer]))

;; See: https://developers.google.com/analytics/devguides/collection/gajs/asyncTracking
;; Note: modified to always use https
(defn analytics
  "Inserts google analytics script"
  [{:keys [enabled? account]}]
  (if enabled?
    [:script {:type "text/javascript"}
     (format
      "var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '%s']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = 'https://ssl.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();" account)]))
