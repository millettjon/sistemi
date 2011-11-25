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
                     [slingshot "0.9.0"]

                     ;; logging
                     [org.clojure/tools.logging "0.1.2" #_"0.2.3"]
                     ;; [clj-logging-config "1.7.0"] ; put back once it works w/ clojure 1.3
                     [sistemi/clj-logging-config "1.7.0"] ; CUSTOM: fixed to work w/ clojure 1.3
                     [org.slf4j/slf4j-api "1.6.1"]
                     [org.slf4j/slf4j-log4j12 "1.6.1"]
                     [log4j "1.2.16"]

                     ;; web server
                     [ring/ring-core "0.3.11"]
                     [ring/ring-devel "0.3.11"]
                     [ring/ring-jetty-adapter "0.3.11"]
                     [net.cgrand/moustache "1.0.0"]
                     [enlive "1.0.0"]
                     [ring-persistent-cookies "0.1.0"]
                     [sistemi/gdata-core-minimal "1.0"] ; CUSTOM: stripped down gdata client

                     ;; web client
                     [clj-http "0.2.5"]

                     ;; configuration
                     [clj-yaml "0.3.0-SNAPSHOT"]

                     ;; database
                     [postgresql/postgresql "8.4-702.jdbc4"]
                     [org.clojure/java.jdbc "0.0.5"]
                     ]

      :dev-dependencies [[ring-mock "0.1.1" :exclusions [org.clojure/clojure]]
                         [lein-marginalia "0.6.1"]]
      ))
