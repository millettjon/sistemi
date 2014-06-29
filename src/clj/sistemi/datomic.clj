(ns sistemi.datomic
  (:require [app.config :as cf]
            [sistemi.config :as scf]
            [datomic.api :as d]
            [util.id :as id]
            [util.string :as s]
            [util.edn :as edn]
            [util.except :as ex]
            [clojure.walk :as w]
            [taoensso.timbre :as log])
  (:refer-clojure :exclude [partition]))

(def partition
  :db.part/user)

(def schema-attributes
  [{:db/id #db/id[:db.part/db]
    :db/ident :db/pack
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}])

(def schema
  [; ========== TRACKING ==========
   ;; browser
   {:db/id #db/id[:db.part/db]
    :db/ident :browser/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/value
    :db/index true
    :db/doc "A browser's unique random id for event tracking."
    :db.install/_attribute :db.part/db}

   ;; ========== PRICE ==========
   ;; price - sub-total
   {:db/id #db/id[:db.part/db]
    :db/ident :price/sub-total
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/pack :edn
    :db.install/_attribute :db.part/db}

   ;; price - tax
   {:db/id #db/id[:db.part/db]
    :db/ident :price/tax
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/pack :edn
    :db.install/_attribute :db.part/db}

   ;; price - total
   {:db/id #db/id[:db.part/db]
    :db/ident :price/total
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/pack :edn
    :db.install/_attribute :db.part/db}

   ;; ========== CONTACT ==========
   ;; contact - name
   {:db/id #db/id[:db.part/db]
    :db/ident :contact/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; contact - email
   {:db/id #db/id[:db.part/db]
    :db/ident :contact/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; contact - phone
   {:db/id #db/id[:db.part/db]
    :db/ident :contact/phone
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; ========== PAYMENT ==========
   {:db/id #db/id[:db.part/db]
    :db/ident :payment/transaction
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/pack :edn
    :db.install/_attribute :db.part/db}

   ;; ========== ADDRESS ==========
   {:db/id #db/id[:db.part/db]
    :db/ident :address/contact
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :address/address1
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :address/address2
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :address/city
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :address/region
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :address/code
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :address/country
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; ========== SHIPPING ==========
   {:db/id #db/id[:db.part/db]
    :db/ident :shipping/address
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :shipping/boxes
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/pack :edn
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :shipping/price
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; ========== ORDER ==========
   ;; order - id
   {:db/id #db/id[:db.part/db]
    :db/ident :order/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/value
    :db/index true
    :db.install/_attribute :db.part/db}

   ;; order - locale
   {:db/id #db/id[:db.part/db]
    :db/ident :order/locale
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - status
   ;; purchased -> building -> shipping -> delivered
   ;; ? should this be an enum?
   {:db/id #db/id[:db.part/db]
    :db/ident :order/status
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - items
   {:db/id #db/id[:db.part/db]
    :db/ident :order/items
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/pack :edn
    :db.install/_attribute :db.part/db}

   ;; order - price
   {:db/id #db/id[:db.part/db]
    :db/ident :order/price
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - taxable?
   {:db/id #db/id[:db.part/db]
    :db/ident :order/taxable?
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - purchase date
   {:db/id #db/id[:db.part/db]
    :db/ident :order/purchase-date
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - contact
   {:db/id #db/id[:db.part/db]
    :db/ident :order/contact
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - shipping
   {:db/id #db/id[:db.part/db]
    :db/ident :order/shipping
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - estimated delivery date
   {:db/id #db/id[:db.part/db]
    :db/ident :order/estimated-delivery-date
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - payment
   {:db/id #db/id[:db.part/db]
    :db/ident :order/payment
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}])

;; datomic:dev://{transactor-host}:{port}/{db-name}
;; datomic:mem://[db-name]
(defn- get-uri
  "Builds a datamic uri based on the current configuration and run level."
  []
  (let [uri (cf/conf :datomic-uri)]
    (str uri
         (if (s/ends-with? uri "/") "" "/")
         (-> (scf/active-profile) name))))

#_ (app.config/conf :datomic-uri)
#_ (get-uri)


;; From the docs: "Datomic connections do not adhere to an acquire/use/release
;; pattern.  They are thread-safe, cached, and long lived."
;;
;; Not sure how the caching works as this is 2 orders of magnitude
;; slower than just using the same connection.
;;
(defn get-conn
  "Gets a connection for the currently configured database."
  []
  (d/connect (get-uri)))

(defn attribute
  "Returns a map of attributes for an attribute."
  ([k] (attribute k (d/db (get-conn))))
  ([k db]
     (let [eid (d/q '[:find ?e
                      :in ?k $
                      :where [?e :db/ident ?k]]
                    k
                    db)]
       (if-let [e (d/entity db (ffirst eid))]
         (into {} e)))))

;; TODO: Take db as an argument.
(defn edn?
  "Returns true if the attribute is packed in edn format."
  [k]
  (-> k
      attribute
      :db/pack
      (= :edn)))
#_ (edn? :price/sub-total) ; -> true
#_ (edn? :order/status) ; -> false

;; TODO: Take db as an argument.
(defn component?
  "Returns true if the attribute is a component."
  [k]
  (-> k
      attribute
      :db/isComponent))
#_ (component? :price/tax) ; -> nil
#_ (component? :order/shipping) ; -> true


(defn ent1
  "Gets, touches, and returns the first entity from a query result consisting of entity ids."
  [eids db]
  (->> eids
       ffirst
       (d/entity db)
       d/touch))

;; TODO: make a tail recursive version using loop
;; TODO: make a version of walk that works with datomic entity maps
(defn unpack
  "Deeply unpacks a datomic entity. Returns a map with all components
coerced to maps and edn values unpacked."
  [emap]
  (reduce (fn [m [k v]]
            (let [v (if (component? k)
                      (unpack v)
                      (if (edn? k)
                        (edn/read-string v)
                        v))]
              (assoc m k v)))
   {}
   emap))

(defn- unqualify1
  "Unqualifies keywords in a map."
  [m]
  (reduce (fn [m [k v]]
            (assoc m
              (if (keyword? k)
                (-> k name keyword)
                k)
              v))
          {}
          m))
#_ (unqualify1 {:address/city "Sturgis" :address/country :US :foo "FOO"})
#_ (instance? clojure.lang.IRecord (Rec. "hi"))

;; Should it ignore records completely?
;; - yes since, if they came from datomic, they had to have already been coerced
(defn unqualify
  [m]
  (w/postwalk (fn [v]
                (cond (instance? clojure.lang.IRecord v) v
                      (map? v) (unqualify1 v)
                      :else v))
              m))
#_ (unqualify1 {:order/shipping {:shipping/address {:address/city "Sturgis" :address/country :US}}})
#_ (unqualify {:order/shipping {:shipping/address {:address/city "Sturgis" :address/country :US}}})
#_ (unqualify {:order/sub-total (frinj.ops/fj 1 :m)})

(defn lookup
  "Queries for a single entity. The result is touched, converted to a
map, unpacked, and has keyworkds unqualified. The database to use can
optionally be passed as the first element in args and will be used as
the first query argument."
  [query & args]
  (let [[db, args] (if (= datomic.db.Db (type (first args)))
                       [(first args) (rest args)]
                       [(d/db (get-conn)) args])]
    (-> (apply d/q query db args)
        (ent1 db)
        unpack
        unqualify)))

(defn tid
  "Creates a temporary id in the :main partition with the indicated number."
  [i]
  (d/tempid partition i))

(defn qualify
  "Qualifies bare keywords in map m with a namespace ns. If they key
ns exists in m and points to a submap, the submap is used instead."
  [m ns]
  (reduce (fn [m [k v]]
            (let [k2 (apply keyword (map name [ns k]))]
              (assoc m
                (if (attribute k2) k2 k)   ; only qualify existing attributes
                (if (component? k2)
                  (qualify v k)
                  v))))
          {}
          m))
#_ (qualify {:foo "FOO"} :order)
#_ (attribute :order/status)

(defn pack
  "Converts map m into a list of linked entities. EDN attributes are packed along the way."
  ([m]
     (vals (pack m -1 {})))
  ([m i acc]
     ;; add children
     (reduce (fn [acc [k v]]
               (let [f (partial assoc-in acc [i k])]
                 (cond (edn? k)        (f (pr-str v))
                       (component? k)  (let [i2 (-> acc count (* -1) dec)]
                                         (pack v i2 (f (tid i2))))
                       :else           (f v)
                       )))
             (assoc acc i {:db/id (tid i)}) ; add self
             m)))

;; TODO: Error handling.
;; TODO: Pass in db.
;; TODO: ? What should this return?
(defn create
  "Qualifies keys in map m using namespace ns, packs the map, and transacts the result."
  [m ns]
  (let [conn (get-conn)
        entities (-> m
                     (qualify ns)
                     pack)]
    @(d/transact conn entities)))

(defn unique-conflict?
  "Returns true if ex or its cause is a a :db.error/unique-conflict."
  [ex]
  (some #(= :db.error/unique-conflict (-> % ex-data :db/error))
        [ex (.getCause ex)]))
#_ (unique-conflict? (java.lang.Exception.))
#_ (unique-conflict? (java.lang.Exception. "foo"))
#_ (unique-conflict? (java.lang.Exception. "foo" (java.lang.Exception.)))

(defn until-unique
  "Retries f up to 5 times in the event of :db.error/unique-conflict exception."
  [f]
  (ex/try-times* 5 unique-conflict? f))

(defn init-db
  "Initializes a database and adds the schema and partitions."
  []
  (let [uri (get-uri)]
    
    ;; Create a database.
    (when (d/create-database uri) ; returns true if it was created, false if it already existed

      (log/info {:event :db/init})

      (let [;; Get a connection.
            conn (d/connect uri)]

        ;; Make a new partition.
        @(d/transact conn [{:db/id (d/tempid :db.part/db)
                            :db/ident partition
                            :db.install/_partition :db.part/db}])


        ;; Add attributes
        @(d/transact conn schema-attributes)

        ;; Load the schema.
        @(d/transact conn schema)
       ))))

#_ (init-db)

;; Update schema
#_ (let [uri (get-uri)]
     (let [conn (d/connect uri)]
       @(d/transact conn schema)
       ))

;; Delete database
#_ (let [uri (get-uri)]
     (d/delete-database uri))

;; ? How long does it take to ...
#_ (let [conn (get-conn)]
     (time
      (dotimes [i 1000]
        #_ (get-conn)   ; 90
        #_ (get-uri)    ; 1
        #_ conn         ; .4
        )))

;; From Jon's laptop (SSD)
;; synchronous:  1683-2293 ms after warmup  ~2ms per write
;; asynchronous: 373-437 ms after warmup    ~0.4 ms
;; rand:          33-38 ms after warmup
;; query existing:    150 ms after warmup   ~0.15 ms
;; query non-existing: 180 ms               ~0.18


;; Check if an entity exists with a given browser id.
#_ (let [browser-id "xyzzy"]
     (->> (get-conn)
          d/db
          (d/q '[:find ?e
                 :in ?browser-id $
                 :where [?e :browser/id ?browser-id]]
               browser-id
               )))

;; Retrieve all attributes for an entity.
#_ (let [db (d/db (get-conn))
         eid (d/q '[:find ?e
                    :where [?e :browser/id "xyzzy"]]
                  db)]
     (into {} (d/entity db (ffirst eid))))

;; Find all browser ids.
#_ (let [db (d/db (get-conn))
         id (d/q '[:find ?id
                   :where [_ :browser/id ?id]]
                 db)]
     id)

;; Count the number of browsers entities.
#_ (let [db (d/db (get-conn))
         eids (d/q '[:find ?e
                     :where [?e :browser/id]]
                   db)]
     (count eids))

