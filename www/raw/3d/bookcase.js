// TODO: Add svg support as fallback?
// TODO: Add stop frame animation mode
//       - disable animation
//       - redraw on change only
//       - factor mouse spin control into separate file
//       - add spin buttons
//       - auto switch to stop frame animation if frame rate is too slow
// TODO: IE support
//       - https://code.google.com/p/explorercanvas/
//         - https://code.google.com/p/explorercanvas/wiki/Instructions
//         - uses VML
//         - needs work to be compatible with three.js
//       - http://code.google.com/p/svgweb/
//         - uses flash to render SVG
//       - http://raphaeljs.com/
//         - uses SVG and VML on IE
//       - https://github.com/learnboost/node-canvas
//         - renders as image over websocket
//         - https://github.com/mrdoob/three.js/issues/2182
//         - https://github.com/mrdoob/three.js/pull/2084
//       - http://iewebgl.com/Faq.aspx
//         - freeware plugin
//       - unity3d?
//         - requires plugin
// TODO: Improve horizontal spacing using well defined layout algorithm.
// TODO: Fix triangulation error in oval cutout.
// TODO: Add sliders for dimensions? How to work on phones?
//       - http://jqueryfordesigners.com/demo/slider-gallery.html (apple like)
//       - http://papermashup.com/demos/jquery-slider-bar/ (another style)
//       - http://www.ryancoughlin.com/demos/interactive-slider/ (basic)
//       - http://d2o0t5hpnwv4c1.cloudfront.net/377_slider/slider_sourcefiles/slider.html
// TODO: Get it working with window resize.
//
// TODO: Review compatability: http://caniuse.com/webgl
// TODO: Test on IE9.
// TODO: Test on iPad.
// TODO: Add a fallback for browsers that don't support canvas?
//       node.js render to png on server?/
//       See: toDataURL
//       See: http://stackoverflow.com/questions/8900498/exporting-html-canvas-as-an-image-sequence
//       WebDriver https://gist.github.com/1666559
//
// TODO: Color members differently for debugging (easter egg?).
// TODO: Explode for instructions?
// TODO: Add slots?
//

// Namespace
var sm = sm || {};
sm.bookcase = sm.bookcase || {};

// Configuration
sm.bookcase = {
  // Model Parameters.
  vertical: {inset: 15},          // distance vertical member is inset from each end
  lateral: {width: 9,
            inset: 3},            // distance lateral's are inset from rear
  thickness:  1.9                   // MDF thickness
};

// Extrusion settings.
sm.bookcase.extrusion = {
  amount: sm.bookcase.thickness,
  steps: 1,
  bevelEnabled: false,
  curveSegments: 1};

// Returns the number of vertical members in a bookcase.
sm.bookcase.numVerticals = function(bookcase) {
  var d = bookcase.width;
  var n = 2;
  if (d > 120) n++;
  if (d > 195) n++;
  return n;
}

