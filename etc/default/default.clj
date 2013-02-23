{:internationalization {;; list of enabled languages
                        :locales ["en" "fr" "es" "it"]
                        :default-locale "en"
                        :default-territories {:en "GB", :fr "FR", :de "DE", :es "ES", :it "IT"}

                        ;; warn if url translations are missing for some locale in a
                        ;; page or url handler.
                        :require-url-translations false}

 :paypal {:site "sandbox"
          :version "88.0"}

 ;; The mail account used to send internal mail notifications.
 ;; (See http://en.wikipedia.org/wiki/Edwin_Jarvis).
 :jarvis {:host "smtp.gmail.com"
          :user "jarvis@sistemimoderni.com"}

 ;; Email address to send feedback emails to.
 :feedback {:email "info@sistemimoderni.com"}}