;; Find all partitions in the database.
(defn get-partitions [db]
  (d/q '[:find ?ident :where [:db.part/db :db.install/partition ?p]
                             [?p :db/ident ?ident]]
       db))
#_ (-> (get-conn) d/db get-partitions)


;; if using in memory database,
;; - load the schema at startup
;; -


;; Sessions
;; - data is stored in encrypted cookie
;; - cookies expire at end of browser session
;; - session id can be tracked by event handler (if needed)
;;
;; ? how to handle authenticated sessions?

;; ring handler - session
;; ? how to link the session-id with the browser-id?
;;   ? should they not be linked at all?
;;   ? should session be a 1-many attribute of the browser?
;;   ? or should the browser be 1-1 attribute of the session?
;; ? should it work without the browser-id? Yes

;; ? is it worth linking them in datomic?
;; ? should the session have a max timeout?
;;   - hmmn, it should be fairly large e.g., days

;; ring-browser
;;   - reads browser-id cookie
;;   - looks up browser-id
;;   - sets browser-id cookie
;; ring-session
;;   - reads session-id cookie
;;   - looks up a session or creates one
;;   - associates the session with a browser (if enabled)
;;   - sets session-id cookie
;; ring-user
;;   - reads user from the session
;;   - creates a new user if one doesn't exist
;; ? hmm, how should cart be handled?
;;   - should be stored w/ the user, not the session
;; ? how should tracking events be send?
;;   ? using user-id or browser-id?

