(ns sistemi.site.order.payment-htm
  (:require [ring.util.response :refer [response]]
            [www.form :as f]
            [sistemi.form :as sf]
            [sistemi.layout :as layout]
            [sistemi.site.order.wizard :as wiz]
            [app.config :as cf]
            ))

(def names {})


(def head
  (seq [[:script {:type "text/javascript" :src "https://js.stripe.com/v2/"}]
        [:script {:type "text/javascript" :src "/js/jquery.payment.js"}]
        [:style {:type "text/css" :media "screen"}
"
   input.invalid {
      border: 1px solid red;
    }
"
         ]

        [:script {:type "text/javascript"}
         (str "Stripe.setPublishableKey('" (cf/conf :stripe :publishable-key) "');")

;; fn to log the stripe response to our server
;; TODO: use standard event function
"
var logEvent = function(data) {
  var xhr = new XMLHttpRequest();
  xhr.open('POST', '/event');
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.send(JSON.stringify(data));
}
"

"
var stripeResponseHandler = function(status, response) {
  var $form = $('#payment-form');

  console.log('stripe response:');
  console.log(status);
  console.log(response);

  if (response.error) {
    console.log('error');

    // Show the errors on the form
    $form.find('.payment-errors').text(response.error.message);
    $form.find('button').prop('disabled', false);

    // Log the error.
    response.status = status;
    logEvent(response);
  } else {
    console.log('success');
    // token contains id, last4, and card type
    var token = response.id;
    // Insert the token into the form so it gets submitted to the server
    $form.append($('<input type=\"hidden\" name=\"stripeToken\" />').val(token));
    // and submit
    $form.get(0).submit();

    // TODO: leave it disabled?   
    $form.find('button').prop('disabled', false);
  }

};"

"
var validateForm = function() {
  $('input').removeClass('invalid');

  var cardType = $.payment.cardType($('.cc-number').val());
  console.log('CARD TYPE', cardType);

  var validNumber = $.payment.validateCardNumber($('.cc-number').val());
  var validExp = $.payment.validateCardExpiry($('.cc-exp').payment('cardExpiryVal'));
  var validCVC = $.payment.validateCardCVC($('.cc-cvc').val(), cardType);

//  $('.cc-number').toggleClass('invalid', !validNumber);
  $('.cc-exp').toggleClass('invalid', !validExp);
  $('.cc-cvc').toggleClass('invalid', !validCVC);

  return validNumber && validExp && validCVC;
};

var decorateCCNumber = function() {
  var validNumber = $.payment.validateCardNumber($('.cc-number').val());
  console.log('decorateCCNumber: valid', validNumber);
  $('.cc-number').toggleClass('invalid', !validNumber);
};

jQuery(function($) {
  $('[data-numeric]').payment('restrictNumeric');
  $('.cc-number').payment('formatCardNumber');
  //$('.cc-number').on('keypress', setCardType);
  $('.cc-number').on('keypress', decorateCCNumber);

  $('.cc-exp').payment('formatCardExpiry');
  $('.cc-cvc').payment('formatCardCVC');

  $('#payment-form').submit(function(event) {
    if ( !validateForm() ) {
      return false;
    }

    console.log('submitting form');

    // Parse the expiration month and year from the expiration input.
    var exp = $('input.cc-exp').payment('cardExpiryVal');
    $('.cc-exp-month').val(exp.month);
    $('.cc-exp-year').val(exp.year);

    var $form = $(this);

    // Disable the submit button to prevent repeated clicks.
    $form.find('button').prop('disabled', true);

    Stripe.card.createToken($form, stripeResponseHandler);

    // Prevent the form from submitting with the default action.
    return false;
  });
});"
         ]
        ]))

