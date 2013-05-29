$(function() {

  function onColor(color) {
    console.log("onColor: ", color);
    //alert("onColor: " + color);
  }

  var canvas = $('#colorwheel').get(0);
  var callback = onColor;

  color.val_picker.init(canvas
                        //,"raw"
                        ,"oiled"
                        ,callback);

});
