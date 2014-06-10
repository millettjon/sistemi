;; Send emails to the developer's email.
(require 'git)
(let [email (git/conf :user :email)]
  {:info email
   :orders email
   :support email})
