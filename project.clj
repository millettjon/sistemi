(let
    ;; Hack to support a local file based maven repository on heroku.
    [local-repo (str "file://" (System/getProperty "user.dir") "/repo")]

    (defproject sistemi "0.1"
      :description "Sistemi Moderni Website"
      :repositories {"local" {:url ~local-repo
                              :snapshots false
                              :releases {:checksum :ignore :update :always}}}

      :min-lein-version "2.0.0" ; needed for heroku

      :target-path "var/target"
      :compile-path "var/target/classes" 

      :dependencies [
                     ;; clojure
                     [org.clojure/clojure "1.4.0"]
                     [slingshot "0.10.2"]

                     ;; logging
                     [clj-logging-config "1.9.6"]
                     [org.slf4j/slf4j-api "1.6.4"]
                     [org.slf4j/slf4j-log4j12 "1.6.4"]
                     [log4j "1.2.16"]

                     ;; web server
                     [ring/ring-core "1.1.0"]
                     [ring/ring-devel "1.1.0"]
                     [ring/ring-jetty-adapter "1.1.0"]
                     [ring-persistent-cookies "0.1.0"]
                     [net.cgrand/moustache "1.1.0"]

                     ;; html
                     [hiccup "1.0.0"]
                     [sistemi/gdata-core-minimal "1.0"] ; CUSTOM: stripped down gdata client

                     ;; web client
                     [clj-http "0.4.0"]

                     ;; email
                     [com.draines/postal "1.8.0"]

                     ;; configuration
                     [fidjet "0.0.1"]

                     ;; swank
                     [swank-clojure "1.4.2"]

                     ;; misc
                     [org.clojure/core.cache "0.6.2"]
                     [ordered "1.2.0"]
                     [frinj "0.1.4"]
                     [com.google.guava/guava "13.0.1"]
                     ]

      :plugins [[codox "0.6.1"]
                ;; [lein-outdated "1.0.0"] ; tried this but it hangs
                [lein-cloverage "1.0.2"]]

      :profiles {:dev {:dependencies [[org.clojure/tools.trace "0.7.3"]
                         [ring-mock "0.1.2" :exclusions [org.clojure/clojure]]]}}
      ))
