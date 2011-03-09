(defproject sistemi "1.0.0-SNAPSHOT"
  :description "Sistemi Moderni Website"
  :namespaces [sistemi.core]
  :dependencies [[compojure "0.6.0"]
		 [ring/ring-servlet "0.3.6"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [lein-ring "0.3.2"]]
  :ring {:handler sistemi.core/main-routes}

  :compile-path "war/WEB-INF/classes"
  :library-path "war/WEB-INF/lib")