;; ubid-main
;; amazon cookies
;; x-wl-uid
;; csm-hit
;; ubid-main  # deleting this deletes the cart
;; session-id
;; session-token
;; session-time

;; browser
;; session
;;   browser
;;   user
;; user
;;   cart
;;   orders

;; ;; browser - session
;; {:db/ident :browser/session
;;  :db/id #db/id[:db.part/db]
;;  :db/valueType :db.type/ref
;;  :db/cardinality :db.cardinality/many
;;  :db.install/_attribute :db.part/db}

;; ;; attributes for session entity
;; {:db/ident :session/browser-id
;;  :db/id #db/id[:db.part/db]
;;  :db/valueType :db.type/uuid
;;  :db/cardinality :db.cardinality/one
;;  :db/unique :db.unique/value
;;  :db/index true
;;  :db/doc "Unique id for a browser"
;;  :db.install/_attribute :db.part/db}

;; browser-id is useful analytics, auditing, and forensics
;; session-id should have expected semantics
;;   - session scope (goes away when browser closes)
;; auth-token
;;   - the server enforces
;;     - expiration
;;     - access grants
;; cookies should be
;;   - sent over https only
;;   - not accesable by scripts

;; cookies
;; browser-id      long     1 yr        tracking device indefintely for analytics, auditing, and forensics
;; session-id      short    session      links to user (which contains cart) and auth-token

