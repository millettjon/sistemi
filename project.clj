(defproject sistemi "1.0.0-SNAPSHOT"
  :description "Sistemi Moderni Website"
  :dependencies [[org.clojure/clojure "1.2.0"]
		 [org.clojure/clojure-contrib "1.2.0"]
		 [ring/ring-jetty-adapter "0.3.6"]
		 [compojure "0.6.0"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [lein-ring "0.3.2"]]
  :ring {:handler sistemi.core/main-routes})

;; ? does "lein ring server" start a swank server as well?
