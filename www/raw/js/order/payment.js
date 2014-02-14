var stripeResponseHandler = function(status, response) {
  // Used client side only to edit cc fields and submit to stripe.
  var $ccForm = $('#cc-form');
  // Used to post stripe token to server.
  var $tokenForm = $('#token-form');

  console.log('stripe response:');
  console.log(status);
  console.log(response);
  logEvent(response);

  if (response.error) {
    console.log('error');

    // Show the errors on the form
    $ccForm.find('.payment-errors').text(response.error.message);
    $ccForm.find('button').prop('disabled', false);

    // Log the error.
    response.status = status;
  } else {
    console.log('success');
    // token contains id, last4, and card type
    var token = response.id;
    // Insert the token into the form so it gets submitted to the server
    $tokenForm.append($('<input type=\"hidden\" name=\"stripe-token\" />').val(token));
    // and submit
    $tokenForm.get(0).submit();

    // TODO: leave it disabled?   
    $ccForm.find('button').prop('disabled', false);
  }

};

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

  $('#cc-form').submit(function(event) {
    // Note: make sure to validate both forms.
    if (!validateForm() || !$('#cc-form').valid()) {
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

    // Get stripe token. This will include any form fields that have the data-stripe attribute.
    Stripe.card.createToken($form, stripeResponseHandler);

    // Prevent the form from submitting with the default action.
    return false;
  });
});

jQuery(document).ready(function() {
    // Focus the first form element.
    $('#name').focus();

    // Validate form on keyup and submit
    $('#cc-form').validate({
        rules: {
            'name': 'required',
            'number': 'required',
            'address1': 'required',
            'city': 'required',
            'code': 'required',
            'country': 'required'
        }
    });
});
