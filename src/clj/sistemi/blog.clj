(ns sistemi.blog
  "Scrapes the wordpress blog."
  (:require [net.cgrand.enlive-html :as el]
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
  ;; (prn "FETCHING URL" url)
  (el/html-resource (java.net.URL. url)))

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
  (el/at node
           [:span.comments-link] nil))

(defn strip-respond
  "Strips the respond section from a post."
  [node]
  (el/at node
           [:div#respond] nil))

;; - strip scheme, host, and port
;; https://blog1.sm1.in/en/blog/looking-for-a-multi-lingual-community-manager
;; -> en/blog/looking-for-a-multi-lingual-community-manager
(defn url-from-blog-to-site
  "Converts the url from the blog site's to the main site's format."
  [url]
  (let [u (u/new-URL url)]
    (if (= (:host base-url) (:host u))
      (assoc u :scheme nil :host nil :port nil) ;; strip scheme, host and port
      url)))

(defn fix-link
  [node]
  (update-in node [:attrs :href] #(str (url-from-blog-to-site %))))

;; <a href="https://blog1.sm1.in/en/blog/looking-for-a-multi-lingual-community-manager">Looking for a multi-lingual community manager</a>
(defn fix-urls
  [node]
  (el/at node
           [:a] fix-link
           ))

(defn fix-date
  [node]
  (el/at node
           [:p.date] (el/prepend (el/html [:i.fa.fa-clock-o]) " ")
           ))

(defn fix-author
  [node]
  (el/at node
           [:span.author.vcard :a] (el/prepend (el/html [:i.fa.fa-user]) " ")
           ))

(defn fix-tag
  [node]
  (el/at node
           [:span.tag-links :a] (el/prepend (el/html [:i.fa.fa-tag]) " ")
           ))

(defn delete-screen-reader-text
  [node]
  (el/at node
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
       el/emit*
       (apply str)))

(defmulti convert-content
  "Converts content from wordpress to the main site's format."
  (fn [url]
    (let [q (:query url)]
      (cond (:p q) :p
            ))))

(defn fix-container
  [node]
  (el/at node
           [:div.large-9] #(-> %
                               (assoc-in [:attrs :class] nil)
                               (assoc-in [:attrs :id] "content"))))

(defn fix-pagenation
  [node]
  (el/at node
           ;; Change <strong> tag to a <span class='current-page'>
           [:div.pagination :strong] #(assoc %
                                        :tag :span
                                        :attrs {:class "current-page"})
           ;; Rename pagination class to avoid conflict with bootstrap's pagination class.
           [:div.pagination] #(assoc-in % [:attrs :class] "blog-pagination")))

;; Move the "Continue Reading ..." anchor to the end of the first paragraph.
(defn fix-continue
  [node]
  (el/at node
         [:div.post-list] (el/move [:a.continue-reading] [:div.article-first-para :p] el/append)))

(defn fix-article-image
  [node]
  (el/at node
         [:div.post-list] (fn [node]
                            (let [article-url (-> [node]
                                                  (el/select [:h2 :a])
                                                  first
                                                  :attrs
                                                  :href)]
                              ;; (prn "ARTICLE-URL" article-url)
                              
                              (el/at node
                                     [:img.article-image] (el/wrap :a {:href article-url}))))))

;; - find url from here: div.post-list h2 a
;; - wrap image in a tag 

(defmethod convert-content :default
  [url]
  (println "CONVERT-CONTENT :DEFAULT")
  (-> url
      str
      fetch-cached-url
      (el/select [:div.large-9])
      fix-container
      fix-urls

      ;; blog page specific
      fix-pagenation
      fix-continue
      fix-article-image

      render
      ))

(defn body
  "Returns the body portion of a blog page."
  [req]
  (-> req
      url-from-site-to-blog
      convert-content))

;; Remove the 3rd and 4th children.

(defn convert-sidebar
  [url]
  (-> url
      str
      fetch-cached-url
      (el/select [:div.large-3])

      ;; Delete the Tags sections for now.
      (el/at [[:div.side-block #{(el/nth-child 3) (el/nth-child 4)} ]] nil)

      fix-urls
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
