// TODO: Fix triangulation error in oval cutout.
// TODO: Improve horizontal spacing using well defined layout algorithm.
//
// TODO: Embed in shelving design page.
// TODO: Scale to size.
// TODO: Update when controls change.
// TODO: Update background color to maintain constrast w/ shelving color.
//
// TODO: Color members differently for debugging (easter egg?).
//
// TODO: Test for webgl and fallback to canvas if not available.
//       - http://stackoverflow.com/questions/9899807/three-js-detect-webgl-support-and-fallback-to-regular-canvas
//       - http://weblogs.asp.net/dwahlin/archive/2012/06/22/detecting-html5-css3-features-using-modernizr.aspx
// TODO: Review compatability: http://caniuse.com/webgl
// TODO: Test on IE9.
// TODO: Test on iPad.
// TODO: Test on android.
//       - works w/ canvas in wk and ff
// TODO: See if requestAnimationFrame shim is needed.
//       - http://www.html5canvastutorials.com/webgl/html5-canvas-webgl-ambient-lighting-with-three-js/
// TODO: Add a fallback for browsers that don't support canvas?
//       node.js render to png on server?/
//       See: toDataURL
//       See: http://stackoverflow.com/questions/8900498/exporting-html-canvas-as-an-image-sequence
//       WebDriver https://gist.github.com/1666559
//
// TODO: Explode for instructions?
// TODO: Add slots?
//


var dummy;

var camera, scene, renderer;

var text, plane;

var targetRotation = 0;
var targetRotationOnMouseDown = 0;

var mouseX = 0;
var mouseXOnMouseDown = 0;

var windowHalfX = window.innerWidth / 2;
var windowHalfY = window.innerHeight / 2;

// Namespaces
var sm = sm || {};
sm.shelving = sm.shelving || {};

// Configuration
sm.shelving = {
  // Model Parameters.
  vertical: {inset: 15},          // distance vertical member is inset from each end
  lateral: {width: 9,
            inset: 5},            // distance lateral's are inset from rear
  thickness:  3                   // MDF thickness
};

// Extrusion settings.
sm.shelving.extrusion = {
  amount: sm.shelving.thickness,
  steps: 1,
  bevelEnabled: false,
  curveSegments: 1};

// Returns the number of vertical members in a shelving unit.
sm.shelving.numVerticals = function(shelving) {
  var d = shelving.width;
  var n = 2;
  if (d > 120) n++;
  if (d > 195) n++;
  return n;
}

// Returns the number of horizontal members in a shelving unit.
sm.shelving.numHorizontals = function(shelving) {
  var d = shelving.height;
  var n = 2;
  if (d > 70) n++;
  if (d > 93) n++;
  if (d > 130) n++;
  if (d > 167) n++;
  if (d > 203) n++;
  return n;
}

// Define the shelving unit.
var shelving = {height: 200, width: 150, depth: 50, color: 0xab003b, cutout: 'quaddro'};
shelving.color = 0x00FF00;
//shelving.cutout = 'none';
//shelving.cutout = 'ovale';
//shelving.width = 70;
//shelving.height = 70;
//shelving.depth = 25;

drawShelving(shelving);
animate();

function makeContainer() {
  container = document.createElement( 'div' );
  document.body.appendChild( container );
  return container;
}

function addInfo(container) {
  var info = document.createElement('div');
  info.style.position = 'absolute';
  info.style.top = '10px';
  info.style.width = '100%';
  info.style.textAlign = 'center';
  info.innerHTML = 'Shelving Unit<br/>Drag to spin';
  container.appendChild( info );
}

// Returns an inner bounding box by subtracting a margin from an outer box.
function innerBox(bbox, margin) {
  var ibox =  {top : bbox.top - margin,
               left: bbox.left + margin,
               bottom: bbox.bottom + margin,
               right: bbox.right - margin};
  var width = ibox.right - ibox.left;
  var height = ibox.top - ibox.bottom;
  var center = {x : ibox.left + width/2,
                y : ibox.bottom + height/2};
  ibox.center = center;
  return ibox;
}