// Returns the number of horizontal members in a bookcase.
sm.bookcase.numHorizontals = function(bookcase) {
  var d = bookcase.height;
  var n = 2;
  if (d > 70) n++;
  if (d > 93) n++;
  if (d > 130) n++;
  if (d > 167) n++;
  if (d > 203) n++;
  return n;
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
  var margin = 3.8;
  var radius = 0.3;
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
  var margin = 3.8;
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
  var margin = 3.8;
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

function addVerticalMembers(bookcase, addGeometry) {
  var s = bookcase;
  var ns = sm.bookcase;

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
      case ':quadro': cutoutRRect(shape, bbox);
        break;
      case ':ovale': cutoutOval(shape, bbox);
        break;
      case ':semplice':
        break;
      default:
        console.warn('Ignoring invalid cutout: "' + s.cutout + '"');
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

function addLateralMembers(bookcase, addGeometry) {
  var s = bookcase;
  var ns = sm.bookcase;

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

function layoutHorizontal(bookcase) {
  var s = bookcase;
  var ns = sm.bookcase;

  num = ns.numHorizontals(s);
  var offset = ns.lateral.width;
  var span = s.height - offset * 2 - ns.thickness;
  var step = span / (num - 1);
  var positions = [];
  for (i=0; i < num; i++) {
    y = offset + step * i;
    //console.log("layout y: " + y);
    positions.push(y);
  }
  return positions;
}

function addHorizontalMembers(bookcase, addGeometry) {
  var s = bookcase;
  var ns = sm.bookcase;

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
    // console.log("rendering y at: " + y)
    addGeometry(shape3d, s.color,  0, y, 0,
                                   Math.PI/2, 0, 0,  1);
  }
}

function drawBookcase(bookcase, container) {

  // Create the scene and camera.
  scene = new THREE.Scene();
  var rWidth = container.offsetWidth;
  var rHeight = container.offsetHeight;
  var fov = 30;
  camera = new THREE.PerspectiveCamera( fov, rWidth / rHeight, 1, 1000 );

  // Set the camera at the point where the bookcase just fits in the container.
  // TODO: handle not square aspect ratio.
  var dist = (Math.max(bookcase.height, bookcase.width) / 2) /
             Math.tan(fov/2*2*Math.PI/360) +
             bookcase.depth / 2;
  dist *= 1.2;
  camera.position.set( 0, 0, dist);
  scene.add( camera );

  // Set rendering mode.
  // TODO: Set mode WebGL -> Canvas -> Image
  var useWebGL = Detector.webgl;
  //useWebGL = false;
  console.log("canvas: " + Detector.canvas);
  console.log("webgl: " + Detector.webgl);
  console.log("webgl2: " + Detector.webgl2);

  // Add subtle ambient lighting.
  if (useWebGL) {
    //var ambientLight = new THREE.AmbientLight(0x222222);
    var ambientLight = new THREE.AmbientLight(0x111111);
    scene.add(ambientLight);
  }

  // Add directional light.
  if (useWebGL) {
    var light = new THREE.DirectionalLight(0xffffff);
    light.position.set( 1, 1, 1 ).normalize();
    scene.add(light);
  }

  // Create a parent container object and a helper function to add all
  // components to it.
  var parent = new THREE.Object3D();
  function addGeometry( geometry, color, x, y, z, rx, ry, rz, s ) {
    var material = useWebGL ?
      new THREE.MeshLambertMaterial({color: color}) :
      //new THREE.MeshLambertMaterial({color: color, map: THREE.ImageUtils.loadTexture("textures/plywood.jpg")}) :
      //new THREE.MeshPhongMaterial({color: color}) :
      new THREE.MeshBasicMaterial({color: color});
    
    var mesh = THREE.SceneUtils.createMultiMaterialObject( geometry, [material] );
    mesh.position.set( x, y, z );
    mesh.rotation.set( rx, ry, rz );
    mesh.scale.set( s, s, s );
    parent.add( mesh );
  }

  // Build components.
  addVerticalMembers(bookcase, addGeometry);
  addLateralMembers(bookcase, addGeometry);
  addHorizontalMembers(bookcase, addGeometry);

  // Center.
  // Use a dummy object so that rotation happens around the center of the bounding box
  // instead of the bookcase's origin. See: https://github.com/mrdoob/three.js/issues/1593
  dummy = new THREE.Object3D();
  scene.add( dummy );
  parent.position.set( - bookcase.width/2, -bookcase.height/2, -bookcase.depth/2 );  // half width, height, depth
  dummy.add( parent );

  // Setup renderer.
  renderer = useWebGL ?
    new THREE.WebGLRenderer( { antialias: true } ) :
    new THREE.CanvasRenderer( { antialias: true } );
    //new THREE.SVGRenderer( { antialias: true } );
  renderer.setSize( rWidth, rHeight );
  container.appendChild( renderer.domElement );

  // Setup event handlers.
  g_container = container;
  g_container.addEventListener( 'mousedown', onDocumentMouseDown, false );
  g_container.addEventListener( 'touchstart', onDocumentTouchStart, false );
  g_container.addEventListener( 'touchmove', onDocumentTouchMove, false );
}

function updateAnimation(bookcase) {
  // Stop the animation and clear the model.
  stopAnimation();
  $(g_container).empty();

  // Redraw the model and start animating.
  drawBookcase(bookcase, g_container);
  startAnimation();
}
