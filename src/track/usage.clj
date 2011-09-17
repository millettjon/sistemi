(ns track.usage
  "Usage tracking"
  )

;; In memory map stored in an atom that gets updated by an agent.
;; A scheduled job to sync the atom state to the database periodically.
(def usage-map (atom {}))

(defn last-used
  "Track the time an item was last used."
  [type key]
  (let [keys [type key]
        t (System/currentTimeMillis)]
    (swap! usage-map #(if (> t (get-in %1 keys 0))
                        (assoc-in %1 [type key] t)
                        %1))))

;; What is the cost of:
;; - batching updates in one thread, then deeply merging.
;; - just doing a hash put
;; - just doing a NOOP swap

;; Performance improvements:
;; - use the time the request came in
;; - use the nearest second
;;   - store the time value in a global and re-get every second...
;; - use System.nanoTime
;; - use a resolution of 1 second in time passed to last-used?
;; - use 1 map per type to limit STM retries?
;; - batch updates? update stats once per thread?
;;   - e.g., update counters in a thread local then commit at end of request

;; performance is 4.5 sec/million on my netbook
;;                5.5 sec/million w/ unconditional set
;; (time (doseq [x (range 1000000)]
;;         (last-used :string :title)))

;; 50% of time is in getting the time.
;; possibly just use the time the request came in (request :time)
;; (time (doseq [x (range 1000000)]
;;        (System/currentTimeMillis)))



;; Not worth using an agent
;; This should be a near instantaneous operation.
;; What are the costs of using an agent?
;; - STM to send and receive messages
;; - Additional thread context switching

;; How does it get updated in the agent.

;; ? how does it get dumped to the database?
;; http://groups.google.com/group/clojure/browse_thread/thread/6e8a8dd14f184969

;; Time Event Last Happened
;; Last Occurrence
;; Last Usage
;; :type :translation-key (:page-render, :snippet-render)
;; :event :used
;; :time xyz

;; - List which translations are never used ().
;;   - stats agent?
;;     - dump usage message to an event queue
;;       - operations are idempotent and order independent
;;       - udpate last event time (:type string :key [/en/profile/select-language.html :foo :bar] :time time)
;;       - each event should have a unique key
;;       - periodically flush to db (set if newer ...)
;;       - might need to store created-on date to not garbage collect new entries... (or just a manual touch)
;;    (last-used type key time)
;;       {:type string {key time}}
;;       db: id type key created last_used (pk is (type,key))
;;       uses: handler, page, snippet, translation

;; counter?
;;   - does a counter reset over some time window?
;;   - e.g., total for: day month year lifetime
;;   - rolling counter?
;;   - not as useful for querying as just storing all data

;; simple value
;; last used at
;; counter
;; moving average: http://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average
;; exponential smoothing: http://en.wikipedia.org/wiki/Exponential_smoothing

;; last usage - garbage collection
;; counter - ...
;;   - or other value e.g., mean time between usage etc.
;; all usage - frequency, distribution
;; activity - user tracking
;; - on site
;; - external
;; - inbound
;; - outbound
;; A/B testing
