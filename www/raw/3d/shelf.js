// Animates a single shelf.

// Namespace
var sm = sm || {};
sm.shelf = sm.shelf || {};

// Configuration
sm.shelf = {
  thickness:  3    // MDF thickness
};

// Extrusion settings.
sm.shelf.extrusion = {
  amount: sm.shelf.thickness,
  steps: 1,
  bevelEnabled: false,
  curveSegments: 1};

// target rotation
// Note: Start with a slight downward rotation to see more than just the edge of the shelf.
rotation.y = Math.PI/14;

// Calculate how many shelves display cleanly in the window.
// Assumes spacing of 1 shelf depth.
function numShelvesToDisplay(shelf) {
  var s = shelf;
  var max = Math.floor(s.width / s.depth);
  return Math.min(s.quantity, max);
}

function updateNumDisplayed(shelf) {
  var s = shelf;
  var d = $('#numDisplayed');
  var n = numShelvesToDisplay(s);
  var vis = (s.quantity == n) ? 'hidden' : 'visible';
  d.css('visibility', vis);
  d.text('(' + n + ' displayed)');
}

function shelvesHeight(shelf) {
  var s = shelf;
  // TODO: include shelf thickness
  return (numShelvesToDisplay(s) - 1) * s.depth;
}

function layoutHorizontal(shelf) {
  var s = shelf;
  num = numShelvesToDisplay(s);
  var step = s.depth;
  var positions = [];
  for (i=0; i < num; i++) {
    y = step * i;
    //console.log("layout y: " + y);
    positions.push(y);
  }
  return positions;
}

function addShelf(shelf, addGeometry) {
  var s = shelf;
  var ns = sm.shelf;

  // Draw a horizontal member.
  var shape = new THREE.Shape();
  shape.moveTo(0, 0);
  shape.lineTo(s.width, 0);
  shape.lineTo(s.width, s.depth);
  shape.lineTo(0, s.depth);
  shape.lineTo(0, 0);
  var shape3d = shape.extrude( ns.extrusion );
  // addGeometry(shape3d, s.color,  0,0,0,  Math.PI/2,0,0,  1);

  // Add horizontals at the correct positions.
  positions = layoutHorizontal(s);
  for (i in positions) {
    y = positions[i] + ns.thickness;
    // console.log("rendering y at: " + y)
    addGeometry(shape3d, s.color,  0,y,0,  Math.PI/2,0,0,  1);
  }
}

function drawShelf(shelf, container) {
  // Create the scene and camera.
  scene = new THREE.Scene();
  var rWidth = container.offsetWidth;
  var rHeight = container.offsetHeight;
  var fov = 30;
  camera = new THREE.PerspectiveCamera( fov, rWidth / rHeight, 1, 1000 );

  // Set the camera at the point where the shelf just fits in the container.
  // TODO: handle not square aspect ratio.
  var dist = (shelf.width / 2) /
             Math.tan(fov/2*2*Math.PI/360) +
             shelf.depth / 2;
  dist *= 1.2;
  camera.position.set( 0, 0, dist);
  scene.add( camera );

  // Set rendering mode.
  // TODO: Set mode WebGL -> Canvas -> Image
  var useWebGL = Detector.webgl;
  //useWebGL = false;
  console.log("canvas: " + Detector.canvas);
  console.log("webgl: " + Detector.webgl);

  // Add subtle ambient lighting.
  if (useWebGL) {
    var ambientLight = new THREE.AmbientLight(0x222222);
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
      new THREE.MeshBasicMaterial({color: color});
    
    var mesh = THREE.SceneUtils.createMultiMaterialObject( geometry, [material] );
    mesh.position.set( x, y, z );
    mesh.rotation.set( rx, ry, rz );
    mesh.scale.set( s, s, s );
    parent.add( mesh );
  }

  // Build components.
  addShelf(shelf, addGeometry);

  // Center.
  // Use a dummy object so that rotation happens around the center of the bounding box
  // instead of the shelf unit's origin. See: https://github.com/mrdoob/three.js/issues/1593
  dummy = new THREE.Object3D();
  scene.add( dummy );
  parent.position.set( - shelf.width/2, - shelvesHeight(shelf)/2, - shelf.depth/2 );  // width, height, depth
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

function updateAnimation(shelf) {
  // Stop the animation and clear the model.
  stopAnimation();
  $(g_container).empty();

  // Update the indicator for number of shelves displayed.
  updateNumDisplayed(shelf);

  // Redraw the model and start animating.
  drawShelf(shelf, g_container);
  startAnimation();
}
