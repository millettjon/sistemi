(ns sistemi.logging
  (:require [taoensso.timbre :as t]
            [taoensso.timbre.appenders.rolling :as rolling]
            [taoensso.timbre.tools.logging :as tl]
            [clj-logging-config.log4j :as log4j]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [util.string :as stru]))

;; For file, log as pure edn.
;; For console, log w/ some formatting and colorization.
;; TODO: Make command line utility to colorize and format logfile output (for tail/grep/less etc).

;; How to get 256 colors in terminal:
;; http://bitmote.com/index.php?post/2012/11/19/Using-ANSI-Color-Codes-to-Colorize-Your-Bash-Prompt-on-Linux#256%20%288-bit%29%20Colors

(defn fg
  [text color]
  (str \u001b "[38;5;" color "m" text \u001b "[0m"))

(defn- colorize-level
  "Colors to use for log levels."
  [level]
  (if-let [color (case level
                   "TRACE" 39
                   "DEBUG" 39
                   "INFO"  34
                   "WARN"  11
                   "ERROR" 196
                   "FATAL" 196
                   "REPORT" 196)]
    (fg level color)
    level))

(defn- unwrap
  "Unwraps single element collections."
  [args]
  (if (= 1 (count args)) (first args) args))

(defmulti format-event
  (fn [event & [opts]] (:format opts)))

(defn- base-event
  "Builds base event map."
  [event meta ns]
  (-> event
      (select-keys [:level :instant :hostname :log-lib :event-meta])
      (merge meta)
      (assoc :ns (str ns)) ; stringify ns for events from clojure.tools.logging  
      ))

(defmethod format-event :default
  [{:keys [args throwable ns] {:keys [meta]} :ap-config :as event} & _]
  (-> event
      (base-event meta ns)
      (assoc :data (unwrap args))
      (merge (if throwable {:throwable (t/stacktrace throwable "\n" {})} {}))))

(defmethod format-event :console
  [{:keys [level throwable args timestamp ns] {:keys [meta]} :ap-config :as event} & _]
  (let [args (unwrap args)
        event? (and (map? args) (contains? args :event))]
    (format "%s %s %s %s %s%s"
            (fg timestamp 39)
            (-> level name str/upper-case colorize-level)
            (fg (if event? (:event args) ns) 39)            ; if there is an event name, use that in place of ns
            (pr-str (if event? (dissoc args :event) args))  ; and remove it from the displayed event data
            (fg (pr-str (base-event event meta ns)) 39)
            (or (t/stacktrace throwable "\n") ""))))

(defn- log4j-adapter
  "Adapter to send log4j events to timbre."
  [{:keys [message level loggerName throwableInformation] :as ev}]
  ;; (prn "ADAPTER" ev)
  (t/send-to-appenders!
   (-> level str str/lower-case keyword) ; level
   {:log-lib :log4j} ; base event parameters
   [message]         ; arguments to log fn
   loggerName        ; ns
   (if throwableInformation (.getThrowable throwableInformation) nil)
   message))

(defn- datomic-middleware
  "Converts datomic log messages from strings to maps for logging as edn."
  [{:keys [ns message args] :as ev}]
  #_ (prn "DATOMIC" ev)
  (if (and (stru/starts-with? (str ns) "datomic."))
    (let [[message x] (try [(edn/read-string message) nil]
                          (catch Exception x [message x]))]
      (if x (assoc-in ev [:event-meta :edn-exception] x)
          (-> ev
              (assoc :message message)
              (assoc-in [:args 0] message))))
    ev))

;; ===== LOGGING =====
;; See: https://github.com/malcolmsparks/clj-logging-config
;; %d   date
;; %p   priority
;; %X   MDC
;; %c   logger name (class)
;; %m   message
;; %t   thread
(defn init-slf4j!
  []
  (log4j/set-loggers!
   ;;:root     {:level :info :pattern "%d{HH:mm:ss.SSS} %p %c %m%n"}
   :root {:out log4j-adapter}
   ))

(defn init!
  "Initializes the logging configuration."
  [meta]
  (swap! t/config (constantly t/example-config))
  (t/merge-config!
   {:timestamp-pattern "HH:mm:ss.SSS"
    :shared-appender-config {:meta meta}
    :fmt-output-fn format-event
    :middleware [datomic-middleware]
    })
  (t/set-config! [:appenders :standard-out :fmt-output-opts] {:format :console})
  (t/set-config! [:appenders :rolling] (rolling/make-rolling-appender))
  (t/set-config! [:shared-appender-config :rolling :path] "var/log/sistemi.edn.log")

  ;; Capture events sent to clojure.tools.logging.
  (tl/use-timbre)

  ;; Capture events sent via slf4j (e.g., jetty and datomic).
  (init-slf4j!)

  ;; Capture System.out and System.err.
  (clojure.tools.logging/log-capture! "stdout"))

#_ (t/debug "test debug")
#_ (t/info "test info")
#_ (t/warn "test warn")
#_  (t/error "test error")
#_ (t/info (Exception. "fucked") "fubar")

#_ (init! {})
#_ (t/info nil)
#_ (t/info "foo")
#_ (t/info {:event :boot-id/create :boot-id "zAEx" })
#_ (t/info {:foo "FOO"} "BAR")
#_ (t/info (Exception. "fucked") "fubar")
#_ (t/debug "debug foo")
#_ (t/warn "warning foo")
#_ (t/error "error foo")
#_ (t/fatal "fatal foo")

#_ (do (import 'org.slf4j.Logger)
       (import 'org.slf4j.LoggerFactory)
       (let [logger (. LoggerFactory getLogger "foo")]
         (.info logger "foo")
         #_ (.error logger "fubar" (Exception. "FUBAR"))
         #_ (.info logger "fubar" (Exception. "FUBAR"))
         ))
