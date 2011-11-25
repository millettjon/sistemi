(ns paypal.version
  "Scrape and verify paypal's api version numbers."
  (:require [clj-http.client :as client])
  (:use [util.except :only (check)]))

(defn scrape-version
  "Scrapes the paypal api version from a url.
References:
- https://www.x.com/people/PP_MTS_Chad/blog/2010/06/22/checking-for-the-most-recent-version
- https://www.x.com/thread/36729
"
  [url]
  (let [resp (client/get url)]
    (last (re-find #"web version: (\S+)" (:body resp)))))

(def scrape-urls
  "Paypal URLs to scrape to determine active API version numbers."
  {:test "https://www.sandbox.paypal.com"
   :production "https://www.paypal.com"})

(defn get-version
  "Returns the paypal version number for either the :live or :sandbox site."
  [site]
  (scrape-version (check (site scrape-urls))))
