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

  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :offline (complement :online)
                   :all (fn [_] true)}

  :clean-targets [:target-path :compile-path "var/doc" "var/log" "lib"]

  :dependencies [
                 ;; sub dependencies to explicitly specify to avoid version overrides
                 ;; note - these are not top level dependencies
                 ;; note - re check these with lein deps :tree when upgrading top level dependencies
                 [xml-apis "1.4.01"]
                 ;[org.eclipse.jetty/jetty-server "8.1.7.v20120910"]
                 [cheshire "5.2.0"]
                 [org.clojure/data.json "0.2.3"]
                 ;[org.mortbay.jetty/jetty "6.1.26"]
                 ;[org.mortbay.jetty/servlet-api-2.5 "6.1.14"]

                 ;; clojure
                 [org.clojure/clojure "1.5.1"]
                 [slingshot "0.10.3"]

                 ;; logging
                 [org.clojure/tools.logging "0.2.6"]
                 [clj-logging-config "1.9.10"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [org.slf4j/slf4j-log4j12 "1.7.5"]
                 [log4j "1.2.17"]
                 [com.taoensso/timbre "3.0.0-RC4"]

                 ;; ring core
                 [ring/ring-core "1.2.1" :exclusions [org.clojure/tools.reader]]
                 [ring/ring-devel "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.1"]

                 ;; ring handlers and middleware
                 [net.cgrand/moustache "1.1.0"]
                 [ring-persistent-cookies "0.1.0"]
                 [ring/ring-json "0.2.0"]

                 ;; templating
                 [hiccup "1.0.4"]
                 [enlive "1.1.1"]
                 [selmer "0.5.9"]

                 ;; web client
                 [clj-http "0.7.7" :exclusions [org.clojure/tools.reader #_ commons-logging]]

                 ;; email
                 [com.draines/postal "1.11.1"]

                 ;; user agent detection
                 [bitwalker/UserAgentUtils "1.11"]

                 ;; calculation
                 [net.cgrand/spreadmap "0.1.4"]
                 [frinj "0.2.5"]

                 ;; payment
                 [clojurewerkz/money "1.4.0"]
                 [abengoa/clj-stripe "1.0.3"]

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

                 ;; event driven
                 [http-kit "2.1.16"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]

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
            [lein-cloverage "1.0.2"]]

  :aliases {"start" ["trampoline" "run" "-m" "sistemi.core"]
            "init-db" ^{:doc "Initialize the datomic schema."} ["run" "-m" "sistemi.cli/init-db"]
            "marg" ["marg" "--dir" "var/doc"]
            "clov" ["cloverage" "-o" "var/cloverage"]}

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
                                  [ring-mock "0.1.5" :exclusions [org.clojure/clojure]]

                                  ;; browser automated testing
                                  [clj-webdriver/clj-webdriver "0.6.0"]
                                  [com.github.detro.ghostdriver/phantomjsdriver "1.0.3"]
                                  ]

                   :jvm-opts ["-Dphantomjs.binary.path=./opt/phantomjs/current/bin/phantomjs"]} })
