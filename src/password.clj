;;
;; Password - Manages passwords required for integration with third party services (e.g., paypal).
;;
;; Threat Models
;; - attacker gets access to source code
;; - attacker sniffs data in transit between db and servers
;; - attacker gets access to database backup
;; - TBD
;;
;; Failure Modes
;; - attacker has access to server
;; - attacker has access to database
;; - attacker has access to both source code and database backup
;;
;; Design
;; - Encrypted passwords are stored in configuration files that get deployed to heroku.
;; - Keys and IVs are stored in the database.
;; - Each password uses a unique single use key.
;; - The database is restricted to contain the keys required for its level of access (e.g., dev/staging/prod).
;; - Key rotation should be possible without downtime.
;; - Passwords are redacted from log files.
;; - Database backups are encrypted.
;; - (TBD) The key from the database can be double encrypted with a key from a file on the
;;   server for extra prevention.
;;
;; Scripts
;; - Add new passwords
;; - Delete old passwords
;; - Audit passwords to determine when rotation is required.
;;
;; Password Table
;;   id
;;   name            setting name used to lookup password (e.g., paypal.password)
;;   key             encryption key
;;   iv              initialization vector
;;   creation_date
;;   description
;;
;;   unique constraints:
;;     key
;;     name, creation_date
;;
;;   Note: multiple keys with one name can exist while a rotation is in progress
;;
;; Questions
;; - Is an IV necessary if keys are single use?
;; - Is there a better name that doesn't conflict with the concept of storing hashed passwords used for
;;   authenticating users? Wallet, Keychain
;;
