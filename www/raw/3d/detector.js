Detector = {
  canvas: !! window.CanvasRenderingContext2D,
  webgl: ( function () { try { return !! window.WebGLRenderingContext && !! document.createElement( 'canvas' ).getContext( 'experimental-webgl' ); } catch( e ) { return false; } } )()
}

jQuery(document).ready(function() {
  console.log("inside");
  // Check for canvas and webgl.
  if (Detector.webgl) {
    $('#under_construction').css('display', 'inline');
  }
  else if (Detector.canvas) {
    $('#webgl_recommended').css('display', 'inline');
  }
  else {
    $('#canvas_recommended').css('display', 'inline');
  }
});