// Cuts a rounded rectangle hole centered in the given bounding box.
function cutoutRRect(shape, bbox) {
  var margin = 4;
  var radius = 1;
  var box = innerBox(bbox, margin);

  var path = new THREE.Path();

  path.moveTo(box.left + radius, box.bottom);
  path.lineTo(box.right - radius, box.bottom);

  // WTF: This has bugs when there is one hole but works when there
  // are many.
  // path.quadraticCurveTo( box.right, box.bottom, box.right, box.bottom + radius );

  path.lineTo(box.right, box.bottom + radius);

  path.lineTo(box.right, box.top - radius);
  path.lineTo(box.right - radius, box.top);
  path.lineTo(box.left + radius, box.top);
  path.lineTo(box.left, box.top - radius);
  path.lineTo(box.left, box.bottom + radius);
  shape.holes.push( path );
}

// Cuts a rectangle hole centered in the given bounding box.
// WTF: This only works when there is one hole.
function cutoutRectAC(shape, bbox) {
  var margin = 4;
  var box = innerBox(bbox, margin);

  var path = new THREE.Path();
  path.moveTo(box.left, box.bottom);
  path.lineTo(box.right, box.bottom);
  path.lineTo(box.right, box.top);
  path.lineTo(box.left, box.top);
  path.lineTo(box.left, box.bottom);
  shape.holes.push( path );
}

// Cuts a oval hole centered in the given bounding box.
function cutoutOval(shape, bbox) {
  var margin = 4;
  var box = innerBox(bbox, margin);

  var path = new THREE.Path();

  // AC from bottom center.
  // Works with defects (triangulation error).
  if (true) {
    path.moveTo(box.center.x, box.bottom);
    path.quadraticCurveTo( box.right, box.bottom, box.right, box.center.y);
    path.quadraticCurveTo( box.right, box.top, box.center.x, box.top);
    path.quadraticCurveTo( box.left, box.top, box.left, box.center.y);
    path.quadraticCurveTo( box.left, box.bottom, box.center.x, box.bottom);
  }

  // AC from right center.
  // Works with defects (triangulation error).
  if (false) {
    path.moveTo(box.right, box.center.y);
    path.quadraticCurveTo( box.right, box.top, box.center.x, box.top);
    path.quadraticCurveTo( box.left, box.top, box.left, box.center.y);
    path.quadraticCurveTo( box.left, box.bottom, box.center.x, box.bottom);
    path.quadraticCurveTo( box.right, box.bottom, box.right, box.center.y);
  }

  // AC from left center.
  // Works with defects (triangulation error).
  if (false) {
    path.moveTo(box.left, box.center.y);
    path.quadraticCurveTo( box.left, box.bottom, box.center.x, box.bottom);
    path.quadraticCurveTo( box.right, box.bottom, box.right, box.center.y);
    path.quadraticCurveTo( box.right, box.top, box.center.x, box.top);
    path.quadraticCurveTo( box.left, box.top, box.left, box.center.y);
  }

  // AC from top center.
  // Works with defects (triangulation error).
  if (false) {
    path.moveTo(box.center.x, box.top);
    path.quadraticCurveTo( box.left, box.top, box.left, box.center.y);
    path.quadraticCurveTo( box.left, box.bottom, box.center.x, box.bottom);
    path.quadraticCurveTo( box.right, box.bottom, box.right, box.center.y);
    path.quadraticCurveTo( box.right, box.top, box.center.x, box.top);
  }

  // CW from bottom center.
  // Doesn't work at all (No holes, but no error either WTF).
  if (false) {
    path.moveTo(box.center.x, box.bottom);
    path.quadraticCurveTo( box.left, box.bottom, box.left, box.center.y);
    path.quadraticCurveTo( box.left, box.top, box.center.x, box.top);
    path.quadraticCurveTo( box.right, box.top, box.right, box.center.y);
    path.quadraticCurveTo( box.right, box.bottom, box.center.x, box.bottom);
  }

  // CW from top center.
  // Doesn't work at all (No holes, but no error either WTF).
  if (false) {
    path.moveTo( box.center.x, box.top );
    path.quadraticCurveTo( box.right, box.top,    box.right,    box.center.y );
    path.quadraticCurveTo( box.right, box.bottom, box.center.x, box.bottom   );
    path.quadraticCurveTo( box.left,  box.bottom, box.left,     box.center.y );
    path.quadraticCurveTo( box.left,  box.top,    box.center.x, box.top      );
  }

  shape.holes.push( path );
}