;; ; having the auth-token as a cookie adds no additional security
;; auth-token      short    session      storing authentication data
;;   - should be a session cookie so that it gets deleted when the user closes the browser
;;   - expiration time is enforced on the server

;; ? how to view order history if no login was created?
;;   - psuedo auth using a) order number and b) postal code? address?
;;   - follow link from purchase summary email (if they have it)
;;     - show history for that single order
;;       - grant's access in auth-token
;;     ? would this validate the email address?
;;       - maybe not, as email is inherently insecure

;; - order history should be more private than cart contents
;; - assume people use public computers

;; - cart is public until the user is logged in
;;   - at login, it gets merged w/ user account

;; ? what does amazon do if?
;;   a) i login
;;   b) add something to cart
;;   c) close browser
;;   d) return
;; ? does it still show the cart contents?
;;   ? are cart contents always public?
;;   ? is a cart analagous to an abandonded cart in a grocery store?

;; ? how to make a safe password reset process?
;;   - user submits the forgot password form
;;   - they get sent an email with an access token
;;   - they click it and open it in the browser
;;     ? require that access token request to have the same browser cookie as the forgot password form submission?
;;       - might prevent email snooping
;;       - might break the cookies are blocked or they open it in a different browser
;;       ? maybe require pasting the code in a form?

;; ? how to make a secure password recovery process?
;;   - http://www.troyhunt.com/2012/05/everything-you-ever-wanted-to-know.html
;;   - rely on openid provider

;; ? How to view order history if no login was created?
;;   - via link in order confirmation email
;;     - shows status
;;     - probably shouldn't allow access to anything else
;;     - probably shouldn't allow changes to order
;;   - call on phone and verify order info
;; ? How to let people change their order while it is in progress?
;;   - cancel
;;   - change delivery address
;;   - change quantities
;;   - change items


;; MOZILLA PERSONA
;; discussion: http://security.stackexchange.com/questions/5323/what-are-the-downsides-of-browserid-persona-compared-to-openid-oauth-facebook
;; clojure:
;;   https://github.com/osbert/persona-kit (includes friend integration)
;;   https://github.com/tmarble/nongrata (uses clojurescript)
;;   discussion: https://groups.google.com/forum/#!topic/clojure/ZytyHzOjwdw/discussion
;; setup: https://developer.mozilla.org/en/Persona/Quick_Setup
;; best practices: https://developer.mozilla.org/en-US/Persona/Security_Considerations?redirectlocale=en-US&redirectslug=Persona%2FSecurity_Considerations
;;   - may need to use csrf tokens (stored in session; submitted as post parameter w/ key forms)

