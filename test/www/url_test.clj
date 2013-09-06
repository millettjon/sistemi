(ns www.url-test
  (:require [util.path :as path]
            [www.url :as url])
  (:use [clojure.test]))

(deftest encode-path-part-test
  (are [s result] (= (url/encode-path-part s) result)
       ""           ""
       "estantería" "estanter%C3%ADa"))

(deftest encode-path-test
  (are [s result] (= (str (url/encode-path s)) result)
       "estantería" "estanter%C3%ADa"
       "/" "/"
       "" ""
       "abc/def" "abc/def"
       "/abc/def" "/abc/def"))

(deftest query-to-str-test
  (are [a b] (= (url/query-to-str a) b)
       {} ""
       {:a "A"} "a=A"
       {:a "A" :b "B"} "a=A&b=B"))

(deftest encode-query-part-test
  (are [s result] (= (url/encode-query-part s) result)
       ""           ""
       "estantería" "estanter%C3%ADa"))

(deftest encode-query-test
  (are [s result] (= (url/query-to-str (url/encode-query s)) result)
       {:a23 "bí"} "a23=b%C3%AD"))

(deftest new-URL-string-test
  (are [s result] (= (url/new-URL s) (url/new-URL result))
       ""  {:path (path/new-path "")}
       "/" {:path (path/new-path "/")}
       "abc" {:path (path/new-path "abc")}
       "/foo" {:path (path/new-path "/foo")}
       "http://foo" {:scheme :http :host "foo" :path (path/new-path "/")}
       "http://foo:8080/bar" {:scheme :http :host "foo" :port 8080 :path (path/new-path "/bar")}
       "http://foo/bar" {:scheme :http :host "foo" :path (path/new-path "/bar")}
       "https://foo/bar/baz" {:scheme :https :host "foo" :path (path/new-path "/bar/baz")}
       "https://foo/bar?baz=qux" {:scheme :https :host "foo" :path (path/new-path "/bar") :query {:baz "qux"}}))

(deftest new-URL-ring-map-test
  (are [m result] (= (url/new-URL m) (url/new-URL result))
       {:server-name "foo"} "//foo"
       {:scheme :https :server-name "foo"} "https://foo"
       {:scheme :https :server-name "foo" :server-port 8118} "https://foo:8118"
       {:scheme :https :server-name "foo" :server-port 8118 :uri "/bar"} "https://foo:8118/bar"
       {:scheme :https :server-name "foo" :server-port 8118 :uri "/bar" :query-params {:baz "qux"}} "https://foo:8118/bar?baz=qux"

       ;; The header x-forwarded-proto should be used instead of :scheme when behind a reverse proxy.
       {:scheme :http, :server-name "foo", :uri "/bar", :headers {"x-forwarded-proto" "https"}, :server-port 443} "https://foo/bar"
       ))

;; {:ssl-client-cert nil, :remote-addr "127.0.0.1", :scheme :http, :query-params {}, :session {}, :form-params {}, :request-method :get, :query-string nil, :content-type nil, :cookies {"locale" {:value "en"}}, :uri "/nivo/images/loading.gif", :server-name "www.sistemimoderni.com", :params {}, :headers {"user-agent" "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:23.0) Gecko/20100101 Firefox/23.0", "cookie" "locale=en", "accept" "image/png,image/*;q=0.8,*/*;q=0.5", "host" "www.sistemimoderni.com", "dnt" "1", "x-forwarded-proto" "https", "x-forwarded-for" "190.95.83.124", "referer" "https://www.sistemimoderni.com/nivo/nivo-slider.css", "accept-encoding" "gzip, deflate", "accept-language" "en-us,en;q=0.7,es-cl;q=0.3", "connection" "close"}, :content-length nil, :server-port 80, :character-encoding nil, :body #<HttpInput org.eclipse.jetty.server.HttpInput@10b3697>}

(deftest new-URL-path-test
  (are [a] (= (url/new-URL (path/new-path a)) (url/new-URL a))
       ""
       "/"
       "/foo"
       "/foo/bar")) 

(deftest to-string-test
  (are [s] (= (str (url/new-URL s)) s)
       ""
       "/"
       "abc"
       "/foo"
       "//foo"
       "#"
       "foo#"
       "foo#bar"
       "/foo?bar=baz#ccc"
       "http://foo:8080/bar"
       "http://foo/bar"
       "https://foo/bar/baz"
       "https://foo/bar?baz=qux")
  (are [s result] (= (str (url/encode (url/new-URL s))) result)
       "" ""
       "http://foo:80/bar" "http://foo/bar"
       "http://fubar.com/a/b/estantería?target=/estantería-moderna.htm" "http://fubar.com/a/b/estanter%C3%ADa?target=/estanter%C3%ADa-moderna.htm"))

(deftest parent-test
  (are [a b] (= (url/parent a) (url/new-URL b))
       "foo" ""
       "/foo" "/"
       "http://foo/bar" "http://foo"))

(deftest qualify-test
  (are [a b c] (= (str (url/qualify a b)) c)
       "bar" "http://foo" "http://foo/bar"
       "bar/baz?qux=42" "http://foo" "http://foo/bar/baz?qux=42"
       "baz" "http://foo/bar" "http://foo/bar/baz"
       "baz" "https://foo:8118/bar" "https://foo:8118/bar/baz"))

(deftest match-scheme-test
  (are [a b c] (= (str (url/match-scheme a b)) c)
       "http://www.google.com" "https://foo/bar" "https://www.google.com"))
