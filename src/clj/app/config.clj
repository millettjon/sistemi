(ns app.config
  "Global configuration map and related helper functions."
  (:require [clojure.edn :as edn]
            [util.path :as path]
            [util.string :as str]
            [harpocrates.core :as gpg])
  (:use [clojure.contrib.map-utils :only (deep-merge-with)]))

(defn- read-file
  "Reads a single edn file decrypting it if necessary."
  [{gpg-opts :gpg} file]
  (let [name (path/name file)]
    (cond
     (str/ends-with? name ".edn")     (-> file slurp edn/read-string)
     (str/ends-with? name ".clj")     (-> file str load-file)
     (str/ends-with? name ".edn.gpg") (-> file (gpg/decrypt gpg-opts)))))

#_ (read-file {} "etc/profiles/default/default.edn")
#_ (read-file {} "etc/profiles/development/email.clj")

(defn- handle-file
  "Handles processing of one file. Removes the file from the
configuration map, reads the file's configuration, and calls f with
the current :conf and the file's conf."
  [{:keys [files conf] :as m} file f]
  (let [name (path/name file)
        matches? #(= name (path/name %))]
    (if-let [target (some #(if (matches? %) %) files)]
      (do
        (remove matches? files)
        (let [m (assoc m :files (remove matches? files))]
          (if-let [data (read-file m target)]
            (f m data)
            m)))
      m)))

#_ (let [files (path/files "etc/profiles/default")]
     (handle-file {:files files :conf {}}
                  "default.edn"
                  (fn [conf sub-conf] (merge conf sub-conf))
                  ))

#_ (let [files (path/files "etc/profiles/default")]
     (map path/name files))

(defn- handle-gpg-conf
  "Loads the gpg configuration if present."
  [m dir]
  (handle-file m "gpg.edn"
               (fn [m data] (assoc m :gpg (merge {:home (str dir "/.gnupg")} data)))))

(defn- handle-default-conf
  "Loads the default configuration if present."
  [m]
  (handle-file m "default.edn" (fn [{:keys [conf] :as m} data]
                                 (assoc m :conf (merge conf data)))))

(defn- handle-conf
  "Loads a configuration."
  [m file]
  (let [key (-> file path/shortname keyword)]
    (handle-file m file
                 (fn [{{old-data key} :conf :as m} data]
                   (assoc-in m [:conf key]
                             (deep-merge-with (fn [& l] (last l)) old-data data))))))

(defn- handle-files
  "Loads all remaining configurations."
  [{:keys [files] :as m}]
  (reduce handle-conf m files))

(defn dir-map
  "Reads and deeply merges all configuration files in a
   directory. Configuration files are expected to contain a single
   clojure map. If a file default.edn exists, it is read and merged
   directly into the result map.  Files ending in .edn (e.g., foo.edn)
   are read and merged into the result map under the key of their
   basename.  Files ending in .edn.gpg are first decrypted and then
   read. Gpg options can configured by creating a file called
   gpg.conf."
  [dir]
  (-> {:files (path/files dir) :conf {}}
      (handle-gpg-conf dir)
      handle-default-conf
      handle-files
      :conf
      ))

#_ (path/files "etc/profiles/default")
#_ (dir-map "etc/profiles/default")

(defn- merge-configs
  "Deeply merges the list of configuration maps. Values from right most maps take precedence."
  [& maps]
  (apply deep-merge-with (fn [& l] (last l)) maps))

(def config
  "Global configuration map."
  {})

(defn set-config!
  "Applies function merge-configs to the list of configuration maps and sets the result in config."
  [& maps]
  (alter-var-root #'config (constantly (apply merge-configs maps))))

;; TODO: get rid of this once migrated to components
(defn conf
  "Gets an entry from the global configuration using the specified keys."
  [& keys]
  (get-in config keys))
