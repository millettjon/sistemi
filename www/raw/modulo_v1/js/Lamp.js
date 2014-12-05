/*	
|------------------------------------------------------------------------------------------
|	@author ivanmoreno
|	http://www.plus360degrees.com/
|   SistemiModerni
|------------------------------------------------------------------------------------------
*/

window.addEventListener( Event.LOAD, init, false );

var browser;
var path = "";

var stats;

var viewport, camera, scene, renderer, controller;

var toolBar;

var VIEW_WIDTH = window.innerWidth;
var VIEW_HEIGHT = window.innerHeight;

var groundMirror;

var loader = new THREE.JSONLoader();

var lamp;

var isAnim1 = false;
var isAnim2 = false;
var isAnim3 = false;
var isAnim4 = false;
var isLoaded = false;

function init() 
{
	browser = new PLUS360DEGREES.IdentifyBrowser( "SistemiModerni" );

	var loading = PLUS360DEGREES.DOM.div( 'loading' );
	loading.innerHTML = "Modulo is loading<br>Thank you for your patience";
	document.body.appendChild( loading );
	TweenLite.to( loading, 0.5, { opacity: 1 } );

	toolBar = PLUS360DEGREES.DOM.div( 'toolBar' );
	document.body.appendChild( toolBar );

	var colors = [ new THREE.Color( "#FFFAF7" ),
					new THREE.Color( "#3E8D31" ),
					new THREE.Color( "#83002C" ),
					new THREE.Color( "#363636" ),
					new THREE.Color( "#DB8FE7" ),
					new THREE.Color( "#EEB00D" ),
					new THREE.Color( "#1A1F5E" ),
					new THREE.Color( "#E7DEB3" ),
					new THREE.Color( "#DFCF24" ),
					new THREE.Color( "#54CCFF" ) ];

	var speculars = [ new THREE.Color( "#121212" ),
				      new THREE.Color( "#121212" ),
				      new THREE.Color( "#1A0F14" ),
				      new THREE.Color( "#121212" ),
				      new THREE.Color( "#121212" ),
				      new THREE.Color( "#121212" ),
				      new THREE.Color( "#1e1e1e" ),
				      new THREE.Color( "#111113" ),
				      new THREE.Color( "#121212" ),
				      new THREE.Color( "#161614" ) ];

	var colorsTop = ["#FFFAF7",
						"#3E8D31",
						"#83002C",
						"#363636",
						"#DB8FE7",
						"#EEB00D",
						"#1A1F5E",
						"#E7DEB3",
						"#DFCF24",
						"#54CCFF" ];

	var colorsBottom = ["#BFBFBF",
						"#2D6523",
						"#5B001E",
						"#1B1B1B",
						"#C542D7",
						"#9D7509",
						"#11143E",
						"#CDBA5F",
						"#9F9517",
						"#00AFFB" ];

	var colorButtons = [];

	for( var i = 0; i < colors.length; i++ )
	{
		var colorButton = PLUS360DEGREES.DOM.div( "color"+i );
		colorButton.classList.add( 'colorButton' );
		colorButton.style.left = ( 47 * i ) + 5 +'px';
		colorButton.style.backgroundImage = '-moz-linear-gradient( 180deg,' + colorsTop[i] + ', ' + colorsBottom[i] + ')';
		colorButton.style.backgroundImage = '-webkit-linear-gradient( 180deg,' + colorsTop[i] + ', ' + colorsBottom[i] + ')';
		colorButton.style.backgroundImage = '-o-linear-gradient( 180deg,' + colorsTop[i] + ', ' + colorsBottom[i] + ')';
		colorButton.style.backgroundImage = '-ms-linear-gradient( 180deg,' + colorsTop[i] + ', ' + colorsBottom[i] + ')';
		colorButton.style.backgroundImage = 'linear-gradient( 180deg, ' + colorsTop[i] + ', ' + colorsBottom[i] + ')';
		toolBar.appendChild( colorButton );
		colorButtons.push( colorButton );

		colorButtons[i].addEventListener( browser.clickEvent, 
			function( event )
			{
				event.preventDefault();
				var index = colorButtons.indexOf( event.currentTarget );

				var bodyColor = { r: bodyMaterial.color.r,
									g: bodyMaterial.color.g,
							  		b: bodyMaterial.color.b };

				var specularColor = { r: bodyMaterial.specular.r,
							  			g: bodyMaterial.specular.g,
							  			b: bodyMaterial.specular.b };

				TweenMax.to( bodyColor, 0.4,
					{ 
						r: colors[ index ].r,
						g: colors[ index ].g,
						b: colors[ index ].b,
						onUpdate: function()
						{
							bodyMaterial.color.setRGB( bodyColor.r, bodyColor.g, bodyColor.b );
						},
						ease:Linear.easeNone
					} );

				TweenMax.to( specularColor, 0.4,
					{ 
						r: speculars[ index ].r,
						g: speculars[ index ].g,
						b: speculars[ index ].b,
						onUpdate: function()
						{
							bodyMaterial.specular.setRGB( specularColor.r, specularColor.g, specularColor.b );
						},
						ease:Linear.easeNone
					} );
				
			}, false );
	}

	var buttons = [];
	var isStats = false;

	for( var i = 0; i < 8; i++ )
	{
		var button = PLUS360DEGREES.DOM.div( 'button'+ i );
		button.classList.add( 'button' );
		button.style.right = ( 75 * i ) + 5 + 'px';
		buttons.push( button );
		toolBar.appendChild( button );

		switch(i){
			case 0: 
				button.textContent = "+ SCREEN";
				break;
			case 1: 
				button.textContent = "ORBIT";
				break;
			case 2: 
				button.textContent = "AXIS 1";
				break;
			case 3: 
				button.textContent = "AXIS 2";
				break;
			case 4: 
				button.textContent = "AXIS 3";
				break;
			case 5: 
				button.textContent = "AXIS 4";
				break;
			case 6: 
				button.textContent = "RETURN";
				break;
			case 7: 
				button.textContent = "BACK";
				break;
		}

		buttons[i].addEventListener( browser.endEvent, 
			function( event )
			{
				switch( buttons.indexOf( event.currentTarget ) )
				{
					case 0:
						browser.toggleFullscreen();
						document.addEventListener( "fullscreenchange", function () {
						    buttons[0].textContent = ( document.fullscreen ) ? "- SCREEN" : "+ SCREEN";
						}, false );
						 
						document.addEventListener( "mozfullscreenchange", function () {
						    buttons[0].textContent = ( document.mozFullScreen ) ? "- SCREEN" : "+ SCREEN";
						}, false );
						 
						document.addEventListener( "webkitfullscreenchange", function () {
						    buttons[0].textContent = ( document.webkitIsFullScreen ) ? "- SCREEN" : "+ SCREEN";
						}, false );
						 
						document.addEventListener( "msfullscreenchange", function () {
						    buttons[0].textContent = ( document.msFullscreenElement ) ? "- SCREEN" : "+ SCREEN";
						}, false );
						break;
					case 1:
						controller.autoRotate = !controller.autoRotate;
						break;
					case 2:
						isAnim1 = !isAnim1;
						break;
					case 3:
						isAnim2 = !isAnim2;
						break;
					case 4:
						isAnim3 = !isAnim3;
						break;
					case 5:
						isAnim4 = !isAnim4;
						break;
					case 6:
						isAnim1 = isAnim2 = isAnim3 = isAnim4 = false;
						lamp.reset();
						break;
					case 7:
                                                window.location.href =  '/';
						break;
				}

				event.preventDefault();
				event.stopPropagation();
			}, false );
	};

	/**
	*	3D
	**/

	viewport = PLUS360DEGREES.DOM.div( 'viewport' );
	document.body.appendChild( viewport );

	scene = new THREE.Scene();

	camera = new THREE.PerspectiveCamera( 40, VIEW_WIDTH / VIEW_HEIGHT, 1, 12000 );

	renderer = new THREE.WebGLRenderer( { antialias: true, alpha: true } );
	renderer.setSize( VIEW_WIDTH, VIEW_HEIGHT );
	renderer.setClearColor( 0xeeeeee, 1 );
	viewport.appendChild( renderer.domElement );

	var light = new THREE.HemisphereLight( 0xbbbbbb, 0x777777, 1 );
	scene.add( light );

	var cameraLight = new THREE.PointLight( 0x777777, 0.6 );//.4
	scene.add( cameraLight );

	var bodyMaterial = new PLUS360DEGREES.BodyMaterial();
	lamp = new PLUS360DEGREES.Lamp( bodyMaterial, initExtras );
	lamp.rotation.y = THREE.Math.degToRad( 30 );
	scene.add( lamp );

	function initExtras()
	{
		var magazines = new PLUS360DEGREES.Magazine( animate );
		magazines.position.set( 0, 0.5, 0 );
		scene.add( magazines );

		var planeGeo = new THREE.PlaneGeometry( 3000, 3000 );

		groundMirror = new THREE.Mirror( renderer, camera, { 
			clipBias: 0.003, 
			textureWidth: VIEW_WIDTH, 
			textureHeight: VIEW_HEIGHT, 
			color: 0x878787 } );

		groundMirror.material.uniforms.opacity.value = 0.2;
		groundMirror.material.transparent = true;
		groundMirror.material.depthWrite = false;

		var mirrorMesh = new THREE.Mesh( planeGeo, groundMirror.material );
		mirrorMesh.add( groundMirror );
		mirrorMesh.rotateX( - Math.PI / 2 );
		mirrorMesh.position.set( 0, -1, 0 );
		scene.add( mirrorMesh );
	}

	window.addEventListener( Event.RESIZE, resize, false );
	window.addEventListener( KeyboardEvent.KEY_DOWN, 
		function( event )
		{
			event.preventDefault();
			event.stopPropagation();

			switch( event.keyCode )
			{
				case Keyboard.S:
					isStats = !isStats;
					if(isStats)
					{
						stats.domElement.style.visibility = 'visible';
					}else{
						stats.domElement.style.visibility = 'hidden';	
					}
					break;
				case Keyboard.ESCAPE:

					break;
			}
		}, false );

	stats = new Stats();
	stats.domElement.style.position = 'absolute';
	stats.domElement.style.zIndex = '20';
	stats.domElement.style.top = '0px';
	stats.domElement.style.left = '0px';
	stats.domElement.style.visibility = 'hidden';
	viewport.appendChild( stats.domElement );

	controller = new THREE.OrbitControls( camera, viewport );
	controller.addLight( cameraLight );
	controller.target = new THREE.Vector3( 0, 250, 0 );
	controller.minDistance = 650;
	controller.maxDistance = 1300;
	controller.enabledAutoRotatePhi = true;
	controller.autoRotateSpeed = 0.3;
	controller.phiRotationSpeed = 0.3;
	controller.minPolarAngle = THREE.Math.degToRad( 13 );
	controller.maxPolarAngle = THREE.Math.degToRad( 97 );
	controller.enabledAll( false );

	camera.rotation.y = THREE.Math.degToRad(90);
	camera.position.set( 0, 250, -1000 );
	cameraLight.position.copy( camera.position );

	window.removeEventListener( Event.LOAD, init, false );

	var dummy = PLUS360DEGREES.DOM.div('dummy');

	function animate()
	{
		render();

		var initTimeLine = new TimelineMax( { delay:0 } );
			initTimeLine.add( TweenLite.to( loading, 0.5, { opacity: 0, delay: 0, onComplete: 
				function()
				{ 
					document.body.removeChild( loading );
					colorButtons.forEach( function( b, i ){
						TweenLite.to( b, 0.2, { bottom: 10 + 'px', delay:i/12 } );
					} );
					buttons.forEach( function( b1, i1 ){
						TweenLite.to( b1, 0.2, { bottom: 10 + 'px', delay:i1/12 } );
					} );
					TweenLite.to( camera.rotation, 4, { y:THREE.Math.degToRad( 180 ), ease:Expo.easeOut, delay:1 } );
					TweenLite.to( dummy, 0.2, { opacity:0, delay:5, onComplete: function(){ 
						isLoaded = true;
						controller.enabledAll( true ); 
					} } );
				} 
			} ) );
			
	}

	function resize( event ) 
	{
		event.preventDefault();

		VIEW_WIDTH = window.innerWidth;
		VIEW_HEIGHT = window.innerHeight;

		camera.aspect =  VIEW_WIDTH / VIEW_HEIGHT;
		camera.updateProjectionMatrix();
		renderer.setSize( VIEW_WIDTH, VIEW_HEIGHT );
	}

}

function render() {
	requestAnimationFrame( render );

	stats.update();

	if( isLoaded )
		controller.update();

	if( lamp ){
		if( isAnim1 )
			lamp.rotateA1();
		if( isAnim2 )
			lamp.rotateA2();
		if( isAnim3 )
			lamp.rotateA3();
		if( isAnim4 )
			lamp.rotateHead();
	}
	
	groundMirror.render();
	
	renderer.render( scene, camera );
}