(defn body
  []
  [:div
   (wiz/checkout-wizard :payment)

   ;; TODO: Add name and address
   ;; TODO: Use bootstrap.
   ;; TODO: ? Are labels needed?
   ;; TODO: Translate error messages from stripe.
   ;; TODO: Add image for credit card type.
   ;; TODO: Print green checkmark next to vaidated (non-empty) fields.
   ;;    fa-check
   ;; TODO: Disable submit button after form submission.
   ;; TODO: ? Faux progress indicator while waiting?

   [:form#payment-form {:action "payment" :method "POST" :novalidate "true" :autocomplete "on"}
    [:span.payment-errors]

    [:div.form-row 
     [:input.cc-number.validation
      {:type "text" :size "20" :data-stripe "number"
       :pattern_ "[\\d ]*" :x-autocompletetype "cc-number" :placeholder "Card number" :required "true"
       :style {:width "150px"}
       }]
     [:span.validation " "]
     ]

    [:div.form-row
     [:input.cc-exp {:type "text" :data-stripe "cc-exp"
                     :pattern "\\d{2} / \\d{2}(\\d{2})?" :x-autocompletetype "cc-exp" :placeholder "Expires MM/YY" :required "true" :maxlength "9"}]
     [:input.cc-exp-month {:type "hidden" :data-stripe "exp-month"}]
     [:input.cc-exp-year {:type "hidden" :data-stripe "exp-year"}]]

    [:div.form-row
     [:input.cc-cvc {:type "text" :data-stripe "cvc"
                     :pattern "\\d*" :x-autocompletetype "cc-csc" :placeholder "Security code" :required "true" :autocomplete "off" :maxlength "4"}]]

    [:button {:type "submit"} "Submit Payment"]
    ]


   ;; [:form.form-horizontal {:method "post" :action "payment"}
   ;;  [:fieldset
   ;;   [:h2 "Credit Card"]

   ;;   [:div.control-group
   ;;    [:label.control-label {:for "first-name"} "First Name"]
   ;;    [:div.controls {:style {:margin-left "80px"}}
   ;;     (f/text :first-name {:style "width: 145px" :tabindex 1})]]

   ;;   [:div.control-group
   ;;    [:label.control-label {:for "last-name"} "Last Name"]
   ;;    [:div.controls {:style {:margin-left "80px"}}
   ;;     (f/text :last-name {:style "width: 145px" :tabindex 1})]]

   ;;   [:div.control-group
   ;;    [:label.control-label {:for "cc-number"} "Card Number"]
   ;;    [:div.controls {:style {:margin-left "80px"}}
   ;;     (f/text :cc-number {:style "width: 145px" :tabindex 1})]]

   ;;   [:div.control-group
   ;;    [:label.control-label {:for "cc-number"} "Card Number"]
   ;;    [:div.controls {:style {:margin-left "80px"}}
   ;;     (f/text :cc-number {:style "width: 145px" :tabindex 1})]]

   ;;   [:div.control-group
   ;;    [:label.control-label {:for "cc-month"} "Month"]
   ;;    [:div.controls {:style {:margin-left "80px"}}
   ;;     (f/text :cc-month {:style "width: 145px" :tabindex 1})]]

   ;;   [:div.control-group
   ;;    [:label.control-label {:for "cc-year"} "Year"]
   ;;    [:div.controls {:style {:margin-left "80px"}}
   ;;     (f/text :cc-year {:style "width: 145px" :tabindex 1})]]

   ;;   [:div.control-group
   ;;    [:label.control-label {:for "cc-cvc"} "Cvc"]
   ;;    [:div.controls {:style {:margin-left "80px"}}
   ;;     (f/text :cc-cvc {:style "width: 145px" :tabindex 1})]]

     ;; [:h2 "Billing Address"]

     ;; [:div.control-group
     ;;  [:label.control-label {:for "address1"} "Address 1"]
     ;;  [:div.controls {:style {:margin-left "80px"}}
     ;;   (f/text :address1 {:style "width: 145px" :tabindex 1})]]

     ;; [:div.control-group
     ;;  [:label.control-label {:for "address2"} "Address 2"]
     ;;  [:div.controls {:style {:margin-left "80px"}}
     ;;   (f/text :address2 {:style "width: 145px" :tabindex 1})]]

     ;; [:div.control-group
     ;;  [:label.control-label {:for "city"} "City"]
     ;;  [:div.controls {:style {:margin-left "80px"}}
     ;;   (f/text :city {:style "width: 145px" :tabindex 1})]]

     ;; [:div.control-group
     ;;  [:label.control-label {:for "region"} "Region"]
     ;;  [:div.controls {:style {:margin-left "80px"}}
     ;;   (f/text :region {:style "width: 145px" :tabindex 1})]]

     ;; [:div.control-group
     ;;  [:label.control-label {:for "code"} "Postal Code"]
     ;;  [:div.controls {:style {:margin-left "80px"}}
     ;;   (f/text :code {:style "width: 145px" :tabindex 1})]]

     ;; [:div.control-group
     ;;  [:label.control-label {:for "country"} "Country"]
     ;;  [:div.controls {:style {:margin-left "80px"}}
     ;;   (f/text :country {:style "width: 145px" :tabindex 1})]]

     ;; [:div {:style "text-align: center"}
     ;;  [:button#submit.btn.btn-inverse {:type "submit" :tabindex 1} "Submit"]]
     ;; ]]
   ])

(defn handle
  [req]
  (response (layout/standard-page head (f/with-form sf/order-payment (:params req) (body)) 0)))
