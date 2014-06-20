(ns price
  (:require edn
            [goog.net.XhrIo :as xhr]
            [dommy.core  :as dom]
            [cljs.core.async :as async :refer [chan close!]]
            [clojure.string :as str]
            [cemerick.url :as url])
  (:require-macros [cljs.core.async.macros :refer [go alt!]]
                   [dommy.macros :refer [sel sel1]]))

(defn log [s]
  (.log js/console (str s)))

(defn GET
  "Gets a url. Returns the text of the response or an empty string on error."
  [url]
  (let [ch (chan 1)]
    (xhr/send url
              (fn [event]
                (let [res      (.-target event)
                      success? (= 200 (.getStatus res))
                      text     (if success?
                                 (.getResponseText res)
                                 ""
                                 )]
                  (go (>! ch text)
                      (close! ch)))))
    ch))

(defn localize-url
  "Localizes a canonical url using the locale of the current page."
  [cpath]
  (let [uri (-> js/window
                .-location
                goog.Uri.)
        locale (-> uri
                   .getPath
                   (str/split #"/")
                   second)]
    (->> cpath
         (str locale)
         (.setPath uri)
         str)))

(defn ^:export update-price
  "Calls the server to get the price of an item and updates the corresponding dom element."
  [item id]
  (let [item (js->clj item)
        url (-> "/price"
                localize-url
                url/url
                (assoc :query item)
                str)]
    ;; (log url)
    (go
      (let [text (<! (GET url))
            ]
        (dom/set-text! (sel1 (keyword id)) text)))))
