(ns locale.test.ring
  (:use [clojure.test]
        (locale core ring)))

(deftest parse-accept-language-test
  (are [x y] (= (@#'locale.ring/parse-accept-language {:headers {"accept-language" x}}) y)
       "en,es,fr"                   [[["en"] 1] [["es"] 1] [["fr"] 1]]
       "en-US,es,fr"                [[["en" "US"] 1] [["es"] 1] [["fr"] 1]]
       "en-a-b-c-d"                 [[["en" "a" "b" "c" "d"] 1]]
       "en;q=0.3"                   [[["en"] 0.3]]
       "en,fr;q=0.3,es;q=0.8,de"    [[["en"] 1] [["de"] 1] [["es"] 0.8] [["fr"] 0.3]])
  (is (= (@#'locale.ring/parse-accept-language {:headers {}}) nil)))

(deftest detect-locale-test
  (are [x y] (= (@#'locale.ring/detect-locale {:headers {"accept-language" x}}) y)
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
  (is (= (@#'locale.ring/detect-locale {:headers {}}) default-locale)))
