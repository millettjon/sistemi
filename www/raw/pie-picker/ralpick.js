$(function() {
  var container = '#ral-wheel';
  var palette = color.ral.palette;
  var callback = function(color) { console.log("onColor: ", color); };
  color.ral_picker.init(container, palette, callback);
});
