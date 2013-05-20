/*
 Common code for rendering product models.
*/

// Namespace
var sm = sm || {};
sm.frame = sm.frame || {};

var dummy;
var camera, scene, renderer;
var g_container;

var windowHalf = {
  x: window.innerWidth / 2,
  y: window.innerHeight / 2
};

// model rotation
var rotation = {
  x: 0, y: 0,             // current
  start: { x: 0, y: 0}    // on mouse down or touch start
};

// mouse position
var mouse = {
  x: 0, y: 0,             // current
  start: { x: 0, y: 0}    // on mouse down or touch start
};

function makeContainer() {
  container = document.createElement( 'div' );
  document.body.appendChild( container );
  return container;
}

function onDocumentMouseDown( event ) {
  event.preventDefault();
  g_container.addEventListener( 'mousemove', onDocumentMouseMove, false );
  g_container.addEventListener( 'mouseup', onDocumentMouseUp, false );
  g_container.addEventListener( 'mouseout', onDocumentMouseOut, false );
  mouse.start.x = event.clientX - windowHalf.x;
  mouse.start.y = event.clientY - windowHalf.y;
  rotation.start.x = rotation.x;
  rotation.start.y = rotation.y;
}

// Limits the y rotation between.
function limitRotation(rotation) {
  if (rotation.y > Math.PI/6) { rotation.y = Math.PI/6; }
  if (rotation.y < - Math.PI/6) { rotation.y = - Math.PI/6; }
}

function onDocumentMouseMove( event ) {
  mouse.x = event.clientX - windowHalf.x;
  rotation.x = rotation.start.x + (mouse.x - mouse.start.x) * 0.02;
  mouse.y = event.clientY - windowHalf.y;
  rotation.y = rotation.start.y + (mouse.y - mouse.start.y) * 0.02;
  limitRotation(rotation);
}

function onDocumentMouseUp( event ) {
  g_container.removeEventListener( 'mousemove', onDocumentMouseMove, false );
  g_container.removeEventListener( 'mouseup', onDocumentMouseUp, false );
  g_container.removeEventListener( 'mouseout', onDocumentMouseOut, false );
}

function onDocumentMouseOut( event ) {
  g_container.removeEventListener( 'mousemove', onDocumentMouseMove, false );
  g_container.removeEventListener( 'mouseup', onDocumentMouseUp, false );
  g_container.removeEventListener( 'mouseout', onDocumentMouseOut, false );
}

function onDocumentTouchStart( event ) {
  if ( event.touches.length == 1 ) {
    event.preventDefault();
    mouse.start.x = event.touches[ 0 ].pageX - windowHalf.x;
    rotation.start.x = rotation.x;
    mouse.start.y = event.touches[ 0 ].pageY - windowHalf.y;
    rotation.start.y = rotation.y;
  }
}

function onDocumentTouchMove( event ) {
  if ( event.touches.length == 1 ) {
    event.preventDefault();
    mouse.x = event.touches[ 0 ].pageX - windowHalf.x;
    rotation.x = rotation.start.x + (mouse.x - mouse.start.x) * 0.02;
    mouse.y = event.touches[ 0 ].pageY - windowHalf.y;
    rotation.y = rotation.start.y + (mouse.y - mouse.start.y) * 0.02;
    limitRotation(rotation);
  }
}

// Converts a hex rgb string to an int.
function rgbHexToInt(rgb) {
  return parseInt(rgb.substring(1), 16);
}

// Converts a jquery rgb color string to a hex integer.
function rgb2hex(rgb) {
  var rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
  return (rgb[1] << 16) | (rgb[2] << 8) | rgb[3];
}

// Returns the luminence of an rgb color string.
function luminence(color) {
  var r = (color >> 16) / 255;
  var g = (color >> 8 & 0xFF) / 255;
  var b = (color & 0xFF) / 255;
  var min = Math.min(r, Math.min(g, b));
  var max = Math.max(r, Math.max(g, b));
  var l = (min + max) / 2;
  return l;
}

// shim layer with setTimeout fallback
window.requestAnimFrame = (function(){
  return  window.requestAnimationFrame || 
    window.webkitRequestAnimationFrame || 
    window.mozRequestAnimationFrame    || 
    window.oRequestAnimationFrame      || 
    window.msRequestAnimationFrame     || 
    function( callback ){
      window.setTimeout(callback, 1000 / 60);
    };
})();

var animationRunning = false;
var stopFrameAnim = false;

function startAnimation() {
  animationRunning = true;
  dummy.rotation.y += rotation.x;
  dummy.rotation.x += rotation.y;
  animate();
}

function stopAnimation() {
  animationRunning = false;
}

function animate() {
  if (animationRunning) {
    if (!stopFrameAnim) {
      requestAnimFrame(animate);
    }
    render();
  }
}

function render() {
  dummy.rotation.y += ( rotation.x - dummy.rotation.y ) * 0.05;
  dummy.rotation.x += ( rotation.y - dummy.rotation.x ) * 0.05;
  renderer.render( scene, camera );
};
