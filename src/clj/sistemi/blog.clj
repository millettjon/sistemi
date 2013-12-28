(ns sistemi.blog
  "Scrapes the wordpress blog."
  (:require [net.cgrand.enlive-html :as html]
            [clojure.contrib.core :as contrib]
            [clojure.core.cache :as cache]
            [www.url :as u]
            [util.path :as p]))

(def base-url
  (-> "https://blog.sm1.in"
      u/new-URL))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

;; (def fetch-url (memoize fetch-url))

(def ^:private cache
  "Cache urls for 1 minute."
  (atom (cache/ttl-cache-factory {} :ttl (* 1                ; min
                                            60               ; sec/min
                                            1000             ; ms/sec
                                            ))))

(defn fetch-cached-url
  "Returns a cached url object by path creating a new one if necessary."
  [url]
  (let [C @cache
        C (if (cache/has? C url)
            (cache/hit C url)
            (swap! cache #(cache/miss % url (fetch-url url))))]
    (clojure.core/get C url)))

(defn strip-comment-links
  "Strips the leave a comment links."
  [node]
  (html/at node
           [:span.comments-link] nil))

(defn strip-respond
  "Strips the respond section from a post."
  [node]
  (html/at node
           [:div#respond] nil))

;; - strip scheme, host, and port and langauge
;; https://blog.sm1.in/?p=31 => /blog?p=31
;; https://blog.sm1.in/?p=31&lang_fr => /blog?p=31
(defn url-from-blog-to-site
  "Converts the url from the blog site's to the main site's format."
  [url]
  (-> url
      u/new-URL
      (assoc :scheme nil :host nil :port nil)
      (contrib/dissoc-in [:query :lang])
      (assoc :path (p/new-path ""))))

(defn fix-link
  [node]
  (update-in node [:attrs :href] #(str (url-from-blog-to-site %))))

(defn fix-urls
  [node]
  (html/at node
           [:span.cat-links :a] fix-link
           [:h1.entry-title :a] fix-link
           [:span.entry-date :a] fix-link
           [[:span :.author :.vcard] :a] fix-link
           [:span.tag-links :a] fix-link
           [:div.nav-links :a] fix-link ; on post page
           ))

(defn fix-date
  [node]
  (html/at node
           [:span.entry-date :a] (html/prepend (html/html [:i.fa.fa-clock-o]) " ")
           ))

(defn fix-author
  [node]
  (html/at node
           [:span.author.vcard :a] (html/prepend (html/html [:i.fa.fa-user]) " ")
           ))

(defn fix-tag
  [node]
  (html/at node
           [:span.tag-links :a] (html/prepend (html/html [:i.fa.fa-tag]) " ")
           ))

(defn delete-screen-reader-text
  [node]
  (html/at node
           [:.screen-reader-text] nil
           ))


(defn url-from-site-to-blog
  "Converts the request's url from the main site's to the blog site's format."
  [req]
  (-> base-url
      ;; Create the blog url using the same query string w/ the lang injected.
      (assoc :query (merge (req :params) {:lang (req :locale)}))))

(defn render
  [nodes]
  (->> nodes
       html/emit*
       (apply str)))

(defmulti convert-content
  "Converts content from wordpress to the main site's format."
  (fn [url]
    (let [q (:query url)]
      (cond (:p q) :p
            ))))

(defmethod convert-content :default
  [url]
  (-> url
      str
      fetch-cached-url
      (html/select [:div#content])
      strip-comment-links
      fix-urls
      fix-date
      fix-author
      fix-tag
      render
      ))

(defmethod convert-content :p
  [url]
  (-> url
      str
      fetch-cached-url
      (html/select [:div#content])
      strip-comment-links
      strip-respond
      fix-urls
      fix-date
      fix-author
      fix-tag
      delete-screen-reader-text
      render
      ))

(defn body
  "Returns the body portion of a blog page."
  [req]
  (-> req
      url-from-site-to-blog
      convert-content))

(defn convert-sidebar
  [url]
  (-> url
      str
      fetch-cached-url
      (html/select [:div#primary-sidebar])
      (html/at [:aside#icl_lang_sel_widget] nil
               [:aside#search-2] nil
               [:aside#recent-comments-2] nil
               [:aside#meta-2] nil
               [:li :a] fix-link
               )
      render))

(defn sidebar
  "Returns the sidebar potion of a blog page."
  [req]
  (-> req
      url-from-site-to-blog
      convert-sidebar))
