(ns shipping.ups.error-codes)

(def errors
  "Copy from UPS pdf and cleanup (sed 's/\([0-9]*\) \([a-zA-Z]*\) \(.*\)/{:code \1 :type "\2" :msg "\3"}/')"
  (list
    {:code 10001 :type "Hard" :msg "The XML document is not well formed"}
    {:code 10002 :type "Hard" :msg "10006 Hard The XML document is well formed but the document is not valid"}
    {:code 10003 :type "Hard" :msg "The XML document is either empty or null. Although the document is well formed and valid, the element content contains values which do not conform to the rules and constraints contained in this specification"}
    {:code 10013 :type "Hard" :msg "20002 Hard The message is too large to be processed by the Application"}
    {:code 20001 :type "Transient" :msg "General process failure. The specified service name, {0}, and version number, {1}, combination is invalid"}
    {:code 20003 :type "Hard" :msg "Please check the server environment for the proper J2EE ws apis"}
    {:code 20006 :type "Hard" :msg "Invalid request action"}
    {:code 20012 :type "Hard" :msg "The Client Information exceeds its Maximum Limit of {0}"}
    {:code 250000 :type "Hard" :msg "No XML declaration in the XML document"}
    {:code 250001 :type "Hard" :msg "Invalid Access License for the tool. Please re-license."}
    {:code 250002 :type "Hard" :msg "Invalid UserId/Password"}
    {:code 250003 :type "Hard" :msg "Invalid Access License number"}
    {:code 250004 :type "Hard" :msg "Incorrect UserId or Password"}
    {:code 250005 :type "Hard" :msg "No Access and Authentication Credentials provided"}
    {:code 250006 :type "Hard" :msg "The maximum number of user access attempts was exceeded"}
    {:code 250007 :type "Hard" :msg "The UserId is currently locked out, please try again in 24 hours."}
    {:code 250009 :type "Hard" :msg "License Number not found in the in the UPS database"}
    {:code 250050 :type "Transient" :msg "License system not available"}
    ;; See ruby script for the rest of these
    ) )
