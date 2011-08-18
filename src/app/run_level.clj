(ns app.run-level
  (:require [clojure.tools.logging :as log])
  (:use [app.config :only (*config*)]
        [util.except :only (affirm)]
        [clojure.contrib.def :only (defvar)]))

(defn- get-run-level
  "Looks up the application run level from the environment variable RUN_LEVEL. If not found, the
   level :development is returned."
  []
  (let [level (keyword (:run-level *config* :development))]
    (affirm (level #{:development :staging :production}) "Unknown run level '~{level}'.")))

(defvar *run-level* (get-run-level)
  "Runtime level of the process.")

(defn development?
  "Returns true if the run level is development."
  []
  (= :development *run-level*))

(defn staging?
  "Returns true if the run level is staging."
  []
  (= :staging *run-level*))

(defn production?
  "Returns true if the run level is production."
  []
  (= :production *run-level*))
