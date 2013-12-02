(ns sistemi.sheet
  "Cached access to spreadsheets via spreadmap."
  (:require [clojure.core.cache :as cache]
            [clojure.tools.logging :as log])
  (:use [net.cgrand.spreadmap :only [spreadmap]])
  (:refer-clojure :exclude [get]))

(def ^:private cache
  "Cache spreadsheets for 10 minutes."
  (atom (cache/ttl-cache-factory {} :ttl (* 10               ; min
                                            60               ; sec/min
                                            1000             ; ms/sec
                                            ))))

(defn- load-sheet
  "Loads a spreadsheet using spreadmap."
  [path]
  (log/info "Loaded spreadsheet" path)
  (spreadmap path))

(defn get
  "Returns a cached spreadsheet object by path creating a new one if necessary."
  [path]
  (let [C @cache
        C (if (cache/has? C path)
            (cache/hit C path)
            (swap! cache #(cache/miss % path (load-sheet path))))]
    (clojure.core/get C path)))
