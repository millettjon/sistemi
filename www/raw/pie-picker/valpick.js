$(function() {

  var callback = function (color) {
    console.log("onColor: ", color);
  }

  color.val_picker.init('#wheel-raw', "raw", callback);
  color.val_picker.init('#wheel-oiled', "oiled", callback);

  $('#oiled').change(function(e) {
    // change visibility; ? does this activate/deactivate event handlers as well?
    var showOiled = $(e.target).is(":checked");
    if (showOiled) {
      $('#wheel-raw').css('visibility', 'hidden');
      $('#wheel-oiled').css('visibility', 'visible');
    }
    else {
      $('#wheel-oiled').css('visibility', 'hidden');
      $('#wheel-raw').css('visibility', 'visible');
    }
  });
});
