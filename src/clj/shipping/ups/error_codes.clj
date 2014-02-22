(ns shipping.ups.error-codes)

(def errors
  "Created a small ruby script to take 'pdftotext -raw -layout foo.pdf' and create a Clojure map structure."
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
    {:code 120001 :type "Transient" :msg "XML Shipping System is unavailable, please try again later." }
    {:code 120014 :type "Warning" :msg "A Large Air Package Minimum Surcharge has been applied to Package %package.index%." }
    {:code 120016 :type "Warning" :msg "Customs Invoice is required when the shipment is tendered to UPS." }
    {:code 120017 :type "Warning" :msg "If the value of the goods you are shipping is above 6,000 Euro or local equivalent, you must supply an EUR1 form in addition to signing your invoice." }
    {:code 120018 :type "International" :msg "Form Data Holder Exception" }
    {:code 120019 :type "Warning" :msg "The location ID you provided is not valid. Valid location ID consists of 3 to 10 alpha numeric characters." }
    {:code 120020 :type "Hard" :msg "Max packages per shipment exceeded." }
    {:code 120021 :type "Hard" :msg "The same shipment cannot be requested to be created more than once." }
    {:code 120022 :type "Warning" :msg "For packages with high value report: give them to a UPS driver or UPS Customer Center representative to ensure he signs one copy of the receipt and returns it to you. This is your proof that UPS has accepted the package(s), and will be required for submitting a claim." }
    {:code 120023 :type "Warning" :msg "Using user generated forms is not permitted. Please attach your user generated forms with your shipment." }
    {:code 120024 :type "Warning" :msg "Uploading of your user generated International Forms was not successful. Please attach the international forms to your shipment." }
    {:code 120050 :type "Hard" :msg "RequestAction has an unsupported value." }
    {:code 120051 :type "Hard" :msg "Invalid RequestOption" }
    {:code 120100 :type "Hard" :msg "Missing or Invalid shipper number" }
    {:code 120101 :type "Hard" :msg "Missing/Invalid shipper name" }
    {:code 120102 :type "Hard" :msg "Missing/Invalid shipper address line 1" }
    {:code 120103 :type "Hard" :msg "Invalid shipper address line 2" }
    {:code 120104 :type "Hard" :msg "Invalid shipper address line 3" }
    {:code 120105 :type "Hard" :msg "Missing/Invalid Shipper City" }
    {:code 120106 :type "Hard" :msg "Missing/Invalid Shipper StateProvinceCode" }
    {:code 120107 :type "Hard" :msg "Missing/Invalid Shipper PostalCode. (Description)" }
    {:code 120108 :type "Hard" :msg "Missing/Invalid Shipper CountryCode" }
    {:code 120109 :type "Hard" :msg "Missing/Invalid Shipper PhoneNumber" }
    {:code 120110 :type "Hard" :msg "Missing/Invalid Shipper AttentionName" }
    {:code 120111 :type "Hard" :msg "Shipper Email Address cannot exceed a length of 50 characters" }
    {:code 120112 :type "Hard" :msg "Shipper Email Address is an invalid format" }
    {:code 120113 :type "Hard" :msg "Shipper number must contain alphanumeric characters only" }
    {:code 120114 :type "Hard" :msg "Shipper phone extension cannot exceed the length of 4." }
    {:code 120115 :type "Hard" :msg "Shipper PhoneNumber must be at least 10 alphanumeric characters" }
    {:code 120116 :type "Hard" :msg "Shipper phone extension must contain only numbers" }
    {:code 120117 :type "Hard" :msg "Shipper phone extension is allowed only if the shipper is located in US, Puerto Rico or Canada" }
    {:code 120118 :type "Hard" :msg "Shipper phone extension is only valid if a phone number is given" }
    {:code 120119 :type "Hard" :msg "Shipper phone number and phone extension together cannot be more than 15 digits long" }
    {:code 120120 :type "Hard" :msg "The country associated with Shippers ShipperNumber must be the same as the shipments Shippers country." }
    {:code 120121 :type "Hard" :msg "The Shippers shipper number cannot be used for the shipment." }
    {:code 120122 :type "Hard" :msg "Invalid Shipper ShipperNumber" }
    {:code 120124 :type "Hard" :msg "The requested service is unavailable between the selected locations" }
    {:code 120125 :type "Hard" :msg "Consignee billing is only supported in US or Pureto Rico" }
    {:code 120200 :type "Hard" :msg "Missing/Invalid ShipTo CompanyName" }
    {:code 120201 :type "Hard" :msg "Missing/Invalid ShipTo AttentionName" }
    {:code 120202 :type "Hard" :msg "Missing/Invalid ShipTo AddressLine1" }
    {:code 120203 :type "Hard" :msg "Invalid ShipTo AddressLine2" }
    {:code 120204 :type "Hard" :msg "Invalid ShipTo AddressLine3" }
    {:code 120205 :type "Hard" :msg "Missing/Invalid ShipTo/ City" }
    {:code 120206 :type "Hard" :msg "Missing/Invalid ShipTo StateProvinceCode" }
    {:code 120207 :type "Hard" :msg "Missing/Invalid ShipTo PostalCode. (Description)" }
    {:code 120208 :type "Hard" :msg "Missing/Invalid/Unsupported ShipTo CountryCode" }
    {:code 120209 :type "Hard" :msg "Missing/Invalid ShipTo PhoneNumber" }
    {:code 120210 :type "Hard" :msg "ShipTo EmailAddress cannot exceed a length of 50 characters" }
    {:code 120211 :type "Hard" :msg "ShipTo EmailAddress is an invalid format" }
    {:code 120212 :type "Hard" :msg "ShipTo PhoneExtension cannot exceed the length of 4." }
    {:code 120213 :type "Hard" :msg "ShipTo PhoneNumber must be at least 10 alphanumeric characters" }
    {:code 120214 :type "Hard" :msg "ShipTo PhoneExtension must contain only numbers" }
    {:code 120216 :type "Hard" :msg "ShipTo PhoneExtension is only valid if a phone number is given" }
    {:code 120217 :type "Hard" :msg "ShipTo phone number and phone extension together cannotbe more than 15 digits long" }
    {:code 120218 :type "Hard" :msg "Missing or Invalid ShipTo tax identification. TaxID may not exceed a length of 15." }
    {:code 120300 :type "Hard" :msg "Missing/Invalid ShipFrom CompanyName" }
    {:code 120301 :type "Hard" :msg "Missing/Invalid ShipFrom AttentionName" }
    {:code 120302 :type "Hard" :msg "Missing/Invalid ShipFrom AddressLine1" }
    {:code 120303 :type "Hard" :msg "Invalid ShipFrom AddressLine2" }
    {:code 120304 :type "Hard" :msg "Invalid ShipFrom AddressLine3" }
    {:code 120305 :type "Hard" :msg "Missing/Invalid ShipFrom City" }
    {:code 120306 :type "Hard" :msg "Missing/Invalid ShipFrom StateProvinceCode" }
    {:code 120307 :type "Hard" :msg "Missing/Invalid ShipFrom PostalCode. (Description)" }
    {:code 120308 :type "Hard" :msg "Missing/Invalid/Unsupported ShipFrom CountryCode" }
    {:code 120309 :type "Hard" :msg "Missing/Invalid ShipFrom PhoneNumber" }
    {:code 120310 :type "Hard" :msg "ShipFrom EmailAddress cannot exceed a length of 50 characters" }
    {:code 120311 :type "Hard" :msg "ShipFrom EmailAddress is an invalid format" }
    {:code 120312 :type "Hard" :msg "ShipFromPhoneExtension cannot exceed the length of 4." }
    {:code 120313 :type "Hard" :msg "ShipFrom PhoneNumber must be at least 10 alphanumeric characters" }
    {:code 120314 :type "Hard" :msg "ShipFromPhoneExtension must contain only numbers" }
    {:code 120316 :type "Hard" :msg "ShipFromPhoneExtension is only valid if a phone number is given" }
    {:code 120317 :type "Hard" :msg "The ShipFrom country must be the same as the Shipper country" }
    {:code 120318 :type "Hard" :msg "ShipFrom phone number and phone extension together cannot be more than 15 digits long" }
    {:code 120400 :type "Hard" :msg "Missing/Invalid shipment payment method" }
    {:code 120402 :type "Hard" :msg "Missing/Invalid credit card type" }
    {:code 120403 :type "Hard" :msg "Missing/Invalid credit card number" }
    {:code 120404 :type "Hard" :msg "Missing/Invalid credit card expiration date" }
    {:code 120406 :type "Hard" :msg "creditcard number is not valid for the credit card type" }
    {:code 120407 :type "Hard" :msg "The credit card provided as the payment method has expired." }
    {:code 120408 :type "Hard" :msg "Missing credit card type" }
    {:code 120410 :type "Hard" :msg "One payment method is required." }
    {:code 120411 :type "Hard" :msg "Credit card payment is not allowed for this shipment." }
    {:code 120412 :type "Hard" :msg "{Description}" }
    {:code 120413 :type "Hard" :msg "The UPS account number provided as the payment method cannot be billed, please try another account." }
    {:code 120414 :type "Hard" :msg "Credit card authorization failed, contact your financial institution" }
    {:code 120415 :type "Hard" :msg "PaymentInformation/Prepaid/BillShipper/AccountNumber must be the same shipper number asShipper/ShipperNumber" }
    {:code 120416 :type "Hard" :msg "A single billing option is required per shipment." }
    {:code 120430 :type "Hard" :msg "Missing/Invalid country code of billed third party" }
    {:code 120437 :type "Hard" :msg "Missing/Invalid PostalCode of payment account" }
    {:code 120500 :type "Hard" :msg "Missing/Invalid Service/Code" }
    {:code 120501 :type "Hard" :msg "Invalid Shipment/ReferenceNumber/Value" }
    {:code 120502 :type "Hard" :msg "InvoiceLineTotal/MonetaryValue must be greater than 0" }
    {:code 120503 :type "Hard" :msg "Shipment Description cannot exceed the length of 35 characters" }
    {:code 120504 :type "Hard" :msg "InvoiceLineTotal is not allowed for this shipment" }
    {:code 120505 :type "Hard" :msg "Saturday Delivery Option cannot be used for this shipment" }
    {:code 120506 :type "Hard" :msg "Invalid Shipment/ReferenceNumber/ Code" }
    {:code 120508 :type "Hard" :msg "Missing/Invalid On Call Air pickup date" }
    {:code 120510 :type "Hard" :msg "Missing/Invalid latest On Call Air pickup time" }
    {:code 120511 :type "Hard" :msg "Invalid earliest or latest On Call Air pickup time" }
    {:code 120512 :type "Hard" :msg "Shipment Description is required for this shipment." }
    {:code 120513 :type "Hard" :msg "The first Email Address used for {Quantum Viewnotification type} exceeds the maximum length of 50 characters." }
    {:code 120514 :type "Hard" :msg "Invalid format for first Email Address used for {Quantum View notification type}" }
    {:code 120515 :type "Hard" :msg "{ notification type} Memo cannot exceed the length of 150 characters" }
    {:code 120516 :type "Hard" :msg "The first Email Address used for {Quantum View notification type} is missing or contains invalid characters" }
    {:code 120517 :type "Hard" :msg "The {order} Email Address used for { notification type} exceeds the maximum length of 50 characters" }
    {:code 120518 :type "Hard" :msg "The format of the {order} Email Address entered for { notification type} is invalid" }
    {:code 120519 :type "Hard" :msg "The {order} Email Address entered for { notification type} is missing or contains invalid characters" }
    {:code 120520 :type "Hard" :msg "For a shipment, the maximum number of Email Addresses allowed for Quantum View Notification is 5" }
    {:code 120521 :type "Hard" :msg "For a shipment, the maximum number of Memos allowed for {Quantum View notification type} is 1" }
    {:code 120522 :type "Hard" :msg "Alternate Delivery Time is not valid for any available services" }
    {:code 120523 :type "Hard" :msg "Shipment/Documents Only is invalid with the shipments origin/destination pair" }
    {:code 120524 :type "Hard" :msg "Package Pickup Request is not available with this shipments service" }
    {:code 120525 :type "Hard" :msg "AlternateDeliveryTime error: (Description)" }
    {:code 120526 :type "Hard" :msg "InvoiceLineTotal/MonetaryValue may not exceed the length of 8" }
    {:code 120527 :type "Hard" :msg "Cannot use multiple types of currency in a shipment" }
    {:code 120528 :type "Hard" :msg "A shipment can have only one type of UnitOfMeasurement for Dimensions" }
    {:code 120529 :type "Hard" :msg "A shipment can have only one type of UnitOfMeasurement for a Weight" }
    {:code 120530 :type "Hard" :msg "SuiteRoomID length of shipment pickup cannot exceed 8 characters" }
    {:code 120531 :type "Hard" :msg "FloorID length of shipment pickup cannot exceed 3 characters" }
    {:code 120532 :type "Hard" :msg "Location length of shipment pickup cannot exceed 11 characters" }
    {:code 120533 :type "Hard" :msg "The earliest shipment pickup time is required" }
    {:code 120534 :type "Hard" :msg "The latest shipment pickup time is required" }
    {:code 120535 :type "Hard" :msg "Contact name of shipment pickup is required" }
    {:code 120536 :type "Hard" :msg "Contact phone number of shipment pickup is required" }
    {:code 120537 :type "Hard" :msg "Invalid/Missing the earliest shipment pickup time" }
    {:code 120538 :type "Hard" :msg "Shipment Pickup Error: (further description is provided in XML)" }
    {:code 120539 :type "Hard" :msg "InvoiceLineTotal/ MonetaryValue is required and must be a whole number" }
    {:code 120540 :type "Hard" :msg "On Call Air Pickup is not available for this shipment" }
    {:code 120541 :type "Hard" :msg "Shipment/ReferenceNumber is not allowed for this shipment" }
    {:code 120542 :type "Hard" :msg "Package/ReferenceNumber is not allowed for this shipment" }
    {:code 120543 :type "Hard" :msg "No more than 2 Shipment/ReferenceNumber can be given per shipment" }
    {:code 120544 :type "Hard" :msg "No more than 2 Package/ReferenceNumber can be given per package" }
    {:code 120545 :type "Hard" :msg "Shipment/Description is not valid with this shipment" }
    {:code 120546 :type "Hard" :msg "(UOM Weight) is not a valid unit of measurement for weight for this shipment" }
    {:code 120547 :type "Hard" :msg "(UOM Dimension) is not a valid unit of measurement for dimensions." }
    {:code 120548 :type "Hard" :msg "A shipment cannot have a KGS/IN or LBS/CM as its unit of measurements" }
    {:code 120597 :type "Hard" :msg "Invalid COD currency code. Please use the destination country currency code." }
    {:code 120598 :type "Hard" :msg "Package Delivery Confirmation is not allowed with the shipment origin/destination " }
    {:code 120599 :type "Hard" :msg "Invalid package Delivery Confirmation type" }
    {:code 120600 :type "Hard" :msg "Missing Package/PackagingType/Code" }
    {:code 120601 :type "Hard" :msg "Missing/Invalid Package weight" }
    {:code 120602 :type "Hard" :msg "Missing/Invalid Combination of package length, width, and height values. " }
    {:code 120603 :type "Hard" :msg "Invalid Package/Reference/Value" }
    {:code 120604 :type "Hard" :msg "Invalid package declared value" }
    {:code 120605 :type "Hard" :msg "Mismatch package dimensions with package type" }
    {:code 120606 :type "Hard" :msg "Mismatch package dimensions, package type and package weight " }
    {:code 120607 :type "Hard" :msg "Invalid Package/Reference/Code" }
    {:code 120608 :type "Hard" :msg "Package weight is required" }
    {:code 120609 :type "Hard" :msg "All Package Dimensions are required and each must be greater than 0 " }
    {:code 120610 :type "Hard" :msg "Invalid COD monetary value" }
    {:code 120611 :type "Hard" :msg "The contact name of verbal confirmation cannot exceed a length of 35 " }
    {:code 120612 :type "Hard" :msg "The contact phone number of verbal confirmation cannot exceed the length of 15 " }
    {:code 120613 :type "Hard" :msg "The contact phone number of verbal confirmation must be at least 10 alphanumeric characters " }
    {:code 120614 :type "Hard" :msg "The contact phone extension of verbal confirmation cannot exceed the length of 4 " }
    {:code 120615 :type "Hard" :msg "The contact phone extension of verbal confirmation must contain numbers only " }
    {:code 120616 :type "Hard" :msg "Package weight cannot exceed a length of 6" }
    {:code 120617 :type "Hard" :msg "Package declared value cannot exceed a value of 999" }
    {:code 120618 :type "Hard" :msg "Contact name is required for verbal confirmation of delivery" }
    {:code 120619 :type "Hard" :msg "Contact phone number is required for verbal confirmation of delivery " }
    {:code 120620 :type "Hard" :msg "Package/PackageWeight, Package/DimensionalWeight, Package/OversizePackage and  Package/Dimensions do not apply to UPS Envelopes " }
    {:code 120621 :type "Hard" :msg "Package declared value cannot be greater than" }
    {:code 120622 :type "Hard" :msg "Package description is required" }
    {:code 120623 :type "Hard" :msg "Invalid Package description" }
    {:code 120624 :type "Hard" :msg "Only one package is allowed for this movement." }
    {:code 120626 :type "Hard" :msg "The COD Funds Code is invalid. Please check the value entered" }
    {:code 120627 :type "Hard" :msg "The COD Code is invalid. Please check the value entered" }
    {:code 120654 :type "Hard" :msg "No more than two email addresses for Receiver Return Notification are allowed in a shipment/SubjectCode" }
    {:code 120651 :type "Invalid" :msg "PackageServiceOptions/LabelDelivery/EMailMessage/SubjectCode" }
    {:code 120652 :type "Invalid" :msg "ShipmentServiceOptionsNotification/EMailMessage/SubjectCode" }
    {:code 120653 :type "Invalid" :msg "PackageServiceOptionsNotification/ EMailMessage " }
    {:code 120655 :type "Hard" :msg "{Email field} of Receiver Return Notification is only allowed to be specified at the first package. " }
    {:code 120658 :type "Hard" :msg "Receiver Return Notification is not allowed for the shipment without return service " }
    {:code 120659 :type "Hard" :msg "ShipmentServiceOptions/ShipmentNotification is not allowed with return service " }
    {:code 120660 :type "Hard" :msg "ShipmentServiceOptions/ShipmentNotification is not allowed with ShipmentServiceOptions/Notification " }
    {:code 120661 :type "Hard" :msg "For a shipment, the maximum number of {email field} allowed for Quantum View notification is 1. " }
    {:code 120662 :type "Hard" :msg "Invalid Subject Code of {Notification type}" }
    {:code 120663 :type "Hard" :msg "Invalid From name of {Notification type}" }
    {:code 120664 :type "Hard" :msg "Invalid UndeliverableEMailAddress of {Notification type}" }
    {:code 120665 :type "Hard" :msg "Invalid FromEMailAddress of {Notification type}" }
    {:code 120666 :type "Hard" :msg "Missing/Invalid subject text of {Notification type}" }
    {:code 120667 :type "Hard" :msg "{field} is not allowed in Quantum View Notification" }
    {:code 120670 :type "Hard" :msg "Shipper Release is not allowed with Return Service" }
    {:code 120700 :type "Hard" :msg "Missing/Invalid LabelSpecification/ LabelPrintMethod/Code" }
    {:code 120701 :type "Hard" :msg "Missing/Invalid LabelSpecification/ HTTPUserAgent" }
    {:code 120702 :type "Hard" :msg "Missing/Invalid LabelSpecification/ LabelImageFormat/Code" }
    {:code 120703 :type "Hard" :msg "Missing/Invalid Combination of LabelSpecification/ LabelStockSize " }
    {:code 120704 :type "Hard" :msg "Invalid LabelSpecification/ LabelStockSize/Height" }
    {:code 120705 :type "Hard" :msg "Invalid LabelSpecification/LabelStockSize/Width" }
    {:code 120759 :type "Hard" :msg "Invalid MovementReferenceNumber" }
    {:code 120760 :type "Hard" :msg "Invalid MovementReferenceNumber" }
    {:code 120761 :type "Hard" :msg "Invalid MovementReferenceNumber" }
    {:code 120801 :type "Hard" :msg "Address Validation Error on Shipper address" }
    {:code 120802 :type "Hard" :msg "Address Validation Error on ShipTo address" }
    {:code 120803 :type "Hard" :msg "Address Validation Error on ShipFrom address" }
    {:code 120804 :type "Warning" :msg "Address Validation Warning on Shipper address. {Description}" }
    {:code 120805 :type "Warning" :msg "Address Validation Warning on ShipTo address. {Description}" }
    {:code 120806 :type "Warning" :msg "Address Validation Warning on ShipFrom address {Description}" }
    {:code 120900 :type "Warning" :msg "User Id and Shipper Number combination is not eligible  to receive Account Based Rates. " }
    {:code 120901 :type "Warning" :msg "Negotiated Rates are unavailable at this time." }
    {:code 120971 :type "Warning" :msg "Your invoice may vary from the displayed reference rates" }
    {:code 121005 :type "Hard" :msg "The COD option cannot be combined with the Return Services option. " }
    {:code 121006 :type "Hard" :msg "The COD option cannot be combined with the UPS Import Control option. " }
    {:code 121011 :type "Hard" :msg "Dangerous Goods cannot be shipped using UPS Import Control." }
    {:code 121015 :type "Hard" :msg "The Saturday Delivery option cannot be combined with the Return Services option. " }
    {:code 121020 :type "Hard" :msg "The Delivery Confirmation option cannot be combined with the Return Services option. " }
    {:code 121021 :type "Hard" :msg "The Delivery Confirmation option cannot be combined with the UPS Import Control option. " }
    {:code 121025 :type "Hard" :msg "The maximum declared amount $50,000 is exceeded" }
    {:code 121030 :type "Hard" :msg "Packages must weigh more than zero pounds." }
    {:code 121031 :type "Hard" :msg "Packages must weigh more than zero kilogram" }
    {:code 121035 :type "Hard" :msg "{description}" }
    {:code 121036 :type "Hard" :msg "{description}" }
    {:code 121041 :type "Hard" :msg "The UPS Import Control accessories are unavailable with the requested service. " }
    {:code 121045 :type "Hard" :msg "The Saturday Delivery option is unavailable with the requested service. The Saturday Delivery option is  unavailable with the requested service. " }
    {:code 121047 :type "Hard" :msg "Saturday Delivery may not be combined with the package type for the given product. { description}" }
    {:code 121050 :type "Hard" :msg "Package exceeds the maximum size total constraints {description} inches. ." }
    {:code 121055 :type "Hard" :msg "Package exceeds the maximum length constraint of {description} cm." }
    {:code 121056 :type "Hard" :msg "Package exceeds the maximum length constraint of" }
    {:code 121057 :type "Hard" :msg "The measurement system is not valid" }
    {:code 121063 :type "Hard" :msg "Accessorial cannot be shipped with the selected service." }
    {:code 121070 :type "Hard" :msg "{Rating error description}." }
    {:code 121085 :type "Hard" :msg "The requested accessory cannot be added to the shipment." }
    {:code 121090 :type "Hard" :msg "{Rating error description}" }
    {:code 121091 :type "Hard" :msg "{Rating error description}" }
    {:code 121100 :type "Hard" :msg "The selected services invalid for the shipment origin." }
    {:code 121105 :type "Hard" :msg "The given accessory key is invalid for the shipment origin." }
    {:code 121106 :type "Hard" :msg "The specified country, origin or destination, is not supported." }
    {:code 121107 :type "Hard" :msg "A blank origin postal was specified." }
    {:code 121109 :type "Hard" :msg "The Delivery Area Surcharge Tier looked up is invalid" }
    {:code 121115 :type "Hard" :msg "The COD amount must be greater than zero" }
    {:code 121120 :type "Hard" :msg "The COD amount cannot exceed $1,000 when a cashiers check or money order is requested. " }
    {:code 121121 :type "Hard" :msg "The COD value entered exceeds the maximum allowed for cash of {amount} ({currency code}) " }
    {:code 121125 :type "Hard" :msg "The COD amount cannot exceed $50,000." }
    {:code 121126 :type "Hard" :msg "The COD value entered exceeds the maximum allowed for check of {amount} ({currency code}) " }
    {:code 121131 :type "Hard" :msg "The Saturday Pickup option cannot be combined with the UPS Import Control option. " }
    {:code 121135 :type "Hard" :msg "The Saturday Pickup option cannot be combined with the Saturday Delivery option. " }
    {:code 121140 :type "Hard" :msg "Saturday Pickup is unavailable with the selected service." }
    {:code 121150 :type "Hard" :msg "Delivery confirmation is unavailable with the selected service." }
    {:code 121155 :type "Hard" :msg "The COD option is unavailable with the selected service, UPS account type, and/or with the shipments origin/destination pair. " }
    {:code 121156 :type "Hard" :msg "Package Level COD is not valid for the shipment origin and/or destination " }
    {:code 121160 :type "Hard" :msg "The accessory is invalid with the billing option." }
    {:code 121166 :type "Hard" :msg "The Verbal Confirmation of Delivery option cannot be combined with the UPS Import Control option. " }
    {:code 121170 :type "Hard" :msg "The Delivery Confirmation option cannot be combined with the Verbal Confirmation of Delivery option. " }
    {:code 121175 :type "Hard" :msg "Verbal Confirmation of Delivery is unavailable with the selected service. " }
    {:code 121180 :type "Hard" :msg "Consignee Billing is unavailable with the selected service." }
    {:code 121182 :type "Hard" :msg "The payer's transportation country is not valid for carbon neutral shipments. " }
    {:code 121185 :type "Hard" :msg "The selected service is unavailable to the desired country" }
    {:code 121195 :type "Hard" :msg "The selected billing option is unavailable with the selected service. " }
    {:code 121196 :type "Hard" :msg "The selected billing option is unavailable with UPS Letters." }
    {:code 121200 :type "Hard" :msg "Saturday Delivery is unavailable to desired destination." }
    {:code 121205 :type "Hard" :msg "Additional Handling is unavailable with the selected service." }
    {:code 121206 :type "Hard" :msg "Additional Handling is unavailable with UPS Letters." }
    {:code 121208 :type "Hard" :msg "UPS Next Day Air Early A.M. service is not available to the requested destination. Please select UPS Next Day Air  service as an alternative. " }
    {:code 121210 :type "Hard" :msg "The selected service is not available from the origin to the destination. " }
    {:code 121211 :type "Hard" :msg "Rating Error: (Description)" }
    {:code 121212 :type "Hard" :msg "{Rating error description}" }
    {:code 121213 :type "Hard" :msg "{Rating error description}" }
    {:code 121214 :type "Hard" :msg "UPS cannot ship from the origin country to the destination country " }
    {:code 121215 :type "Hard" :msg "The selected service is not available to residential destinations." }
    {:code 121230 :type "Hard" :msg "Next Day Air Early AM Surcharge is unavailable with the selected service. " }
    {:code 121231 :type "Hard" :msg "Switzerland Domestic Container must weigh greater than 2 KG" }
    {:code 121232 :type "Hard" :msg "Worldwide Express Plus from Europe to Switzerland must be UPS Envelope or document only " }
    {:code 121235 :type "Hard" :msg "{rating error description}" }
    {:code 121245 :type "Hard" :msg "Saturday Pickup and Shipper Duty Fees cannot be applied to the same package. " }
    {:code 121250 :type "Hard" :msg "Saturday Delivery and Shipper Duty Fees cannot be applied to the same package. " }
    {:code 121255 :type "Hard" :msg "Shipper Duty Fee is unavailable with the selected service." }
    {:code 121260 :type "Hard" :msg "Shipper Duty Fee is unavailable with the selected billing option." }
    {:code 121261 :type "Hard" :msg "Accessory may not be combined with the product." }
    {:code 121262 :type "Hard" :msg "Accessory may not be combined with the accessory." }
    {:code 121265 :type "Hard" :msg "The selected billing option is unavailable to the desired country." }
    {:code 121266 :type "Hard" :msg "The currency code is invalid for the shipment." }
    {:code 121267 :type "Hard" :msg "The Ship From Country is invalid for the credit card." }
    {:code 121268 :type "Hard" :msg "The Billing address Country is invalid for the credit card." }
    {:code 121285 :type "Hard" :msg "{Rating error description}" }
    {:code 121286 :type "Hard" :msg "{Rating error description}" }
    {:code 121290 :type "Hard" :msg "The given billing option is invalid" }
    {:code 121295 :type "Hard" :msg "{Rating error description}" }
    {:code 121300 :type "Hard" :msg "Shipper Pays Duty - Tax Unpaid is unavailable with the selected accessorial. " }
    {:code 121305 :type "Hard" :msg "Shipper Pays Duty - Tax Unpaid is unavailable with the selected service. " }
    {:code 121310 :type "Hard" :msg "Shipper Pays Duty - Tax Unpaid is unavailable with the selected billing option. " }
    {:code 121315 :type "Hard" :msg "The Authorized Return Service is unavailable with the selected accessorial. " }
    {:code 121317 :type "Hard" :msg "The UPS Import Control option is unavailable with the selected accessory. " }
    {:code 121320 :type "Hard" :msg "The Authorized Return Service is unavailable with the selected service. " }
    {:code 121325 :type "Hard" :msg "The Authorized Return Service is unavailable with the selected billing option. " }
    {:code 121330 :type "Hard" :msg "The Certificate of Origin is unavailable with the selected accessorial. " }
    {:code 121335 :type "Hard" :msg "The Certificate of Origin is unavailable with the selected service." }
    {:code 121340 :type "Hard" :msg "The Certificate of Origin is unavailable with the selected billing option. " }
    {:code 121345 :type "Hard" :msg "The Shipper Export Declaration is unavailable with the selected accessorial. " }
    {:code 121350 :type "Hard" :msg "The Shipper Export Declaration is unavailable with the selected service. " }
    {:code 121355 :type "Hard" :msg "The Shipper Export Declaration is unavailable with the selected billing option. " }
    {:code 121360 :type "Hard" :msg "The Worldwide Express Plus Surcharge is unavailable with the selected service. " }
    {:code 121363 :type "Hard" :msg "The One Time Pickup Surcharge is unavailable with the selected service. " }
    {:code 121365 :type "Hard" :msg "Accessorial is not available with the selected access method." }
    {:code 121370 :type "Hard" :msg "Invalid access method." }
    {:code 121375 :type "Hard" :msg "Letter service is invalid for shipments with more than 1 package. International shipment cannot be made  with multiple 10KG or 25KG packages " }
    {:code 121452 :type "Hard" :msg "An Import Control option and a Return Service option are not valid on the same shipment. " }
    {:code 121453 :type "Hundredweight" :msg "is not valid with Return Service options" }
    {:code 121460 :type "Hard" :msg "Shipments cannot exceed a COD amount of $5,000 when requesting a cashiers check or money order. " }
    {:code 121500 :type "Hard" :msg "{Rating error description}" }
    {:code 121501 :type "Hard" :msg "{Rating error Description}" }
    {:code 121502 :type "Hard" :msg "The selected country does not allow forward movements." }
    {:code 121510 :type "Hard" :msg "Unsupported package type." }
    {:code 121511 :type "Hard" :msg "Unsupported accessory type" }
    {:code 121512 :type "Hard" :msg "Unsupported billing option" }
    {:code 121513 :type "Hard" :msg "Unsupported service" }
    {:code 121515 :type "Warning" :msg "Weight of (actual weight) exceeds maximum for rating  the requested container. Using standard package rates " }
    {:code 121520 :type "Hard" :msg "The UPS account number specified for Transportation charges is invalid for Split Duty VAT shipment. " }
    {:code 121521 :type "Hard" :msg "Invalid payer of Duty and Tax" }
    {:code 121522 :type "Hard" :msg "The payment information must be the same for both Transportation charges and Duty and Tax charges  when Bill to Shipper or Bill to Receiver is specified as the  payer. " }
    {:code 121523 :type "Hard" :msg "Invalid GoodsNotInFreeCirculationIndicator" }
    {:code 121524 :type "Warning" :msg "The payer of Duty and Tax charges is not required for UPS  Letter, Documents of No Commercial Value or Qualified  Domestic Shipments. " }
    {:code 121526 :type "Hard" :msg "The payment method specified for Transportation charges is invalid for return service. " }
    {:code 121527 :type "Hard" :msg "The payer of Transportation charges is required. /ShipmentCharge/Type" }
    {:code 121528 :type "Hard" :msg "Missing or invalid ItemizedPaymentInformation" }
    {:code 121529 :type "Hard" :msg "A single payment method is required per shipment charge." }
    {:code 121530 :type "Hard" :msg "The payer of Duty and Tax charges is required." }
    {:code 121531 :type "Hard" :msg "Invalid SplitDutyVATIndicator" }
    {:code 121532 :type "Hard" :msg "The payer of Duty and Tax charges is invalid for Split Duty VAT shipment. " }
    {:code 121534 :type "Hard" :msg "The payer of Transportation charges and the payer of Duty and Tax charges must not be the same for  Split Duty VAT shipment. " }
    {:code 121535 :type "Hard" :msg "The payer of Transportation charges is invalid for Split Duty VAT shipment. " }
    {:code 121536 :type "Hard" :msg "Bill Receiver is an invalid payer of an international return movement. " }
    {:code 121537 :type "Hard" :msg "A payer is required when specifying a shipment charge." }
    {:code 121538 :type "Hard" :msg "For a domestic return movement, the payer of the freight must be in the country of the return movement. " }
    {:code 121539 :type "Hard" :msg "For an EU return movement, the payer of the freight must be in an EU country. " }
    {:code 121565 :type "Hard" :msg "The UPS Returns Flexible Access option is unavailable with the requested accessory. " }
    {:code 121570 :type "Hard" :msg "The UPS Returns Flexible Access option is available only with Return Services Print Mail, Electronic  Return Label, and Print Return Label. " }
    {:code 121575 :type "Hard" :msg "The UPS Returns Flexible Access option is unavailable with Additional Handling. " }
    {:code 121580 :type "Hard" :msg "The UPS Returns Flexible Access option is unavailable with Large Package. " }
    {:code 121585 :type "Hard" :msg "The UPS Returns Flexible Access option is available for US and Puerto Rico shippers only. " }
    {:code 121590 :type "Hard" :msg "The maximum per package weight for the UPS Returns Flexible Access option is ... " }
    {:code 121595 :type "Hard" :msg "The maximum length (the longest side) of a UPS Returns Flexible Access package is ... " }
    {:code 121600 :type "Hard" :msg "The maximum length of the second-longest side of a UPS Returns Flexible Access package is ... " }
    {:code 121605 :type "Hard" :msg "The maximum total size constraint (length + girth, where girth is 2 x width plus 2 x height) of a UPS Returns Flexible  Access package is ... " }
    {:code 121610 :type "Hard" :msg "The maximum declared value amount for the UPS Returns Flexible Access option is ... " }
    {:code 121615 :type "Hard" :msg "All packages in the shipment must have the UPS Returns Flexible Access option if 1 package has it. " }
    {:code 121780 :type "Hard" :msg "Commercial Invoice Removal is valid only for Import Control shipments. " }
    {:code 121801 :type "Hard" :msg "The country selected for transportation charges does not allow for Third Party payment option. " }
    {:code 121802 :type "Hard" :msg "The country selected for duty/taxes charges does not allow for Third Party payment option. " }
    {:code 123005 :type "Warning" :msg "(Description)" }
    {:code 123010 :type "Warning" :msg "Package(s) in this shipment contains a warning: (Description)" }
    {:code 123020 :type "Warning" :msg "Invalid Ship From postal code" }
    {:code 123021 :type "Warning" :msg "Invalid Ship To postal code" }
    {:code 124022 :type "Hard" :msg "'Invalid number of shipment level notifications" }
    {:code 123060 :type "Warning" :msg "The weight exceeds the limit for the UPS Letter/Envelope rate and will  be rated using the weight " }
    {:code 125000 :type "Hard" :msg "Missing or invalid shipment digest." }
    {:code 128001 :type "Hard" :msg "Invalid or missing international forms form type. Valid values are 01, 02, 03 or 04 " }
    {:code 128002 :type "Hard" :msg "Invalid or missing forward agent company name. Valid length is 1 to 35 alphanumeric " }
    {:code 128003 :type "Hard" :msg "Invalid or missing forward agent tax id. Valid length is 1 to 15 alphanumeric " }
    {:code 128004 :type "Hard" :msg "Invalid or missing forward agent address line 1. Valid length is 1 to 35 alphanumeric " }
    {:code 128005 :type "Hard" :msg "Invalid forward agent address line 2. Valid length is 0 to 35 alphanumeric " }
    {:code 128006 :type "Hard" :msg "Invalid forward agent address line 3. Valid length is 0 to 35 alphanumeric " }
    {:code 128007 :type "Hard" :msg "Invalid or missing forward agent city. Valid length is 1 to 30 alphanumeric " }
    {:code 128008 :type "Hard" :msg "Invalid forward agent state province code. Valid length is 0 to 5 alphanumeric " }
    {:code 128009 :type "Hard" :msg "Invalid forward agent postal code. Valid length is 0 to 9 alphanumeric" }
    {:code 128010 :type "Hard" :msg "Invalid or missing forward agent country code" }
    {:code 128011 :type "Hard" :msg "Invalid or missing ultimate consignee company name. Valid length is 1 to 35 alphanumeric " }
    {:code 128012 :type "Hard" :msg "Invalid or missing ultimate consignee addressLine1. Valid length is 1 to 35 alphanumeric " }
    {:code 128013 :type "Hard" :msg "Invalid ultimate consignee address line 2. Valid length is 0 to 35 alphanumeric " }
    {:code 128014 :type "Hard" :msg "Invalid ultimate consignee address line 3. Valid length is 0 to 35 alphanumeric " }
    {:code 128015 :type "Hard" :msg "Invalid or missing ultimate consignee city. Valid length is 1 to 30 alphanumeric " }
    {:code 128016 :type "Hard" :msg "Invalid ultimate consignee state province code. Valid length is 0 to 5 alphanumeric " }
    {:code 128017 :type "Hard" :msg "Invalid ultimate consignee postal code. Valid length is 0 to 9 alphanumeric " }
    {:code 128018 :type "Hard" :msg "Invalid or missing ultimate consignee country code" }
    {:code 128019 :type "Hard" :msg "Invalid or missing intermediate consignee company name. Valid length is 1 to 35 alphanumeric " }
    {:code 128020 :type "Hard" :msg "Invalid or missing intermediate consignee address line 1. Valid length is 1 to 35 alphanumeric " }
    {:code 128021 :type "Hard" :msg "Invalid intermediate consignee address line 2. Valid length is 0 to 35 alphanumeric " }
    {:code 128022 :type "Hard" :msg "Invalid intermediate consignee address line 3. Valid length is 0 to 35 alphanumeric " }
    {:code 128023 :type "Hard" :msg "Invalid or missing intermediate consignee city. Valid length is 1 to 30 alphanumeric " }
    {:code 128024 :type "Hard" :msg "Invalid intermediate consignee state province code. Valid length is 0 to 5 alphanumeric " }
    {:code 128025 :type "Hard" :msg "Invalid intermediate consignee postal code. Valid length is 0 to 9 alphanumeric " }
    {:code 128026 :type "Hard" :msg "Invalid or missing intermediate consignee country code" }
    {:code 128027 :type "Hard" :msg "Invalid or missing producer option. Valid values are 01, 02, 03 or 04" }
    {:code 128028 :type "Hard" :msg "Invalid or missing producer company name. Valid length is 1 to 35 alphanumeric " }
    {:code 128029 :type "Hard" :msg "Invalid or missing producer address line 1. Valid length is 1 to 35 alphanumeric " }
    {:code 128030 :type "Hard" :msg "Invalid producer address line 2. Valid length is 0 to 35 alphanumeric" }
    {:code 128031 :type "Hard" :msg "Invalid producer address line 3. Valid length is 0 to 35 alphanumeric" }
    {:code 128032 :type "Hard" :msg "Invalid or missing producer city. Valid length is 1 to 30 alphanumeric" }
    {:code 128033 :type "Hard" :msg "Invalid producer state province code. Valid length is 0 to 5 alphanumeric " }
    {:code 128034 :type "Hard" :msg "Invalid producer postal code. Valid length is 0 to 9 alphanumeric" }
    {:code 128035 :type "Hard" :msg "Invalid or missing producer country code" }
    {:code 128036 :type "Hard" :msg "Invalid telephone number." }
    {:code 128030 :type "Hard" :msg "Invalid producer email address. Valid length is 0 to 50 alphanumeric" }
    {:code 128030 :type "Hard" :msg "Invalid producer tax id. Valid length is 0 to 15 alphanumeric" }
    {:code 128039 :type "Hard" :msg "Invalid number of products. Valid number of products are 1 to 50" }
    {:code 128041 :type "Hard" :msg "The form specified for the product must be one of the requested form." }
    {:code 128042 :type "Hard" :msg "If invoice is a requested international form, all specified products must belong to invoice " }
    {:code 128043 :type "Hard" :msg "Invalid or missing product unit number for product number {0}. Valid length is 1 to 7 numeric " }
    {:code 128044 :type "Hard" :msg "Invalid or missing Product/Unit/UnitOfMeasurement/Code for product number {0}. Valid length is 1 to 3 alphanumeric " }
    {:code 128045 :type "Hard" :msg "Product/Unit/UnitOfMeasurement/Description is required when Product/Unit/UnitOfMeasurement/Code is ""OTH"" for product  number {0}. Valid length is 1 to 3 alphanumeric " }
    {:code 128046 :type "Hard" :msg "Invalid or missing product unit value for product number {0}. Valid length is 1 to 12 numeric and it can hold up to 6 decimal places " }
    {:code 128047 :type "Hard" :msg "Invalid part number. Valid length is 0 to 10 alphanumeric" }
    {:code 128048 :type "Hard" :msg "Invalid commodity code for product number {0}. Valid length is 6 to 15 alphanumeric " }
    {:code 128049 :type "Hard" :msg "Invalid or missing product origin country code for product number {0}" }
    {:code 128050 :type "Hard" :msg "Invalid or missing net cost code for product number {0}. Valid values are NC or NO {0}. Valid format is yyyyMMdd" }
    {:code 128051 :type "Hard" :msg "Invalid or missing net cost date range begin date for product number" }
    {:code 128052 :type "Hard" :msg "Invalid or missing net cost date range end date for product number {0}. Valid format is yyyyMMdd " }
    {:code 128053 :type "Hard" :msg "Invalid or missing preference criteria for product number {0}. Valid values are A through F " }
    {:code 128054 :type "Hard" :msg "Invalid or missing producer info for product number {0}. Valid values are YES, NO[1], NO[2] or NO[3] " }
    {:code 128055 :type "Hard" :msg "Invalid or missing marks and numbers for product number {0}. Valid length is 1 to 35 alphanumeric " }
    {:code 128056 :type "Hard" :msg "Invalid or missing product weight unit of measurement code for product number {0}. Valid values are KGS or LBS " }
    {:code 128057 :type "Hard" :msg "Invalid or missing product weight for product number {0}. Valid length is 1 to 5 and it can hold up to 1 decimal places. " }
    {:code 128058 :type "Hard" :msg "Invalid product vehicle ID for product number {0}. Valid length is 1 to 25 alphanumeric " }
    {:code 128059 :type "Hard" :msg "Invalid or missing product schedule B number for product number {0}. Valid length is 10 alphanumeric " }
    {:code 128060 :type "Hard" :msg "Invalid product schedule B quantity for product number {0}. Valid length is 0 to 10 numeric " }
    {:code 128061 :type "Hard" :msg "Invalid or missing product schedule B unit of measurement code for product number {0}. Valid length is 1 to 3 alphanumeric " }
    {:code 128062 :type "Hard" :msg "Schedule B quantity is required when the schedule B unit of measurement code is not equal to X " }
    {:code 128063 :type "Hard" :msg "Invalid or missing export type for product number {0}. Valid values are D, F or M " }
    {:code 128064 :type "Hard" :msg "Invalid or missing SED total value for product number {0}. Valid length is 1 to 15 numeric and can hold up to 2 decimal places " }
    {:code 128065 :type "Hard" :msg "Invalid invoice number. Valid length is 0 to 35 alphanumeric" }
    {:code 128066 :type "Hard" :msg "Invalid or missing invoice date. Valid format is yyyyMMdd" }
    {:code 128067 :type "Hard" :msg "Invalid purchase order number. Valid length is 0 to 35 alphanumeric" }
    {:code 128068 :type "Hard" :msg "Invalid terms of shipment." }
    {:code 128069 :type "Hard" :msg "Invalid or missing reason for export. Valid length is 1 to 20 alphanumeric " }
    {:code 128070 :type "Hard" :msg "Invalid additional comments. Valid length is 0 to 150 alphanumeric" }
    {:code 128071 :type "Hard" :msg "Invalid declaration statement. Valid length is 0 to 250 alphanumeric" }
    {:code 128072 :type "Hard" :msg "Invalid discount monetary value. Valid length is 0 to 15 numeric and can hold up to 2 decimal places. Discount can not be greater than the  invoice line total. " }
    {:code 128073 :type "Hard" :msg "Invalid freight charges monetary value. Valid length is 0 to 15 numeric and can hold up to 2 decimal places. " }
    {:code 128074 :type "Hard" :msg "Invalid other charges monetary value. Valid length is 0 to 15 numeric and can hold up to 2 decimal places. " }
    {:code 128075 :type "Hard" :msg "Invalid or missing other charges description. Valid length is 1 to 10 alphanumeric " }
    {:code 128076 :type "Hard" :msg "Invalid or missing blanket period begin date. Valid format is yyyyMMdd " }
    {:code 128077 :type "Hard" :msg "Invalid or missing blanket period end date. Valid format is yyyyMMdd" }
    {:code 128078 :type "Hard" :msg "Invalid or missing export date. Valid format is yyyyMMdd" }
    {:code 128079 :type "Hard" :msg "Invalid or missing export carrier. Valid length is 1 to 35 alphanumeric" }
    {:code 128080 :type "Hard" :msg "Invalid carrier ID. Valid length is 0 to 2 alphanumeric" }
    {:code 128081 :type "Hard" :msg "Invalid or missing in bond code. Valid length is 2 alphanumeric" }
    {:code 128082 :type "Hard" :msg "Entry number is required when the in bond code is other than 70. Valid Length is 1 to 25 alpha numeric " }
    {:code 128083 :type "Hard" :msg "Invalid or missing point of origin. Valid length is 1 to 5 alphanumeric" }
    {:code 128084 :type "Hard" :msg "Invalid or missing mode of transport. Valid length is 1 to 35 alphanumeric " }
    {:code 128085 :type "Hard" :msg "Invalid port of export. Valid length is 0 to 35 alphanumeric" }
    {:code 128086 :type "Hard" :msg "Invalid port of unloading. Valid length is 0 to 35 alphanumeric" }
    {:code 128087 :type "Hard" :msg "Invalid loading pier. Valid length is 0 to 35 alphanumeric" }
    {:code 128088 :type "Hard" :msg "Invalid or missing parties to transaction. Valid values are R or N" }
    {:code 128089 :type "Hard" :msg "Invalid license number. Valid length is 0 to 35 alphanumeric" }
    {:code 128090 :type "Hard" :msg "Invalid or missing license date. Valid format is yyyyMMdd" }
    {:code 128091 :type "Hard" :msg "Invalid license exception code. Valid length is 0 to 4 alphanumeric" }
    {:code 128092 :type "Hard" :msg "Either license number or license exception code must be present for a SED form. " }
    {:code 128093 :type "Hard" :msg "Invalid ECCN number code. Valid length is 1 to 8 alphanumeric" }
    {:code 128094 :type "Hard" :msg "Sold to information is required when an invoice or NAFTA CO is requested. " }
    {:code 128095 :type "Hard" :msg "Invalid or missing sold to company name. Valid length is 1 to 35 alphanumeric " }
    {:code 128096 :type "Hard" :msg "Invalid or missing sold to address line 1. Valid length is 1 to 35 alphanumeric " }
    {:code 128097 :type "Hard" :msg "Invalid sold to address line 2. Valid length is 0 to 35 alphanumeric" }
    {:code 128098 :type "Hard" :msg "Invalid sold to address line 3. Valid length is 0 to 35 alphanumeric" }
    {:code 128099 :type "Hard" :msg "Invalid or missing sold to city. Valid length is 1 to 30 alphanumeric" }
    {:code 128100 :type "Hard" :msg "Invalid sold to state province code. Valid length is 0 to 5 alphanumeric" }
    {:code 128101 :type "Hard" :msg "Invalid sold to postal code. Valid length is 0 to 9 alphanumeric" }
    {:code 128102 :type "Hard" :msg "Invalid or missing sold to country code" }
    {:code 128103 :type "Hard" :msg "The selected international form is not valid for the shipment origin country " }
    {:code 128104 :type "Hard" :msg "The selected international form is not valid for the shipment origin and product origin combination. " }
    {:code 128105 :type "Hard" :msg "The selected international form is not valid for the shipment origin and destination countries " }
    {:code 128109 :type "Hard" :msg "Invalid or missing sold to attention name. Valid value is 1 to 35 alphanumeric " }
    {:code 128110 :type "Hard" :msg "Invalid sold to option. Valid values are 01 or 02" }
    {:code 128111 :type "Hard" :msg "Commodity code is required for NAFTA CO" }
    {:code 128112 :type "Hard" :msg "Invalid insurance monetary value. Valid length is 0 to 15 numeric and can hold up to 2 decimal places. " }
    {:code 128113 :type "Hard" :msg "Invalid product description for product number {0}. Product description should be present at least once. Valid range is 0 to 35  alphanumeric " }
    {:code 128114 :type "Hard" :msg "Invalid or missing currency code. Valid length is 3 alphanumeric." }
    {:code 128115 :type "Hard" :msg "Invalid or missing sold to phone number. Valid length is 1 to 15 alphanumeric. " }
    {:code 128116 :type "Hard" :msg "Invalid sold to tax identification number. Valid length is 1 to 15 alphanumeric. " }
    {:code 128117 :type "Hard" :msg "Invalid or missing Shipper tax identification." }
    {:code 128118 :type "Hard" :msg "Invalid license information. Either license number and license date or license exception code and ECCN number must be present. Both the  combinations can not be present together. " }
    {:code 128119 :type "Hard" :msg "Invalid or missing product NumberOfPackagesPerCommodity. Valid length is 1-3 Numerics. " }
    {:code 128120 :type "Hard" :msg "Origin country must be different than destination country." }
    {:code 128121 :type "Hard" :msg "Missing value of other unit of measurement." }
    {:code 128122 :type "Hard" :msg "Invalid net cost date range." }
    {:code 128123 :type "Hard" :msg "Invoice cannot be requested for non-document forward shipments." }
    {:code 128124 :type "Hard" :msg "Invalid blanket period." }
    {:code 128125 :type "Hard" :msg "Invalid or missing ShipFrom tax identification." }
    {:code 128126 :type "Hard" :msg "Invalid or missing SED filing option. SED filing option is required when SED form is requested. Valid values are 01 and 02. " }
    {:code 128201 :type "Hard" :msg "Invalid or missing international forms form type for shipment with return service. Valid value is 01 or 05. " }
    {:code 128202 :type "Hard" :msg "International forms can not be requested for letters or documents." }
    {:code 128203 :type "Hard" :msg "Duplicate contact information" }
    {:code 128204 :type "Hard" :msg "Invalid form group id name. valid range is 0 to 50 alphanumeric" }
    {:code 128205 :type "Hard" :msg "Duplicate International FormType Information. valid values are 01, 02, 03, 04 " }
    {:code 128210 :type "Hard" :msg "Invalid combination of international forms. Both partial and complete invoice form cannot be selected together. " }
    {:code 128211 :type "Hard" :msg "Invalid Email Address" }
    {:code 128212 :type "Hard" :msg "Invalid or missing ShipTo tax identification." }
    {:code 128213 :type "Hard" :msg "Invalid or missing producer attention name. Valid value is 1 to 35 alphanumeric " }
    {:code 128214 :type "Hard" :msg "Invalid telephone extension. Valid length is 1 to 4 alphanumeric." }
    {:code 128215 :type "Hard" :msg "Invalid telephone extension. Valid length is 1 to 4 alphanumeric." }
    {:code 128216 :type "Hard" :msg "Invalid or missing international forms form type for ExcludeFromForm. Valid values are 04. " }
    {:code 128217 :type "Hard" :msg "A NAFTA Certificate of Origin must have at least one commodity in a request. " }
    {:code 128218 :type "Hard" :msg "A Commercial Invoice must have at least one commodity in a request." }
    {:code 128219 :type "Hard" :msg "A Blanket Period can not exceed 365 days." }
    {:code 128220 :type "Hard" :msg "The Total Product units allocated to packages do not match." }
    {:code 128221 :type "Hard" :msg "Product not associated with a package." }
    {:code 128222 :type "Hard" :msg "Invalid number of products. The valid number of products is 1 to 1000." }
    {:code 128223 :type "Hard" :msg "Invalid or missing DocumentID." }
    {:code 128224 :type "Hard" :msg "Invalid or missing Product Currency Code." }
    {:code 128225 :type "Hard" :msg "Invalid or missing Invoice Line Total." }
    {:code 128226 :type "Hard" :msg "Exceeds maximum number of International Forms (13) allowed per Shipment. " }
    {:code 128227 :type "Hard" :msg "Invalid or missing Package Number (Product is selected to be placed in a Package that does not exist). " }
    {:code 128228 :type "Hard" :msg "Invalid or missing Product number." }
    {:code 128229 :type "Hard" :msg "Invalid or missing packing list info." }
    {:code 128230 :type "Hard" :msg "Invalid or missing package associated data." }
    {:code 128231 :type "Hard" :msg "Invalid Product Id." }
    {:code 128232 :type "Hard" :msg "Exceeds the maximum number of packages allowed for the packing list" }
    {:code 128233 :type "Hard" :msg "User generated forms size exceeds total allowable limit for shipment of 5MB. " }
    {:code 128234 :type "Hard" :msg "Invalid or missing Shipper Memo." }
    {:code 128235 :type "Hard" :msg "Invalid PackageInfo object." }
    {:code 128236 :type "Hard" :msg "Invalid or missing Tracking Number." }
    {:code 128237 :type "Hard" :msg "No form data found for given formsGroupID." }
    {:code 128238 :type "Hard" :msg "No PDF found for given documentId." }
    {:code 128239 :type "Hard" :msg "Exceeds maximum number of Document IDs (13) allowed per Shipment. " }
    {:code 128240 :type "Hard" :msg "Invalid or missing User Created Form Data." }
    {:code 128241 :type "Hard" :msg "A shipment can only contain one Commercial Invoice." }
    {:code 129001 :type "Warning" :msg "Additional Handling has automatically been set  on Package (index of the package). " }
    {:code 129002 :type "Warning" :msg "An Extended Area Surcharge of (monetary value) has been  added to the service cost. " }
    {:code 129003 :type "Warning" :msg "{Rating error description}" }
    {:code 129004 :type "Warning" :msg "{Rating error description}" }
    {:code 129005 :type "Warning" :msg "{Rating error description}" }
    {:code 129006 :type "Warning" :msg "{Rating error description}" }
    {:code 129017 :type "Warning" :msg "{Rating error description} ." }
    {:code 129018 :type "Warning" :msg "{Rating error description} ." }
    {:code 129019 :type "Hard" :msg "{Rating error description}" }
    {:code 129021 :type "Hard" :msg "Both Security code and Billing address are required if credit card information is provided. If security  code or billing address is provided both of them should be  provided " }
    {:code 129022 :type "Hard" :msg "This credit card has been locked due to multiple unsuccessful validation attempts. Please use another  credit card to continue or try again later. " }
    {:code 129023 :type "Hard" :msg "Line Origin Country is missing or invalid" }
    {:code 129024 :type "Hard" :msg "Invoice Line Total is missing or invalid" }
    {:code 129025 :type "Hard" :msg "Invoice subtotal is missing or invalid" }
    {:code 129026 :type "Hard" :msg "Reason for export is missing or invalid" }
    {:code 129027 :type "Hard" :msg "Invoice line number is missing or invalid" }
    {:code 129028 :type "Hard" :msg "Line Unit Amount Price is missing or invalid" }
    {:code 129029 :type "Hard" :msg "Commodity quantity is missing or invalid" }
    {:code 129030 :type "Hard" :msg "Commodity unit of measure is missing or invalid" }
    {:code 129031 :type "Hard" :msg "Merchandise description1 is missing or invalid" }
    {:code 129032 :type "Hard" :msg "Merchandise description2 is missing or invalid" }
    {:code 129033 :type "Hard" :msg "Merchandise description3 is missing or invalid" }
    {:code 129034 :type "Hard" :msg "Total Amount is missing or invalid" }
    {:code 129035 :type "Hard" :msg "PO number missing or invalid" }
    {:code 129036 :type "Hard" :msg "Terms of shipment is missing or invalid" }
    {:code 129037 :type "Hard" :msg "Freight Charges is missing or invalid" }
    {:code 129038 :type "Hard" :msg "Insurance is missing or invalid" }
    {:code 129039 :type "Hard" :msg "Discount is missing or invalid" }
    {:code 129040 :type "Hard" :msg "Other charges is missing or invalid" }
    {:code 129041 :type "Hard" :msg "Commodity code is missing or invalid" }
    {:code 129042 :type "Hard" :msg "Commodity part number is missing or invalid" }
    {:code 129043 :type "Hard" :msg "Invoice number is missing or invalid" }
    {:code 129044 :type "Hard" :msg "Commodity currency code is missing or invalid" }
    {:code 129045 :type "Hard" :msg "Commodity comments is missing or invalid" }
    {:code 129046 :type "Hard" :msg "Missing credit card billing address line 1" }
    {:code 129048 :type "Hard" :msg "Missing credit card billing address city" }
    {:code 129049 :type "Hard" :msg "Missing credit card billing address state province code" }
    {:code 129050 :type "Hard" :msg "Missing credit card billing address postal code" }
    {:code 129051 :type "Hard" :msg "Missing credit card billing address country code" }
    {:code 129057 :type "Hard" :msg "Location ID may not exceed the length of 10" }
    {:code 129058 :type "Hard" :msg "Shipper Number {0} does not support pallet Contract Service." }
    {:code 129059 :type "Hard" :msg "Shipper Number {0} does not support Third Country Contract Service. " }
    {:code 129070 :type "Warning" :msg "{Rating error description}" }
    {:code 129076 :type "Hard" :msg "Pallets may not be included with other package types." }
    {:code 129077 :type "Warning" :msg "A dimensional surcharge has been added to pallet {0}." }
    {:code 129078 :type "Hard" :msg "The maximum dimensions for a pallet are 200 cm by 160 cm by 120 cm. " }
    {:code 129079 :type "Hard" :msg "The requested service is not valid with pallets with a weight greater than 1000kg. Please select  Dedicated Package Courier Same Day Service. " }
    {:code 129080 :type "Hard" :msg "Pallet {0} exceeds maximum width." }
    {:code 129081 :type "Hard" :msg "Pallet {0} exceeds maximum height." }
    {:code 129082 :type "Hard" :msg "Pallet {0} exceeds maximum length." }
    {:code 129083 :type "Hard" :msg "Pallet {0} exceeds maximum weight." }
    {:code 129085 :type "Hard" :msg "Cannot combine declared value and shipper declared value in a shipment. " }
    {:code 129086 :type "Hard" :msg "GNIFC is not valid for Letters/Envelopes and documents with no commercial value. " }
    {:code 129087 :type "Hard" :msg "Invalid Shipment Delivery Confirmation Type" }
    {:code 129088 :type "Hard" :msg "Package Delivery Confirmation DCISNumber may not exceed a length of 11. " }
    {:code 129090 :type "Hard" :msg "Only future date pickup is available for pickup requests for the requested origin and destination and  selected service " }
    {:code 129171 :type "Hard" :msg "For Import Control movements, BILL RECEIVER is not a valid payer. " }
    {:code 129172 :type "Hard" :msg "Credit card is not a valid payment method for Import Control" }
    {:code 129173 :type "Hard" :msg "The following SED filing options are not valid with Import Control shipments: AES Filing Option 2, AES  Filing Option 4 and UPS Prepare SED " }
    {:code 129174 :type "Hard" :msg "A package with a Import Control accessory must have a Merchandise Description. " }
    {:code 129175 :type "Hard" :msg "Invalid Import Control shipment." }
    {:code 129176 :type "Hard" :msg "Either LabelDelivery Email or LabelLinksIndicator must be provided for this shipment. " }
    {:code 129177 :type "Hard" :msg "LabelMethod is not valid for this shipment." }
    {:code 129180 :type "Hard" :msg "Missing or Invalid LabelMethod type for ImportControl shipment." }
    {:code 129200 :type "Hard" :msg "Unable to retrieve Mailer ID from UPS Internet Membership Services system. " }
    {:code 129201 :type "Hard" :msg "Account is not authorized for UPS Returns Flexible Access service. " }
    {:code 129202 :type "Hard" :msg "Account is only authorized for UPS Returns Flexible Access service in test mode. " }
    {:code 129203 :type "Hard" :msg "Account is not authorized for UPS Exchange service" }
    {:code 129204 :type "Hard" :msg "Account is not authorized for UPS Pack and Collect service" }
    {:code 126086 :type "Hard" :msg "PreAlertNotification Phone Number is missing" }
    {:code 126087 :type "Hard" :msg "PreAlert Notification Phone Number may not exceed length of 15" }
    {:code 126088 :type "Hard" :msg "PreAlertNotification Dialect length must be less than or equal to 2" }
    {:code 126089 :type "Hard" :msg "PreAlertNotification Language-Dialect Pair not valid" }
    {:code 126090 :type "Hard" :msg "Pre-alert notification is valid only with UPS Returns Exchange and UPS Returns Pack and Collect shipments " }
    {:code 126091 :type "Hard" :msg "Notification Language is missing" }
    {:code 126092 :type "Hard" :msg "Notification Dialect is missing" }
    {:code 126093 :type "Hard" :msg "Notification Language must be less than or equal to 3" }
    {:code 126094 :type "Hard" :msg "Missing/Invalid Label Instruction Code" }
    {:code 126095 :type "Hard" :msg "Invalid set of Label Instruction Codes" }
    {:code 126096 :type "Hard" :msg "At least one rate information either negotiated rates or rate chart indicator is required. " }
    {:code 126097 :type "Hard" :msg "LabelSpecification/Instruction is not allowed with the shipment" }
    {:code 124998 :type "Hard" :msg "ReceiptSpecification applies only for PRL or Exchange Return Receipt. " }
    {:code 124999 :type "Hard" :msg "Invalid ImageFormat code" }
    {:code 126005 :type "Hard" :msg "DryIce DryIceWeight Weight is absent or doesn't meet specification." }
    {:code 126007 :type "Hard" :msg "DryIce DryIceWeight UnitOfMeasurment Code has invalid value." }
    {:code 126044 :type "Hard" :msg "DryIce Regulation is required or has invalid value." }
    {:code 126000 :type "Hard" :msg "Shipper is not eligible to ship Dry Ice." }
    {:code 126008 :type "Hard" :msg "All packages with DryIce RegulationSet must contain the same Regulation Set. " }
    {:code 126009 :type "Hard" :msg "DryIce RegulationSet for non-US PR origins destinations should be IATA. " }
    {:code 126010 :type "Hard" :msg "DryIce DryIceWeight Weight cannot be larger than package weight." }
    {:code 126077 :type "Hard" :msg "Number of DryIce items per Package is greater than 1." }
    {:code 126078 :type "Hard" :msg "DryIce item cannot exist on the same package as another HazMat." }
    {:code 128216 :type "Hard" :msg "Invalid or missing international forms form type for ExcludeFromForm. Valid values are 04. " }
    {:code 128217 :type "Hard" :msg "A NAFTA Certificate of Origin must have at least one commodity in the request. " }
    {:code 128218 :type "Hard" :msg "A Commercial Invoice must have at least one commodity in the request. " }
    {:code 126098 :type "Warning" :msg "Account authentication is required to be completed to get negotiated  rates. " }
    {:code 126099 :type "Warning" :msg "Credit Card is the only valid payment method for this type of account." }
    {:code 120910 :type "Warning" :msg " TPFCNegotiatedRatesIndicator is applicable only for Third  party/Freight Collect shipments. " }
    {:code 120911 :type "Warning" :msg " Shipper not authorized to request for the Third Party/Freight Collect  negotiated rates for this shipment. " }
    {:code 126004 :type "Hard" :msg "PackageServiceOption HazMat Quantity is absent or doesn't meet specification. " }
    {:code 126006 :type "Hard" :msg "PackageServiceOption HazMat TransportationMode has invalid value." }
    {:code 126030 :type "Hard" :msg "PackageServiceOption HazMat Regulation required or has invalid value. " }
    {:code 126001 :type "Hard" :msg "Shipper is not eligible to ship Hazardous Material / International Dangerous Goods. " }
    {:code 126004 :type "Hard" :msg "HazMat Quantity is absent or doesn't meet specification." }
    {:code 126006 :type "Hard" :msg "HazMat TransportationMode has invalid value." }
    {:code 126011 :type "Hard" :msg "Package/HazMatPackageInformation required with either AllPackedInOneIndicator/OverPackedIndicator values. " }
    {:code 126032 :type "Hard" :msg "HazMat ClassDivisionNumber is required." }
    {:code 126033 :type "Hard" :msg "HazMat ClassDivisionNumber exceeds maximum length of 7 characters. " }
    {:code 126034 :type "Hard" :msg "HazMat ClassDivisionNumber violates datatype. Only ASCII allowed." }
    {:code 126035 :type "Hard" :msg "HazMat Quantity is required." }
    {:code 126036 :type "Hard" :msg "HazMat Quantity exceeds maximum value." }
    {:code 126038 :type "Hard" :msg "HazMat UOM not set or invalid." }
    {:code 126039 :type "Hard" :msg "HazMat UOM value exceeds maximum length of 10 characters." }
    {:code 126040 :type "Hard" :msg "HazMat UOM violates datatype only ASCII allowed." }
    {:code 126041 :type "Hard" :msg "HazMat ProperShippingName is required and not set." }
    {:code 126042 :type "Hard" :msg "HazMat ProperShippingName exceeds maximum length of 150 characters. " }
    {:code 126043 :type "Hard" :msg "HazMat ProperShippingName violates datatype only ASCII allowed." }
    {:code 126045 :type "Hard" :msg "HazMat ReferenceNumber exceeds maximum of 15 characters." }
    {:code 126046 :type "Hard" :msg "HazMat ReferenceNumber violates datatype only ASCII allowed." }
    {:code 126047 :type "Hard" :msg "HazMat ReportableQuantity exceeds maximum length of 2 characters." }
    {:code 126048 :type "Hard" :msg "HazMat ReportableQuantity violates datatype only ASCII allowed." }
    {:code 126051 :type "Hard" :msg "HazMat IDNumber Number exceeds maximum length of 6 characters." }
    {:code 126052 :type "Hard" :msg "HazMat IDNumber Number violates datatype only ASCII allowed." }
    {:code 126055 :type "Hard" :msg "HazMat PackagingGroupType exceeds maximum length of 5 characters. " }
    {:code 126056 :type "Hard" :msg "HazMat PackagingGroupType violates datatype only ASCII allowed." }
    {:code 126059 :type "Hard" :msg "HazMat PackagingInstructionCode is Required." }
    {:code 126060 :type "Hard" :msg "HazMat PackagingInstructionCode exceeds maximum length of 4 characters. " }
    {:code 126061 :type "Hard" :msg "HazMat PackagingInstructionCode violates datatype only ASCII allowed. " }
    {:code 126062 :type "Hard" :msg "HazMat TransportationMode exceeds maximum length of 30 characters. " }
    {:code 126063 :type "Hard" :msg "HazMat TransportationMode violates datatype only ASCII allowed." }
    {:code 126064 :type "Hard" :msg "HazMat EmergencyPhone required since Regulation Set is TDG" }
    {:code 126065 :type "Hard" :msg "HazMat EmergencyPhone Number exceeds maximum length of 25 characters. " }
    {:code 126066 :type "Hard" :msg "HazMat EmergencyPhone Number violates datatype only ASCII allowed. " }
    {:code 126069 :type "Hard" :msg "HazMat AdditionalDescription Info exceeds maximum length of 255 characters. " }
    {:code 126070 :type "Hard" :msg "HazMat AdditionalDescription Info violates datatype only ASCII allowed. " }
    {:code 126071 :type "Hard" :msg "HazMat PackagingType is required." }
    {:code 126072 :type "Hard" :msg "HazMat PackagingType exceeds maximum length of 255 characters." }
    {:code 126073 :type "Hard" :msg "HazMat PackagingType violates datatype only ASCII allowed." }
    {:code 126076 :type "Hard" :msg "Number of HazMat entities per package cannot be greater than 3." }
    {:code 126079 :type "Hard" :msg "HazMat EmergencyContact Information is required for this shipment" }
    {:code 126080 :type "Hard" :msg "HazMat EmergencyContact Information exceeds maximum length of 35 characters. " }
    {:code 126081 :type "Hard" :msg "HazMat EmergencyContact Information violates datatype only ASCII allowed. " }
    {:code 126082 :type "Hard" :msg "Package HazMatPackageInformation AllPackedInOneIndicator is optional. " }
    {:code 126083 :type "Hard" :msg "Package HazMatPackageInformation OverpackedIndicator is optional." }
    {:code 126084 :type "Hard" :msg "Package HazMatPackageInformation QValue must exist with RegulationSet IATA when AllPackedInOneIndicator present. " }
    {:code 126085 :type "Hard" :msg "HazMatPackageInformation QValue must be decimal value with one significant digit. " }
    {:code 120444 :type "Hard" :msg "Invalid Bill-To type" }
    {:code 120026 :type "Hard" :msg "Lift Gate Accessorial for Pick Up and Delivery are not allowed when both Hold For Pick Up and Drop off At UPS Facility is requested. " }
    {:code 120027 :type "Hard" :msg "Lift Gate for Pick Up accessorial is not allowed with Drop Off At UPS Facility accessorial. " }
    {:code 120028 :type "Hard" :msg "Lift Gate for Delivery accessorial is not allowed with Hold For Pick Up accessorial. " }
    {:code 120029 :type "Hard" :msg "World Wide Express Freight Shipment Service Option is not available for Return Shipments. " }
    {:code 120030 :type "Hard" :msg "Missing or Invalid Total Number of Pieces in all Pallets in a Shipment." }
    {:code 120031 :type "Hard" :msg "Exceeds Total Number of allowed pieces per World Wide Express Shipment. ({2}) cm for the selected origin." }
    {:code 120032 :type "Hard" :msg "The maximum dimensions for a pallet are ({0}) cm by ({1}) cm by" }
    {:code 120033 :type "Hard" :msg "The maximum dimensions for a pallet are ({0}) in by ({1}) in by ({2}) in for the selected origin. ({2}) cm for the selected destination." }
    {:code 120034 :type "Hard" :msg "The maximum dimensions for a pallet are ({0}) cm by ({1}) cm by" }
    {:code 120035 :type "Hard" :msg "The maximum dimensions for a pallet are ({0}) in by ({1}) in by ({2}) in for the selected destination. " }
    {:code 120036 :type "Warning" :msg "Please contact customer service center for capacity authorization." }
    {:code 120037 :type "Hard" :msg "Shipment total weight has exceeded the maximum shipment weight limit of 99,999.9 kgs or 99,999.9 pounds. " }
    {:code 120041 :type "Hard" :msg "The maximum per pallet weight for the selected service from the selected origin is ({0}) pounds. " }
    {:code 120042 :type "Hard" :msg "The maximum per pallet weight for the selected service from the selected origin is ({0}) kgs. " }
    {:code 120043 :type "Hard" :msg "The maximum per pallet weight for the selected service to the selected destination is ({0}) pounds. " }
    {:code 120044 :type "Hard" :msg "The maximum per pallet weight for the selected service to the selected destination is ({0}) kgs. " }
    {:code 128255 :type "Hard" :msg "Air Freight Packing List and Small Package Packing List are not allowed together in same shipment. " }
    {:code 120062 :type "Hard" :msg "The requested billing option is unavailable with the selected packaging. " }
    {:code 120681 :type "Hard" :msg "Invalid pallet dimensions, the length must be longer than the width." }
    {:code 120683 :type "Hard" :msg "The maximum Dry Ice for a pallet/shipment is 200 kg or 440 lbs." }
    {:code 120684 :type "Hard" :msg "Itemized Charges are not valid for this service." }
    {:code 120695 :type "Hard" :msg "Missing or Invalid Packaging Type Quantity." }
    {:code 121016 :type "Hard" :msg "The Saturday Delivery option cannot be combined with the UPS Import Control option. " }
    {:code 120059 :type "Warning" :msg "A CN22 form is required for this shipment." }
    {:code 120066 :type "Hard" :msg "Endorsements are not valid for the selected origin, service, and package combination. " }
    {:code 120067 :type "Hard" :msg "An endorsement is required for the selected origin, service, and package combination. " }
    {:code 120069 :type "Hard" :msg "The Mailer ID is required for domestic non-flat Mail Innovations shipments. " }
    {:code 120070 :type "Hard" :msg "The Mail Innovations account number is required on Mail Innovations shipments. " }
    {:code 120071 :type "Hard" :msg "Confirmation Type for Mail Innovations USPS Delivery Confirmation is invalid. " }
    {:code 120072 :type "Hard" :msg "Invalid Number of Packages. Mail Innovations shipments are limited to 1 package. " }
    {:code 120073 :type "Hard" :msg "Missing or Invalid Mail Innovations Package Id." }
    {:code 120074 :type "Hard" :msg "Missing or Invalid Mail Innovations Cost Center." }
    {:code 120075 :type "Hard" :msg "Shipper is not authorized for Mail Innovations." }
    {:code 120076 :type "Hard" :msg "Missing or invalid USPS Endorsement." }
    {:code 120077 :type "Hard" :msg "CN22 form is required for combined MI package and CN22 label." }
    {:code 120078 :type "Hard" :msg "Too many characters provided in Mail Innovations Package ID." }
    {:code 120079 :type "Hard" :msg "Too many characters provided in Mail Innovations Cost Center." }
    {:code 120080 :type "Hard" :msg "The maximum number of goods printed on CN22 form cannot be more than 1 for combined MI package and CN22 label. " }
    {:code 120081 :type "Hard" :msg "Shipment Reference Number is not supported for Mail Innovation shipments. " }
    {:code 120082 :type "Hard" :msg "Package Reference Number is not supported for Mail Innovation shipments. " }
    {:code 128242 :type "Hard" :msg "Label Size is not provided in the Cn22 form." }
    {:code 128243 :type "Hard" :msg "Missing the No of Prints per Page value" }
    {:code 128244 :type "Hard" :msg "Missing the Label Print Type" }
    {:code 128245 :type "Hard" :msg "Missing the CN22 form Type" }
    {:code 128246 :type "Hard" :msg "Missing the Cn22 Other Description in the CN22 form" }
    {:code 128247 :type "Hard" :msg "The CN22 Content is not provided in the CN22 form" }
    {:code 128248 :type "Hard" :msg "The Total number of items associated with the content is invalid" }
    {:code 128249 :type "Hard" :msg "The description of the content provided in the CN22 form is invalid" }
    {:code 128250 :type "Hard" :msg "Total Weight of the content provided in the CN22 form is invalid" }
    {:code 128251 :type "Hard" :msg "Total value of the items associated with the content in the CN22 form is invalid " }
    {:code 128252 :type "Hard" :msg "The currency code provided in the CN22 form is invalid" }
    {:code 128253 :type "Hard" :msg "The Weight format provided in the CN22 form is invalid." }
    {:code 128254 :type "Hard" :msg "The CN22 form cannot be combined with any other form" }
    {:code 128256 :type "Hard" :msg "Invalid Fold Here Text length." }
    {:code 128257 :type "Hard" :msg "The country of origin associated with the content in the CN22 form is invalid. " }
    {:code 128258 :type "Hard" :msg "The tariff number associated with the content in the CN22 form is invalid. " }
    {:code 120687 :type "Hard" :msg "Missing or Invalid Page Size for UPS Premium Care Form." }
    {:code 120688 :type "Hard" :msg "Missing or Invalid Print Type for UPS Premium Care Form." }
    {:code 120689 :type "Hard" :msg "UPS Premium Care Form is required if UPS Premium Care Accessorial is requested. " }
    {:code 120690 :type "Hard" :msg "Missing or Invalid Number of Copies for UPS Premium Care Form." }
    {:code 120691 :type "Hard" :msg "Missing or Invalid Languages for UPS Premium Care Form." }
    {:code 120692 :type "Hard" :msg "Two Languages are required for UPS Premium Care Form." }
    {:code 10001 :type "Hard" :msg "The XML document is not well formed" }
    {:code 10002 :type "Hard" :msg "The XML document is well formed but the document is not valid" }
    {:code 190001 :type "Transient" :msg "Void not available at this time" }
    {:code 190002 :type "Transient" :msg "Invalid/Missing registration ID" }
    {:code 190100 :type "Hard" :msg "Invalid ShipmentIdentificationNumber." }
    {:code 190101 :type "Hard" :msg "Time for voiding has expired." }
    {:code 190102 :type "Hard" :msg "No shipment found within the allowed void period" }
    {:code 190103 :type "Hard" :msg "The Pickup Request associated with this shipment has already been completed " }
    {:code 190104 :type "Hard" :msg "Return shipments cannot be voided." }
    {:code 190105 :type "Hard" :msg "Pickup Cancellation is not Available on this Pickup Request" }
    {:code 190106 :type "Hard" :msg "Pickup Cancellation is not Available on this Pickup Request" }
    {:code 190107 :type "Hard" :msg "The Pickup Request associated with this shipment has previously been canceled " }
    {:code 190108 :type "Hard" :msg "The Pickup Request associated with this shipment cannot be canceled " }
    {:code 190109 :type "Hard" :msg "Invalid TrackingNumber" }
    {:code 190110 :type "Hard" :msg "Invalid Void Package {0}" }
    {:code 190111 :type "Hard" :msg "Package Void Limit Exceeded" }
    {:code 190112 :type "Hard" :msg "Return Service Shipments cannot be Voided at the Package Level " }
    {:code 190113 :type "Hard" :msg "International Shipments cannot be Voided at the Package Level" }
    {:code 190114 :type "Hard" :msg "Invalid Return Service Void Date" }
    {:code 190115 :type "Hard" :msg "Invalid Void Date" }
    {:code 190116 :type "Hard" :msg "A Label cannot be generated for a voided Return Service Shipment " }
    {:code 190117 :type "Hard" :msg "The Shipment has already been voided" }
    {:code 190118 :type "Hard" :msg "Same Day Service Void Not Allowed." }
    {:code 190119 :type "Warning" :msg "The requested Void was successful, but the associated  Pickup was not Cancelled " }
    {:code 190121 :type "Hard" :msg "None of the submitted packages were voided" }
    {:code 190122 :type "Hard" :msg "Package(s) {0} selected to void is(are) invalid" }
    {:code 190124 :type "Hard" :msg "Import Control Shipment cannot be voided later than 3 am of the day after the shipment uploaded. " }
    {:code 10001 :type "Hard" :msg "The XML document is not well formed" }
    {:code 10002 :type "Hard" :msg "The XML document is well formed but the document is not valid" }
    {:code 20011 :type "Hard" :msg "The Integration Indicator is no longer supported" }
    {:code 300000 :type "Transient" :msg "Label Recovery system is currently unavailable" }
    {:code 300001 :type "Hard" :msg "Multiple shipments found for the request" }
    {:code 300002 :type "Hard" :msg "Label is unavailable -- the package has been sent to the destination address " }
    {:code 300003 :type "Hard" :msg "The requested shipment contains more than 100 packages." }
    {:code 300004 :type "Hard" :msg "Invalid shipments return service for label recovery" }
    {:code 300005 :type "Hard" :msg "Either tracking number or combination of reference plus shipper number required " }
    {:code 300006 :type "Hard" :msg "Label is unavailable -- the label is expired" }
    {:code 300007 :type "Hard" :msg "The shipment for the requested tracking number or the combination of reference number plus shipper  number could not be found. Please check the  submitted data or wait until the shipment is processed. " }
    {:code 300008 :type "Hard" :msg "Invalid Translate/code" }
    {:code 300009 :type "Hard" :msg "Invalid Inquire Method" }
    {:code 300013 :type "Hard" :msg "Invalid Label Print Method" }
    {:code 300014 :type "Hard" :msg "Invalid Tracking Number" }
    {:code 300022 :type "Warning" :msg "Email subsystem is currently unavailable" }
    {:code 300023 :type "Hard" :msg "Invalid LabelSpecification/HTTPUserAgent" }
    {:code 300026 :type "Warning" :msg "The Label Link Email has been sent to the original label  delivery email address when the shipment was placed instead of  the requested email address  Error  Code Severity Description " }
    {:code 300030 :type "Hard" :msg "Invalid Reference Number" }
    {:code 300031 :type "Hard" :msg "Invalid Shipper Number" }
    {:code 300032 :type "Hard" :msg "Label is unavailable -- the shipment has no {0}" }
    {:code 300033 :type "Hard" :msg "The shipment for which you are trying to recover a label or Receipt has been voided. Please contact the  vendor for further information " }
    {:code 300034 :type "Hard" :msg "Label is unavailable -- the shipment has not been processed." }
    {:code 300035 :type "Hard" :msg "Too many shipments found with the requested Shipper Number and Reference Number. Please re-  submit the request with the Tracking Number.  Appendix E - Common error codes for all webservices and API  Common Errors can apply to all web services;  Code Severity Description " }
    {:code 10001 :type "Hard" :msg "The XML document is not well formed" }
    {:code 10002 :type "Hard" :msg "The XML document is well formed but the document is not valid" }
    {:code 10003 :type "Hard" :msg "The XML document is either empty or null" }
    {:code 10006 :type "Hard" :msg "Although the document is well formed and valid, the element content contains values which do not conform to the rules and constraints contained  in this specification " }
    {:code 10013 :type "Hard" :msg "The message is too large to be processed by the Application" }
    {:code 20001 :type "Transient" :msg "General process failure" }
    {:code 20002 :type "Hard" :msg "The specified service name, {0}, and version number, {1}, combination is invalid " }
    {:code 20003 :type "Hard" :msg "Please check the server environment for the proper J2EE ws apis" }
    {:code 20006 :type "Hard" :msg "Invalid request action" }
    {:code 20012 :type "Hard" :msg "The Client Information exceeds its Maximum Limit of {0}" }
    {:code 250000 :type "Hard" :msg "No XML declaration in the XML document" }
    {:code 250001 :type "Hard" :msg "Invalid Access License for the tool. Please re-license." }
    {:code 250002 :type "Hard" :msg "Invalid UserId/Password" }
    {:code 250003 :type "Hard" :msg "Invalid Access License number" }
    {:code 250004 :type "Hard" :msg "Incorrect UserId or Password" }
    {:code 250005 :type "Hard" :msg "No Access and Authentication Credentials provided" }
    {:code 250006 :type "Hard" :msg "The maximum number of user access attempts was exceeded" }
    {:code 250007 :type "Hard" :msg "The UserId is currently locked out; please try again in 24 hours." }
    {:code 250009 :type "Hard" :msg "License Number not found in the UPS database" }
    ) )
