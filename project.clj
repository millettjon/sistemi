;; ______________________________________________________________________
;;  ____  _     _                 _ __  __           _                 _ 
;; / ___|(_)___| |_ ___ _ __ ___ (_)  \/  | ___   __| | ___ _ __ _ __ (_)
;; \___ \| / __| __/ _ \ '_ ` _ \| | |\/| |/ _ \ / _` |/ _ \ '__| '_ \| |
;;  ___) | \__ \ ||  __/ | | | | | | |  | | (_) | (_| |  __/ |  | | | | |
;; |____/|_|___/\__\___|_| |_| |_|_|_|  |_|\___/ \__,_|\___|_|  |_| |_|_|
;; ______________________________________________________________________

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
                 [org.eclipse.jetty/jetty-server "8.1.12.v20130726"]
                 [org.clojure/data.json "0.2.5"]

                 ;; clojure
                 [org.clojure/clojure "1.6.0"]
                 [slingshot "0.10.3"]

                 ;; logging
                 [org.clojure/tools.logging "0.3.0"]
                 [clj-logging-config "1.9.10"]
                 [org.slf4j/slf4j-api "1.7.7"]
                 [org.slf4j/slf4j-log4j12 "1.7.7"]
                 [log4j "1.2.17"]
                 [com.taoensso/timbre "3.2.1" :exclusions [org.clojure/tools.reader]]

                 ;; ring core
                 [ring/ring-core "1.3.0" :exclusions [org.clojure/tools.reader]]
                 [ring/ring-devel "1.3.0" :exclusions [org.clojure/java.classpath]]
                 [ring/ring-jetty-adapter "1.3.0"]

                 ;; ring handlers and middleware
                 [net.cgrand/moustache "1.1.0" :exclusions [org.clojure/clojure ring/ring-core]]
                 [ring-persistent-cookies "0.1.0"]
                 [ring/ring-json "0.3.1"]

                 ;; templating
                 [hiccup "1.0.5"]
                 [enlive "1.1.5"]
                 [selmer "0.6.7"]

                 ;; web client
                 [clj-http "0.9.2" :exclusions [org.clojure/tools.reader]]

                 ;; email
                 [com.draines/postal "1.11.1"]
                 [clj-mandrill "0.1.0"]
                 [net.sf.cssbox/cssbox "4.5"]

                 ;; user agent detection
                 [bitwalker/UserAgentUtils "1.13"]

                 ;; calculation
                 [net.cgrand/spreadmap "0.1.4"]
                 [frinj "0.2.5"]

                 ;; payment
                 [clojurewerkz/money "1.6.0"]
                 [abengoa/clj-stripe "1.0.4"]

                 ;; validation
                 [bouncer "0.3.0"]

                 ;; xml for UPS
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/data.zip "0.1.1"]

                 ;; Date related.
                 [net.objectlab.kit/datecalc-common "1.3.0"]
                 [net.objectlab.kit/datecalc-joda "1.3.0" :exclusions [joda-time]]

                 ;; Datomic/Data
                 [com.datomic/datomic-pro "0.9.4384"
                 :exclusions [
                              ;; Exclude these since using log4j for logging.
                              ;; See: http://docs.datomic.com/configuring-logging.html
                              org.slf4j/slf4j-nop
                              org.slf4j/log4j-over-slf4j ; note: causes an IncompatibleClassChangeError if included
                              ]]
                 [prismatic/schema "0.2.3"]

                 ;; misc
                 [org.clojure/core.cache "0.6.3"]
                 [org.clojure/core.memoize "0.5.6"]
                 ;; [flatland/ordered "1.5.2"] ; TODO use flatland/ordered as it is newer.
                 [ordered "1.3.2"]
                 [com.google.guava/guava "17.0"]
                 [org.clojure/tools.namespace "0.2.4"]

                 ;; event driven
                 [http-kit "2.1.18"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]

                 ;; Clojurescript
                 [org.clojure/clojurescript "0.0-2234"]
                 [jayq "2.5.1"]            ;; jquery wrapper
                 [rm-hull/monet "0.1.12" :exclusions [org.clojure/tools.reader]] ;; html5 canvas
                 [prismatic/dommy "0.1.2"] ;; jquery replacement using clojurescript idioms
                 [com.cemerick/url "0.1.1"]
                 [pathetic "0.5.1"]

                 ;; For proper handling of internationalized chars in url path segments.
                 [com.google.gdata/core "1.47.1" :exclusions [com.google.code.findbugs/jsr305]]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-marginalia "0.7.1"]
            [lein-ancient "0.5.5"]
            [lein-cloverage "1.0.2"]]

  :aliases {"start" ["trampoline" "run" "-m" "sistemi.core"]
            "init-db" ^{:doc "Initialize the datomic schema."} ["run" "-m" "sistemi.cli/init-db"]
            "marg" ["marg" "--dir" "var/doc"]
            "clov" ["cloverage" "-o" "var/cloverage"]}

  :cljsbuild {:builds
              {:dev {:source-paths ["src/cljs"]
                     :compiler
                     {:output-to "www/raw/js/main.js"
                      :output-dir "www/raw/js/dev"
                      :optimizations :whitespace
                      :source-map "www/raw/js/main.js.map"}}

               :prod {:source-paths ["src/cljs"]
                      :compiler
                      {:optimizations :advanced
                       :externs ["src/cljs-externs/jquery-1.9.js"]
                       :pretty-print false
                       :output-to "www/raw/js/main.min.js"
                       :output-dir "var/target/cljsbuild/prod"}}}}

  :profiles {:dev {:dependencies [[org.clojure/tools.trace "0.7.8"]
                                  [org.clojure/tools.nrepl "0.2.3"]
                                  [weavejester/cider-nrepl "0.7.0-SNAPSHOT"]
                                  [ring-mock "0.1.5" :exclusions [org.clojure/clojure]]

                                  ;; browser automated testing
                                  [clj-webdriver/clj-webdriver "0.6.1" :exclusions [org.mortbay.jetty/jetty]]
                                  [com.github.detro.ghostdriver/phantomjsdriver "1.1.0"]]

                   :jvm-opts ["-Dphantomjs.binary.path=./opt/phantomjs/current/bin/phantomjs"]} })
