{:internationalization
 {:locales ["en" "fr" "es"]
  :default-locale "en"
  :default-territories {:en "GB", :fr "FR", :de "DE", :es "ES", :it "IT"}}

 :paypal {:site "sandbox"
          :version "88.0"}

 ;; Jarvis aka Iron Man's butler (Ref. http://en.wikipedia.org/wiki/Edwin_Jarvis).
 ;; The mail account used to send internal mail notifications.
 :jarvis {:host "smtp.gmail.com"
          :user "jarvis@sistemimoderni.com"}

 :feedback {:email "info@sistemimoderni.com"}}
