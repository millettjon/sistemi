$(function() {

  function onColor(color) {
    console.log("onColor: ", color);
    alert("onColor: " + color);
  }

  var canvas = $('#colorwheel').get(0);
  var palette = color.ral.palette;
  var callback = onColor;
  var options = null;
  color.pie_picker.init(canvas, palette, callback, options);

});
