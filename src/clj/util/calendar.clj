(ns util.calendar
  (:import java.util.HashSet
           java.util.Locale
           org.joda.time.LocalDate
           org.joda.time.format.DateTimeFormat
           net.objectlab.kit.datecalc.common.DefaultHolidayCalendar
           net.objectlab.kit.datecalc.common.HolidayHandlerType
           net.objectlab.kit.datecalc.joda.LocalDateKitCalculatorsFactory
           ))


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
  (DefaultHolidayCalendar. holidays (LocalDate. "2013-12-01") (LocalDate. "2014-12-31")))

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

(defn format-france
  [date]
  (.. DateTimeFormat (forPattern "dd MMM") (withLocale (. Locale FRANCE)) (print date)))

#_ (format-france (business-days 10))
