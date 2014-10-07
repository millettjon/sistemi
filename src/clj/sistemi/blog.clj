(ns sistemi.blog
  "Scrapes the wordpress blog."
  (:require [net.cgrand.enlive-html :as html]
            [clojure.contrib.core :as contrib]
            [clojure.core.cache :as cache]
            [www.url :as u]
            [util.path :as p]
            [app.config :as cf]
            [ring.util.response :as r]
            [sistemi.layout :as l]))

(def base-url
  (-> (cf/conf :blog-uri)
      u/new-URL))

(defn fetch-url [url]
  (prn "FETCHING URL" url)
  (html/html-resource (java.net.URL. url)))

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

;; - strip scheme, host, and port
;; https://blog1.sm1.in/en/blog/looking-for-a-multi-lingual-community-manager
;; -> en/blog/looking-for-a-multi-lingual-community-manager
(defn url-from-blog-to-site
  "Converts the url from the blog site's to the main site's format."
  [url]
  (-> url
      u/new-URL
      (assoc :scheme nil :host nil :port nil)
      ;; (assoc :path (p/new-path ""))
      ))

(defn fix-link
  [node]
  (update-in node [:attrs :href] #(str (url-from-blog-to-site %))))

;; <a href="https://blog1.sm1.in/en/blog/looking-for-a-multi-lingual-community-manager">Looking for a multi-lingual community manager</a>
(defn fix-urls
  [node]
  (html/at node
           [:h2 :a] fix-link ; post title
           [:p.categories :a] fix-link
           [:div.pagination :a] fix-link

           ;;[:h1.entry-title :a] fix-link
           ;;[:span.entry-date :a] fix-link
           ;;[[:span :.author :.vcard] :a] fix-link
           ;;[:span.tag-links :a] fix-link
           ;;[:div.nav-links :a] fix-link ; on post page
           ))

(defn fix-date
  [node]
  (html/at node
           [:p.date] (html/prepend (html/html [:i.fa.fa-clock-o]) " ")
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
  (-> (str (req :locale) "/" (req :uri))
      (u/qualify base-url)
      ;; Create the blog url using the same query string.
      (assoc :query (req :params))))

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

(defn fix-container
  [node]
  (html/at node
           [:div.large-9] #(-> %
                               (assoc-in [:attrs :class] nil)
                               (assoc-in [:attrs :id] "content"))))

(defn fix-pagenation
  [node]
  (html/at node
           ;; Change <strong> tag to a <span class='current-page'>
           [:div.pagination :strong] #(assoc %
                                        :tag :span
                                        :attrs {:class "current-page"})
           ;; Rename pagination class to avoid conflict with bootstrap's pagination class.
           [:div.pagination] #(assoc-in % [:attrs :class] "blog-pagination")))

(defmethod convert-content :default
  [url]
  (println "CONVERT-CONTENT :DEFAULT")
  (-> url
      str
      fetch-cached-url
      (html/select [:div.large-9])
      fix-container
      fix-urls
      fix-pagenation

      ;; fix-date
      ;; fix-author
      ;; fix-tag
      render
      ))

(defmethod convert-content :p
  [url]
  (println "CONVERT-CONTENT :P")
  (-> url
      str
      fetch-cached-url
      ;;(html/select [:div#content])
      ;;(html/select [:div.row])
      ;; strip-comment-links
      ;; strip-respond
      ;; fix-urls
      ;; fix-date
      ;; fix-author
      ;; fix-tag
      ;; delete-screen-reader-text
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
      (html/select [:div.large-3])
#_      (html/at [:aside#icl_lang_sel_widget] nil
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

#_ (defn wrap-blog
  "Calls a handler if one is defined for the current URI. Otherwise delegates to the next
   middleware."
  [app]
  (fn [req]
    (let [uri-parts (path/split (req :uri))]
      (if-let [handler (get-in registry/handlers (concat uri-parts [:handler]))]
        (handler req)
        (app req)))))

(def head
  [:link {:rel "stylesheet" :href "/css/blog.css" :type "text/css"}])

;; no content type?
(defn handle
  [req]
  (-> (l/standard-page head (body req) (sidebar req))
      r/response
      (r/content-type "text/html; charset=utf-8")))
