(ns locale.middleware.test.translate
  (:require [util.path :as path])
  (:use [clojure.test]
        [locale core translate]
        locale.middleware.translate))

(deftest load-name-translations-test
  (with-redefs [default-locale "en"
                locales #{"en" "es"}]
    (let [[localized canonical] (load-name-translations "test/locale/middleware/test/site")]
      (are [a b c] (= (localize-path localized a (path/new-path b)) (path/new-path c))
           "en" "/" "/en"
           "es" "/" "/es"
           "en" "/profile" "/en/profile"
           "es" "/profile" "/es/perfil"
           "en" "/modern-shelving.htm" "/en/modern-shelving.htm"
           "es" "/modern-shelving.htm"
           "/es/estanter%C3%ADa-moderna.htm")
      (are [a b c] (= (str (canonicalize-path canonical a (path/new-path b))) c)
           "en" "/" "/"
           "es" "/" "/"
           "en" "/profile" "/profile"
           "es" "/perfil" "/profile"
           "en" "/modern-shelving.htm" "/modern-shelving.htm"
           "es" "/estanter%C3%ADa-moderna.htm" "/modern-shelving.htm"))))

#_(with-redefs [default-locale "en"
                locales #{"en" "es"}]
    (let [m (load-name-translations "test/locale/middleware/test/site")]
      (prn m)
      m))
