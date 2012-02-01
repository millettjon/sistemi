(let
    ;; Hack to support a local file based maven repository on heroku.
    [local-repo (str "file://" (System/getProperty "user.dir") "/repo")]

    (defproject sistemi "0.1"
      :description "Sistemi Moderni Website"
      :repositories {"local" {:url ~local-repo
                              :snapshots false
                              :releases {:checksum :ignore :update :always}}}

      :dependencies [
                     ;; clojure
                     [org.clojure/clojure "1.3.0"]
                     [slingshot "0.10.0"]

                     ;; logging
                     [clj-logging-config "1.9.5"]
                     [org.slf4j/slf4j-api "1.6.4"]
                     [org.slf4j/slf4j-log4j12 "1.6.4"]
                     [log4j "1.2.16"]

                     ;; web server
                     [ring/ring-core "1.0.1"]
                     [ring/ring-devel "1.0.1"]
                     [ring/ring-jetty-adapter "1.0.1"]
                     [net.cgrand/moustache "1.1.0"]
                     [ring-persistent-cookies "0.1.0"]

                     ;; html
                     [enlive "1.0.0"]
                     [hiccup "0.3.7"]
                     [sistemi/gdata-core-minimal "1.0"] ; CUSTOM: stripped down gdata client

                     ;; web client
                     [clj-http "0.2.5"]

                     ;; configuration
                     [fidjet "0.0.1"]
                     [clj-yaml "0.3.1"]

                     ;; database
                     [postgresql/postgresql "8.4-702.jdbc4"]
                     [org.clojure/java.jdbc "0.1.1"]

                     ;; swank
                     [swank-clojure "1.4.0"]]

      :dev-dependencies [[ring-mock "0.1.1" :exclusions [org.clojure/clojure]]
                         [lein-marginalia "0.6.1"]]))
