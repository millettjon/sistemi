{;; Start a swank server.
 :swank true
 
 ;; Launch the website in a browser.
 :launch-browser true

 ;; Send feedback emails to the developer's email.
 :feedback {:email (git/conf :user :email)}
 }
