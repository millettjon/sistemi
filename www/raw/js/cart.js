var sm = sm || {};
sm.cart = sm.cart || {};

// See: http://stackoverflow.com/questions/2812585/jquery-validate-characters-on-keypress

// Quanties in cart on server. Set this in cart.htm.
sm.cart.quantities = {};

sm.cart.update_quantity = function(event) {
  var target = event.target;
  var item_id = (/[0-9]+$/).exec(target.id)[0];
  var quantity = parseInt(target.value);
  var orig_quantity = sm.cart.quantities[item_id];
  var a = $('#quantity_action' + item_id);

  if (quantity == orig_quantity) {
    a.hide();
  }
  else if (!quantity || quantity == 0) {
    a.text('Delete');
    a.show();
  }
  else {
    a.text('Update');
    a.show();
  }
}

sm.cart.quantity_keypress = function (event) {
  var key = event.which || event.keyCode; //use event.which if it's truthy, and default to keyCode otherwise

  // Allow: backspace, delete, tab, and enter
  var controlKeys = [8, 46, 9, 13];
  //for mozilla these are arrow keys
  if ($.browser.mozilla) controlKeys = controlKeys.concat([37, 38, 39, 40]);

  // Ctrl+ anything or one of the conttrolKeys is valid
  var isControlKey = event.ctrlKey || controlKeys.join(",").match(new RegExp(key));

  if (isControlKey) {return;}

  // Stop current key press if it's not a number.
  if (!(48 <= key && key <= 57)) {
    //console.log('blocking key: ' + key);
    event.preventDefault();
    return;
  }
};

sm.cart.quantity_keyup = function(e) {
  var $th = $(this);

  // Delete any non numberic values that may have been pasted in.
  if (/[^0-9]/.test($th.val()))
    $th.val($th.val().replace(/[^0-9]/g, ''));

  // Reset large numbers to max allowed value.
  var quantity = parseInt($th.val()) || 0;
  if (quantity > 100)
    $th.val('100');

  // Update quantity.
  sm.cart.update_quantity(e);
};
