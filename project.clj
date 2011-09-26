;; Dependency versions last checked on 7/27/11.
(defproject sistemi "0.1"
  :description "Sistemi Moderni Website"
  :dependencies [
                 ;; clojure
                 [org.clojure/clojure "1.3.0"]
;;                 [org.clojure.contrib/condition "1.3.0-SNAPSHOT"] ; TODO: use production version when ready

                 ;; logging
                 [org.clojure/tools.logging "0.1.2"]
                 ;; [clj-logging-config "1.7.0"] ; put back once it works w/ clojure 1.3
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

                 ;; web client
;;                 [clj-http "0.1.3"]

                 ;; configuration
                 [clj-yaml "0.3.0-SNAPSHOT"]

                 ;; database
                 [postgresql/postgresql "8.4-702.jdbc4"]
                 [org.clojure/java.jdbc "0.0.5"]
                 ]

  :dev-dependencies [[ring-mock "0.1.1" :exclusions [org.clojure/clojure]]
                     ;;[org.clojars.weavejester/autodoc "0.9.0"]  ;; BREAKS ENLIVE
                     ;; AUTODOC DEPS; TODO: Remove when autodoc works with clojure 1.3.
                     [org.clojure/data.json "0.1.1" :exclusions [org.clojure/clojure]]
                     [org.clojure/tools.namespace "0.1.0" :exclusions [org.clojure/clojure]]
                     ]

  :extra-classpath-dirs [;; Jars not available in maven.
                         "opt/gdata/gdata-core-1.0-minimal.jar"
                         ;; Custom builds for clojure 1.3 compatability.
                         "opt/clj-logging-config/clj-logging-config-1.7.0.jar"
                         "opt/contrib"
                         "opt/autodoc/autodoc-0.9.0.jar" ; weavjester; note, copy to lib/dev for lein autodoc to work
                         ]

  :autodoc { :name "Sistemi Moderni", :page-title "Sistemi Moderni Documentation"}
  :aot [clojure.mono-contrib.condition.Condition] ;; TODO: Remove this?
  )
