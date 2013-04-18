(let
    ;; Hack to support a local file based maven repository on heroku.
    [local-repo (str "file://" (System/getProperty "user.dir") "/repo")]


    (defproject sistemi "0.1"
      :description "Sistemi Moderni Website"
      :repositories {"local" {:url ~local-repo
                              :snapshots false
                              :releases {:checksum :ignore :update :always}}
                     "bitwalker.user-agent-utils.mvn.repo" {:url "https://raw.github.com/HaraldWalker/user-agent-utils/mvn-repo/"
                                                            :snapshots false
                                                            :releases {:checksum :ignore :update :always}}}

      :min-lein-version "2.0.0" ; needed for heroku

      ;; Use var/ for generated files.
      :target-path "var/target"
      :compile-path "var/target/classes" 
      :resource-paths ["etc/resources"]

      ;:jvm-opts ["-Dlog4j.debug=true"]

      :dependencies [
                     ;; clojure
                     [org.clojure/clojure "1.4.0"]
                     [slingshot "0.10.3"]

                     ;; logging
                     [clj-logging-config "1.9.10"]
                     [org.slf4j/slf4j-api "1.7.2"]
                     [org.slf4j/slf4j-log4j12 "1.7.2"]
                     [log4j "1.2.17"]

                     ;; ring core
                     [ring/ring-core "1.1.8"]
                     [ring/ring-devel "1.1.8"]
                     [ring/ring-jetty-adapter "1.1.8"]

                     ;; ring handlers and middleware
                     [net.cgrand/moustache "1.1.0"]
                     [ring-persistent-cookies "0.1.0"]

                     ;; html
                     [hiccup "1.0.2"]
                     [sistemi/gdata-core-minimal "1.0"] ; CUSTOM: stripped down gdata client

                     ;; web client
                     [clj-http "0.6.4"]

                     ;; email
                     [com.draines/postal "1.9.2"]

                     ;; configuration
                     [fidjet "0.0.1"]

                     ;; user agent detection
                     [bitwalker/UserAgentUtils "1.8"]
                     
                     ;; calculation
                     [dgraph "1.2.2"]
                     [frinj "0.1.4"]

                     ;; misc
                     [org.clojure/core.memoize "0.5.3"]
                     ;;[org.clojure/core.cache "0.6.3"]
                     [ordered "1.3.2"]
                     [com.google.guava/guava "13.0.1"]
                     [org.clojure/tools.namespace "0.2.2"]]

      :plugins [[codox "0.6.1"]
                ;; [lein-outdated "1.0.0"]  ; hangs
                ;; [lein-cloverage "1.0.2"] ; runs but no output is produced
                ]

      :profiles {:dev {:dependencies [[org.clojure/tools.trace "0.7.3"]
                                      [org.clojure/tools.nrepl "0.2.1"]
                                      [ring-mock "0.1.2" :exclusions [org.clojure/clojure]]]}}))
