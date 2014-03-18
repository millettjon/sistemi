(ns sistemi.datomic
  (:require [app.config :as cf]
            [sistemi.config :as scf]
            [datomic.api :as d]
            [util.id :as id]
            [util.string :as s]
            [taoensso.timbre :as log])
  (:refer-clojure :exclude [partition]))

(def partition
  :main)

(def schema
  [;; browser
   {:db/id #db/id[:db.part/db]
    :db/ident :browser/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/value
    :db/index true
    :db/doc "A browser's unique random id for event tracking."
    :db.install/_attribute :db.part/db}

   ;; ========== PRICE ==========
   ;; price amount
   {:db/id #db/id[:db.part/db]
    :db/ident :price/amount
    :db/valueType :db.type/bigdec
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; price currency
   {:db/id #db/id[:db.part/db]
    :db/ident :price/currency
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
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

   ;; ========== ADDRESS ==========
   ;; ? should this include name? probably not, name should be part of
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
    :db/valueType :db.type/string
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
    :db.install/_attribute :db.part/db}

   ;; order - total
   {:db/id #db/id[:db.part/db]
    :db/ident :order/total
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - contact
   ;; ? should this be a component or separate entity?
   ;;   ? is it different if logged in?
   {:db/id #db/id[:db.part/db]
    :db/ident :order/contact
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; order - shipping address
   ;; ? where/how will ups info be stored? price, tracking number
   {:db/id #db/id[:db.part/db]
    :db/ident :order/shipping-address
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

        ;; Load the schema.
        @(d/transact conn schema)))))

#_ (init-db)


;;
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