;; LoginRadius (combined social login)
;; https://www.loginradius.com/loginradius-for-developers/index
;;   free - 2500 users, 4 providers
;;   basic - $12/mo, 20k users, 30 providers
;; - seems quite invasive

;; AUTHENTICATION
;; mozilla persona (w/ openid bridge; for privacy conscious)
;; facebook (for others)
;; own login (lower priority)


;; ? what if i make a purchase with somebody else's email address?
;;   ? does my ephemeral user get merged into theirs?
;;     - amazon would block this as they require a login
;;   ? would i see their order history?

;; hmm, perhaps each order is ephemeral
;;   ? is it safe to merge users when an email validation occurs?
;;     - given that email can be more easily snooped than https, this may be a bad idea
;;     - maybe just give access to that single order

;; should the session be invalidated if
;;   ? ip address changes? No. (what about companies w/ NAT and a pool of ips going out?)
;;   ? user-agent changes? No. (may be changed by browser plugin or proxy)

;; ? how to sync up datomic w/ web logs?
;;   ? log session-id cookie?

;; ? how long should cart information be stored?
;;   - indefinitely in user account
;;   - once the session is gone, the user will be inaccessable unless there is a way to authenticate

;; cart info gets stored w/ the user

;; the session just has a link the user
;;   - an anonymous user (if not authenticated)
;;   - or a known user (if authenticated)

;; - closing the browser should kill the session cookies
;; - long-term sessions (like amazon) can be added later

;; - a browser links to many sessions
;; - a session links to a single user
;; - a user links to
;;   - shopping cart
;;     ? how to enforce that there is just one cart?
;;       ? it might be nice to have more than one cart?
;;   - order history
;;   - profile data

;; - so tracking events would be associated with users


;; ? does session need to be distinct from browser-id?
;;   - if so, split out fu
;; - tracking events get assigned to the user entity
;;   - but the browser-id and session-id could be included

;; ? does the session store any data, or does it just point to a user (e.g., shadow user)

;; ? what if?
;;   - user purchases stuff on computer A
;;   - auth expires on computer A
;;   - user goes on computer B
;;   - hacker goes on computer A
;;   - hacker adds stuff to cart
;;   ? does the stuff show up in the cart on computer B?
;;     - if so, a purchase injection could be possible
;;       - amazon merges the carts, so cart injection is possible!
;;   - this would actually be useful for family browsing on amazon

;; - amazon uses a "keep me signed in checkbox"
;;   - is unchecked by default (with a warning)
;;   - sets a 2 week cookie


;; ? oops, what if a different user uses the same browser?
;; ? give the user a link to logout?
;; ? what if there is no account yet?

;; ? what happens if they logout?
;;   ? is the session terminated?
;; session-id: 175-1773104-3782800
;; session-id-time: 2082787201l
;; session-token: eOZ2E11qs47t81i9q4GCH64wj0Wjp+NCM9Ub+FofLlk/123LIthSE7IMVFkyxq6pnlA4W1BQVJytQUF+OxYWd0Uy1W6vjWTubqQ+tL9UWihIqLrWb8HDKQXwdhvKe59DjH2mYqQjWJuM7cxzHa2nAjFEES+c8Vkj+Gt01KM4TY8QbWGWfdeQCGLm62jpLQ+lc1CC3HK5B0A0fHkn119qwZcoNMrtd1ZMjA+BbKyUtm7dLrdyjQEbvM1tTtWXLdC54U1FJ8RBC7OyVfijWEyL2Q==
;; ? maybe set a special long lasting cookie once they "log-in"?

;; ? what if somebody logs in, then later another person browses?
;;   - and they change the language

;; - one that never expires (Jan 17, 2038)
;;   - identify user agent
;;     - useful to see multiple people using 1 browser
;; - one that expires after a shorter time that is a reasonable compromise (1 week? 1 month?)
;; amazon has a csm-hit cookie w/ 1 week expiration

;; x-main
;; at-main
;; s_vn
;; s_fid
;; s_dslv
;; s_nr
;; x-wl-uid
;; UserPref
;; skin
;; csm-hit
;; ubid-main

;; set http only flag in cookies to prevent access via javascript
