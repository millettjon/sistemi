;; Dependency versions last checked on 7/27/11.
(defproject sistemi "0.1"
  :description "Sistemi Moderni website"
  :dependencies[
                ;; clojure
                [org.clojure/clojure "1.2.1"]
                [org.clojure/clojure-contrib "1.2.0"]

                ;; logging
                [org.clojure/tools.logging "0.1.2"]
                [clj-logging-config "1.5"]
                [org.slf4j/slf4j-api "1.6.1"]
                [org.slf4j/slf4j-log4j12 "1.6.1"]
                [log4j "1.2.16"]
                
                ;; web
                [ring/ring-core "0.3.11"]
                [ring/ring-devel "0.3.11"]
                [ring/ring-jetty-adapter "0.3.11"]
                [net.cgrand/moustache "1.0.0"]
                [enlive "1.0.0"]

                ;; database
                [postgresql/postgresql "8.4-702.jdbc4"]
                [org.clojure/java.jdbc "0.0.5"]])
