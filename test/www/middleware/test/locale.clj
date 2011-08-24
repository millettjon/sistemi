(ns www.middleware.test.locale
  (:use [clojure.test]
        www.middleware.locale))

(deftest parse-locale-test
  (are [uri locale] (= (@#'www.middleware.locale/parse-locale uri) locale)
       ;; test uri     expected locale
       "/fr/foo"       "fr"
       "/france/foo"   nil
       "/fr/"          "fr"
       "/fr"           "fr"
       "/france"       nil
       "/"             nil
       "/foo"          nil
       "/foo/fr"       nil
       "fr"            nil
       "/foo/fr/"      nil))

(deftest canonicalize-url-test
  (let [dreq {:scheme :http :server-name "foo.com" :server-port 80}]
    (are [req uri result] (= (canonicalize-url (merge dreq req) uri) result)
         ;; request map                     uri    result
         {}                                 ""     "http://foo.com"
         {:server-port 8080}                ""     "http://foo.com:8080"
         {:scheme :https :server-port 443}  ""     "https://foo.com"
         {:scheme :https  :server-port 444} ""     "https://foo.com:444"
         {}                                 "/fr"  "http://foo.com/fr"
         {:scheme :https :server-port 444}  "/en"  "https://foo.com:444/en"
     )))

(deftest parse-accept-language-test
  (are [x y] (= (@#'www.middleware.locale/parse-accept-language {:headers {"accept-language" x}}) y)
       "en,es,fr"                   [[["en"] 1] [["es"] 1] [["fr"] 1]]
       "en-US,es,fr"                [[["en" "US"] 1] [["es"] 1] [["fr"] 1]]
       "en-a-b-c-d"                 [[["en" "a" "b" "c" "d"] 1]]
       "en;q=0.3"                   [[["en"] 0.3]]
       "en,fr;q=0.3,es;q=0.8,de"    [[["en"] 1] [["de"] 1] [["es"] 0.8] [["fr"] 0.3]])
  (is (= (@#'www.middleware.locale/parse-accept-language {:headers {}}) nil)))


(deftest detect-locale-test
  (are [x y] (= (@#'www.middleware.locale/detect-locale {:headers {"accept-language" x}}) y)
       "it"                         :it
       "fo"                         default-locale
       "fr-ab"                      :fr
       "fr-ab;q=0.8"                :fr
       "fr-ab;q=0.8,en"             :en
       "fr-ab;q=0.8,en=0.7"         :fr
       "fr-ab;q=0.8,en,it"          :en
       "fr-ab;q=0.8,it,en"          :it
       "fr-ab;q=0.8,de,xy,it,en"    :it
       "es-cl,en-us;q=0.7,en;q=0.3" :es
       )
  (is (= (@#'www.middleware.locale/detect-locale {:headers {}}) default-locale)))

(deftest wrap-locale-test
  (let [f (wrap-locale identity)
        req {:request-method :get :scheme :http
             :server-name "foo.com" :server-port 80 :uri "/"}]

    ;; test that the locale is set
    (are [x y] (= (:locale (f (merge req x))) y)
         {:uri "/en"}                             "en"
         {:uri "/en/xyz"}                         "en"
         {:uri "/en/xyz?a=b"}                     "en"
         {:request-method :head :uri "/en/xyz"}   "en")

    ;; test redirects
    (are [x y] (= (get-in (f (merge req x)) [:headers "Location"]) y)
         {} "http://foo.com/en/"        ; should this redirect to /en instead of /en/ ?
         {:uri "/foo"} "http://foo.com/en/foo"
         {:uri "/foo/bar?a=b"} "http://foo.com/en/foo/bar?a=b"
         {:request-method :head :uri "/foo/bar?a=b"} "http://foo.com/en/foo/bar?a=b"
         {:request-method :post :uri "/foo/bar?a=b"} "http://foo.com/en")))
