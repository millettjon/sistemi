(defproject sistemi "0.1"
  :description "Sistemi Moderni Website"
  :repositories {"project" "file:opt/m2"
                 "bitwalker.user-agent-utils.mvn.repo" {:url "https://raw.github.com/HaraldWalker/user-agent-utils/mvn-repo/"
                                                        :snapshots false
                                                        :releases {:checksum :ignore :update :always}}}

    ;; Use var/ for generated files.
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :target-path "var/target"
  :compile-path "var/target/classes" 
  :resource-paths ["etc/resources"]

  :clean-targets [:target-path :compile-path "var/doc" "var/log" "lib"]

  ;;:jvm-opts ["-Dlog4j.debug=true"]

  :dependencies [
                 ;; clojure
                 [org.clojure/clojure "1.5.1"]
                 [slingshot "0.10.3"]

                 ;; logging
                 [clj-logging-config "1.9.10"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [org.slf4j/slf4j-log4j12 "1.7.5"]
                 [log4j "1.2.17"]

                 ;; ring core
                 [ring/ring-core "1.2.1" :exclusions [org.clojure/tools.reader]]
                 [ring/ring-devel "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.1"]

                 ;; ring handlers and middleware
                 [net.cgrand/moustache "1.1.0"]
                 [ring-persistent-cookies "0.1.0"]

                 ;; html
                 [hiccup "1.0.4"]
                 [enlive "1.1.1"]

                 ;; web client
                 [clj-http "0.7.7" :exclusions [org.clojure/tools.reader #_ commons-logging]]

                 ;; email
                 [com.draines/postal "1.11.1"]

                 ;; configuration
                 [fidjet "0.0.2"]

                 ;; user agent detection
                 [bitwalker/UserAgentUtils "1.11"]

                 ;; calculation
                 [net.cgrand/spreadmap "0.1.4"]
                 [frinj "0.2.5"]

                 ;; payment
                 [clojurewerkz/money "1.4.0"]

                 ;; validation
                 [bouncer "0.3.0-alpha1"]

                 ;; xml for UPS
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/data.zip "0.1.1"]

                 ;; Date related.
                 [net.objectlab.kit/datecalc-common "1.2.0"]
                 [net.objectlab.kit/datecalc-joda "1.2.0" :exclusions [joda-time]]

                 ;; Datomic
                 [com.datomic/datomic-pro "0.9.4384"
                  :exclusions [
                               ;; Exclude these since using log4j for logging.
                               ;; See: http://docs.datomic.com/configuring-logging.html
                               org.slf4j/slf4j-nop
                               org.slf4j/log4j-over-slf4j ; note: causes an IncompatibleClassChangeError if included
                               ]]

                 ;; misc
                 [org.clojure/core.cache "0.6.3"]
                 [org.clojure/core.memoize "0.5.6"]
                 [ordered "1.3.2"]
                 [com.google.guava/guava "15.0"]
                 [org.clojure/tools.namespace "0.2.4"]

                 ;; Clojurescript
                 ;; Note: Dommy 0.1.2 doesn't work with 0.0-2030.
                 [org.clojure/clojurescript "0.0-1859" #_ "0.0-2030"]
                 [jayq "2.5.0"]            ;; jquery wrapper
                 [rm-hull/monet "0.1.9" :exclusions [org.clojure/tools.reader]] ;; html5 canvas
                 [prismatic/dommy "0.1.2"] ;; jquery replacement using clojurescript idioms

                 ;; For proper handling of internationalized chars in url path segments.
                 [com.google.gdata/core "1.47.1" :exclusions [com.google.code.findbugs/jsr305]]
                 ]

  :plugins [[lein-cljsbuild "0.3.4"]
            [lein-marginalia "0.7.1"]
            [lein-ancient "0.5.3"]
            [lein-cloverage "1.0.2"]
            [slamhound "RELEASE"]]

  :aliases { "init-db" ^{:doc "Initialize the datomic schema."} ["run" "-m" "sistemi.cli/init-db"]
             "marg" ["marg" "--dir" "var/doc"]}

  :cljsbuild {:crossovers []
              :crossover-path "var/target/crossovers"
              :builds
              {:dev {:source-paths ["src/cljs"]
                     :compiler
                     {:output-to "www/raw/js/main.js"
                      :output-dir "var/target/cljsbuild/dev"}}

               :prod {:source-paths ["src/cljs"]
                      :compiler
                      {:optimizations :advanced
                       :externs ["src/cljs-externs/jquery-1.9.js"]
                       :pretty-print false
                       :output-to "www/raw/js/main.min.js"
                       :output-dir "var/target/cljsbuild/prod"}}}}

  :profiles {:dev {:dependencies [[org.clojure/tools.trace "0.7.6"]
                                  [org.clojure/tools.nrepl "0.2.3"]
                                  [ring-mock "0.1.5" :exclusions [org.clojure/clojure]]]}})
