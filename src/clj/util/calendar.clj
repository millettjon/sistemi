(ns util.calendar
  (require [sistemi.translate :as tr]
           [clojure.string :as str])
  (:import java.util.HashSet
           java.util.Locale
           org.joda.time.LocalDate
           org.joda.time.format.DateTimeFormat
           net.objectlab.kit.datecalc.common.DefaultHolidayCalendar
           net.objectlab.kit.datecalc.common.HolidayHandlerType
           net.objectlab.kit.datecalc.joda.LocalDateKitCalculatorsFactory))


;; Create or get the holidays.
;; See: http://www.timeanddate.com/holidays/france/2014#!hol=9
(def holidays
  (doto (HashSet.)
    (.add (LocalDate. "2013-12-25"))
    (.add (LocalDate. "2014-01-01"))
    (.add (LocalDate. "2014-04-21"))
    (.add (LocalDate. "2014-05-01"))
    (.add (LocalDate. "2014-05-08"))
    (.add (LocalDate. "2014-05-29"))
    (.add (LocalDate. "2014-06-09"))
    (.add (LocalDate. "2014-06-14"))
    (.add (LocalDate. "2014-08-15"))
    (.add (LocalDate. "2014-11-01"))
    (.add (LocalDate. "2014-11-11"))
    (.add (LocalDate. "2014-12-25"))))

(def calendar
  (DefaultHolidayCalendar. holidays (LocalDate. "2013-12-01") (LocalDate. "2030-12-31")))

;; Register the holidays, any calculator with name "FR" asked from now
;; on will receive an IMMUTABLE reference to this calendar.
(.. LocalDateKitCalculatorsFactory getDefaultInstance (registerHolidays "FR" calendar))

(defn business-days
  "Returns a date days business days in the future."
  [days]
  (let [cal (.. LocalDateKitCalculatorsFactory
                getDefaultInstance
                (getDateCalculator "FR", (. HolidayHandlerType FORWARD)))]
  (.. cal (moveByBusinessDays days) getCurrentBusinessDate)))
#_ (business-days 160)
#_ (.. DateTimeFormat (forStyle "M-") (withLocale (. Locale FRANCE)) (print (business-days 16)))
#_ (.. DateTimeFormat shortDate (withLocale (. Locale FRANCE)) (print (business-days 16)))
#_ (.. DateTimeFormat (forPattern "dd MMM") (withLocale (. Locale FRANCE)) (print (business-days 16)))

(defn- trim-pattern
  "Removes the year from a pattern."
  [s]
  (-> s
      (str/replace #"y" "")             ; remove the year
      str/trim                          ; trim spaces
      (str/replace #",$" "")            ; trim punctuation
      ))
#_ (trim-pattern "d MMM yyyy")
#_ (trim-pattern "MMM d, yyyy")

;; Date styles
;; S  6/1/14
;; M  Jun 01, 2014
;; L  June 01, 2014
;; F  Sunday, June 01, 2014
(defn format-date-dMMM
  [date]
  (let [date (LocalDate. date)
        locale (-> (tr/full-locale) name Locale.)
        pattern (->> locale
                     (DateTimeFormat/patternForStyle "M-")
                     trim-pattern)]
    (.. DateTimeFormat
        (forPattern pattern)
        (withLocale locale)
        (print date))))
#_ (format-date-dMMM (java.util.Date.))

(defn format-date-M
  [date]
  (let [date (LocalDate. date)
        locale (-> (tr/full-locale) name Locale.)]
    (.. DateTimeFormat
        (forStyle "M-")
        (withLocale locale)
        (print date))))
#_ (format-date-M (java.util.Date.))

;; ? how to get locale from 2 char keyword
#_ (Locale. "FR")

#_ (format-france (business-days 10))

;; Convert java date to joda date.
#_ (LocalDate. (java.util.Date.))

;; Convert joda date to java date.
#_ (.toDate (LocalDate.))