function addVerticalMembers(shelving, addGeometry) {
  var s = shelving;
  var ns = sm.shelving;

  // Draw a vertical member.
  var shape = new THREE.Shape();
  shape.moveTo(0, 0);
  shape.lineTo(0, s.height);
  shape.lineTo(s.depth, s.height);
  shape.lineTo(s.depth, 0);
  shape.lineTo(0, 0);

  // Add cutouts
  positions = layoutHorizontal(s);
  for (i=0; i < positions.length - 1 ; i++) {
    var bbox = {bottom : positions[i] + ns.thickness, left : 0,
                top: positions[i+1], right: s.depth};
    switch (s.cutout) {
      case 'quaddro': cutoutRRect(shape, bbox);
                      break;
      case 'ovale': cutoutOval(shape, bbox);
                    break;
      default:
    }
  }

  var shape3d = shape.extrude( ns.extrusion );

  // Add verticals in correct positions.
  num = ns.numVerticals(s);
  var offset = ns.vertical.inset;
  var span = s.width - ns.vertical.inset * 2 - ns.thickness;
  var step = span / (num - 1);
  for (i=0; i < num; i++) {
    var x = offset + step * i + ns.thickness;
    // console.log("veritical x: " + x);
    addGeometry(shape3d, s.color,  x, 0, 0,
                                   0, -Math.PI/2, 0,  1);
  }
}

function addLateralMembers(shelving, addGeometry) {
  var s = shelving;
  var ns = sm.shelving;

  // Draw a lateral member.
  var Shape = new THREE.Shape();
  Shape.moveTo(0, 0);
  Shape.lineTo(s.width, 0);
  Shape.lineTo(s.width, ns.lateral.width);
  Shape.lineTo(0, ns.lateral.width);
  Shape.lineTo(0, 0);
  var shape3d = Shape.extrude( ns.extrusion );
  addGeometry(shape3d, s.color,  0, 0, ns.lateral.inset,
                                 0, 0, 0,  1);
  addGeometry(shape3d, s.color,  0, s.height - ns.lateral.width, ns.lateral.inset,
                                 0, 0, 0,  1);
}

function layoutHorizontal(shelving) {
  var s = shelving;
  var ns = sm.shelving;

  num = ns.numHorizontals(s);
  var offset = ns.lateral.width;
  var span = s.height - offset * 2 - ns.thickness;
  var step = span / (num - 1);
  var positions = [];
  for (i=0; i < num; i++) {
    y = offset + step * i;
    console.log("layout y: " + y);
    positions.push(y);
  }
  return positions;
}

function addHorizontalMembers(shelving, addGeometry) {
  var s = shelving;
  var ns = sm.shelving;

  // Draw a horizontal member.
  var shape = new THREE.Shape();
  shape.moveTo(0, 0);
  shape.lineTo(s.width, 0);
  shape.lineTo(s.width, s.depth);
  shape.lineTo(0, s.depth);
  shape.lineTo(0, 0);
  var shape3d = shape.extrude( ns.extrusion );

  // Add horizontals at the correct positions.
  positions = layoutHorizontal(s);
  for (i in positions) {
    y = positions[i] + ns.thickness;
    console.log("rendering y at: " + y)
    addGeometry(shape3d, s.color,  0, y, 0,
                                   Math.PI/2, 0, 0,  1);
  }
}

