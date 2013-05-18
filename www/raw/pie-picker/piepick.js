$(function() {

  function onColor(oldColor, newColor) {
    console.log("onColor: ", oldColor, newColor);
  }

  var canvas = $('#colorwheel').get(0);
  var palette = color.ral.palette;
  var callback = onColor;
  var options = null;
  color.pie_picker.init(canvas, palette, callback, options);

});