function drawShelving(shelving) {
  var container = makeContainer();
  addInfo(container);

  // Create the scene and camera.
  scene = new THREE.Scene();
  camera = new THREE.PerspectiveCamera( 50, window.innerWidth / window.innerHeight, 1, 1000 );
  camera.position.set( 0, 0, 500 );
  scene.add( camera );

  // Add subtle ambient lighting.
  var ambientLight = new THREE.AmbientLight(0x333333);
  scene.add(ambientLight);

  // Add directional light.
  var light = new THREE.DirectionalLight(0xffffff);
  light.position.set( 1, 1, 1 ).normalize();
  scene.add( light );

  // Set rendering mode.
  var useWebGL = true;
  //useWebGL = false;

  // Create a parent container object and a helper function to add all
  // components to it.
  var parent = new THREE.Object3D();
  function addGeometry( geometry, color, x, y, z, rx, ry, rz, s ) {
    var material = useWebGL ?
      new THREE.MeshLambertMaterial({color: color}) :
      new THREE.MeshBasicMaterial({color: color});
    
    var mesh = THREE.SceneUtils.createMultiMaterialObject( geometry, [material] );
    mesh.position.set( x, y, z );
    mesh.rotation.set( rx, ry, rz );
    mesh.scale.set( s, s, s );
    parent.add( mesh );
  }

  // Build components.
  addVerticalMembers(shelving, addGeometry);
  addLateralMembers(shelving, addGeometry);
  addHorizontalMembers(shelving, addGeometry);

  // Center.
  // Use a dummy object so that rotation happens around the center of the bounding box
  // instead of the shelving unit's origin. See: https://github.com/mrdoob/three.js/issues/1593
  dummy = new THREE.Object3D();
  scene.add( dummy );
  parent.position.set( - shelving.width/2, -shelving.height/2, -shelving.depth/2 );  // half width, height, depth
  dummy.add( parent );

  // Setup renderer.
  renderer = new THREE.WebGLRenderer( { antialias: true } );
  //renderer = new THREE.CanvasRenderer( { antialias: true } );
  renderer.setSize( window.innerWidth, window.innerHeight );
  container.appendChild( renderer.domElement );

  // Setup event handlers.
  document.addEventListener( 'mousedown', onDocumentMouseDown, false );
  document.addEventListener( 'touchstart', onDocumentTouchStart, false );
  document.addEventListener( 'touchmove', onDocumentTouchMove, false );
}

function onDocumentMouseDown( event ) {
  event.preventDefault();
  document.addEventListener( 'mousemove', onDocumentMouseMove, false );
  document.addEventListener( 'mouseup', onDocumentMouseUp, false );
  document.addEventListener( 'mouseout', onDocumentMouseOut, false );
  mouseXOnMouseDown = event.clientX - windowHalfX;
  targetRotationOnMouseDown = targetRotation;
}

function onDocumentMouseMove( event ) {
  mouseX = event.clientX - windowHalfX;
  targetRotation = targetRotationOnMouseDown + ( mouseX - mouseXOnMouseDown ) * 0.02;
}

function onDocumentMouseUp( event ) {
  document.removeEventListener( 'mousemove', onDocumentMouseMove, false );
  document.removeEventListener( 'mouseup', onDocumentMouseUp, false );
  document.removeEventListener( 'mouseout', onDocumentMouseOut, false );
}

function onDocumentMouseOut( event ) {
  document.removeEventListener( 'mousemove', onDocumentMouseMove, false );
  document.removeEventListener( 'mouseup', onDocumentMouseUp, false );
  document.removeEventListener( 'mouseout', onDocumentMouseOut, false );
}

function onDocumentTouchStart( event ) {
  if ( event.touches.length == 1 ) {
    event.preventDefault();
    mouseXOnMouseDown = event.touches[ 0 ].pageX - windowHalfX;
    targetRotationOnMouseDown = targetRotation;
  }
}

function onDocumentTouchMove( event ) {
  if ( event.touches.length == 1 ) {
    event.preventDefault();
    mouseX = event.touches[ 0 ].pageX - windowHalfX;
    targetRotation = targetRotationOnMouseDown + ( mouseX - mouseXOnMouseDown ) * 0.05;
  }
}

function animate() {
  requestAnimationFrame( animate );
  render();
}

function render() {
  //parent.rotation.y += ( targetRotation - parent.rotation.y ) * 0.05;
  dummy.rotation.y += ( targetRotation - dummy.rotation.y ) * 0.05;
  renderer.render( scene, camera );
};
