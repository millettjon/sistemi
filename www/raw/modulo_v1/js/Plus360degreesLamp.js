/*	
|------------------------------------------------------------------------------------------
|	@author ivanmoreno
|	http://www.plus360degrees.com/
|	
|	The MIT License (MIT)
|
|	Copyright (c) 2014 - Plus 360 Degrees
|
|	Permission is hereby granted, free of charge, to any person obtaining a copy
|	of this software and associated documentation files (the "Software"), to deal
|	in the Software without restriction, including without limitation the rights
|	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
|	copies of the Software, and to permit persons to whom the Software is
|	furnished to do so, subject to the following conditions:
|
|	The above copyright notice and this permission notice shall be included in
|	all copies or substantial portions of the Software.
|
|	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
|	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
|	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
|	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
|	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
|	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
|	THE SOFTWARE.
|------------------------------------------------------------------------------------------
*/

var PLUS360DEGREES = PLUS360DEGREES || { MAYOR_VERSION:'0', MINOR_VERSION:'4', REVISION:'5' };

var touchSupport = 'ontouchstart' in window.document ? true : false;
var prefixes = [ 'webkit', 'moz', 'ms', 'o', '' ];

/**
*	browser vendor and support identification class.
*
*	@param appName: String
*	@param mayorVersion: Number
*	@param minorVersion: Number
*	@param revision: Number
*	@param printInfo: Boolean
*
**/
PLUS360DEGREES.IdentifyBrowser = function( appName, mayorVersion, minorVersion, revision )
{
	
	this.applicationName = appName || "My App Name",
	this.mayorVersion = mayorVersion || 0,
	this.minorVersion = minorVersion || 0,
	this.revision = revision || 0,
	this.fullscreenSupport = null,
	this.webglSupport = null,
	this.clickEvent = null,
	this.startEvent = null,
	this.moveEvent = null,
	this.endEvent = null,
	this.windowHiddenEvent = null,
	this.domElement = null,
	this.inFullscreen = false;

	this.dataBrowser = [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera",
			versionSearch: "Version"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 	
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		} 
	];

	this.dataOS = [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			string: navigator.userAgent,
			subString: "iPhone",
			identity: "iPhone/iPod"
	    },
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	];

	this.mobile = function()
	{
		return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
	}

	this.init = function()
	{
		this.browser = this.searchString( this.dataBrowser ) || "An unknown browser";
		this.version = this.searchVersion( navigator.userAgent )
			|| this.searchVersion( navigator.appVersion )
			|| "an unknown version";
		this.operatingSystem = this.searchString( this.dataOS ) || "an unknown Operating System";

		this.clickEvent = touchSupport ? TouchEvent.TOUCH_START : MouseEvent.CLICK;
		this.startEvent = touchSupport ? TouchEvent.TOUCH_START : MouseEvent.MOUSE_DOWN;
		this.moveEvent = touchSupport ? TouchEvent.TOUCH_MOVE : MouseEvent.MOUSE_MOVE;
		this.endEvent = touchSupport ? TouchEvent.TOUCH_END : MouseEvent.MOUSE_UP;
		this.windowHiddenEvent = this.getHiddenProperty().replace(/[H|h]idden/,'') + 'visibilitychange';

		this.getWebglSupport();
	}

	this.specifications = function( name, mayorVersion, minorVersion, revision )
	{
		this.applicationName = name;
		this.mayorVersion = mayorVersion;
		this.minorVersion = minorVersion;
		this.revision = revision;
	}

	this.searchString = function( data )
	{
		for( var i = 0; i < data.length; i++ )
		{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if( dataString ) {
				if( dataString.indexOf( data[i].subString ) != -1 )
					return data[i].identity;
			}
			else if( dataProp )
				return data[i].identity;
		}
	}

	this.searchVersion = function( dataString )
	{
		var index = dataString.indexOf( this.versionSearchString );
		if (index == -1) return;
		return parseFloat( dataString.substring( index + this.versionSearchString.length + 1 ) );
	}

	this.getWebglSupport = function()
	{
		try 
		{ 
			this.webglSupport = !! window.WebGLRenderingContext && !! document.createElement( 'canvas' ).getContext( 'experimental-webgl' );
		} 
		catch( error ) 
		{ 
			return false; 
		}
	}
	//TODO add webgl message
	this.addWebglMessage = function( domElement )
	{
		
	}

	this.toggleFullscreen = function( domElement )
	{
		this.domElement = domElement === undefined ? document.body : domElement;

		if( document.fullscreenEnabled || 
			document.webkitFullscreenEnabled || 
			document.msFullscreenEnabled || 
			document.mozFullScreenEnabled) 
		{
			if( !document.fullscreenElement && 
				!document.webkitFullscreenElement && 
				!document.msFullscreenElement && 
				!document.mozFullScreenElement)
			{
				if( this.domElement.requestFullscreen )
				{
					this.domElement.requestFullscreen();
				}
				else if( this.domElement.webkitRequestFullscreen )
				{
					this.domElement.webkitRequestFullscreen();
				}
				else if( this.domElement.msRequestFullscreen )
				{
					this.domElement.msRequestFullscreen();
				}
				else if( this.domElement.mozRequestFullScreen )
				{
					this.domElement.mozRequestFullScreen();
				}
				this.inFullscreen = true;
				return;
			} else {
				if( document.exitFullscreen )
				{
					document.exitFullscreen();
				}
				else if( document.webkitExitFullscreen )
				{
					document.webkitExitFullscreen();
				}
				else if( document.msExitFullscreen )
				{
					document.msExitFullscreen();
				}
				else if( document.mozCancelFullScreen )
				{
					document.mozCancelFullScreen();
				}
				this.inFullscreen = false;
				return;
			}
		} 
		else 
		{
			alert( "Your browser doesn’t support the fullscreen API" );
		}
	}

	this.enabledFullscreen = function( domElement )
	{
		if( domElement.requestFullscreen )
		{
			domElement.requestFullscreen();
		}
		else if( domElement.webkitRequestFullscreen )
		{
			domElement.webkitRequestFullscreen();
		}
		else if( domElement.msRequestFullscreen )
		{
			domElement.msRequestFullscreen();
		}
		else if( domElement.mozRequestFullScreen )
		{
			domElement.mozRequestFullScreen();
		}
		this.inFullscreen = true;
		return;
	}

	this.exitFullscreen = function()
	{
		if( document.exitFullscreen )
		{
			document.exitFullscreen();
		}
		else if( document.webkitExitFullscreen )
		{
			document.webkitExitFullscreen();
		}
		else if( document.msExitFullscreen )
		{
			document.msExitFullscreen();
		}
		else if( document.mozCancelFullScreen )
		{
			document.mozCancelFullScreen();
		}
		this.inFullscreen = false;
		return;
	}

	this.windowHidden = function()
	{
		return document[ this.getHiddenProperty() ] || false;
	}

	this.getIE = function()
	{
	  var rv = -1;
	  if( navigator.appName == 'Microsoft Internet Explorer' )
	  {
	    var ua = navigator.userAgent;
	    var re = new RegExp( "MSIE ([0-9]{1,}[\.0-9]{0,})" );
	    if (re.exec(ua) != null)
	      rv = parseFloat( RegExp.$1 );
	  }
	  else if( navigator.appName == 'Netscape' )
	  {
	    var ua = navigator.userAgent;
	    var re = new RegExp( "Trident/.*rv:([0-9]{1,}[\.0-9]{0,})" );
	    if( re.exec( ua ) != null )
	      rv = parseFloat( RegExp.$1 );
	  }
	  return rv;
	}

	this.getHiddenProperty = function()
	{
		if( 'hidden' in document ) return 'hidden';
    
		for( var i = 0; i < prefixes.length; i++ )
		{
			if( ( prefixes[i] + 'Hidden' ) in document ) 
				return prefixes[i] + 'Hidden';
		}
		return null;
	}

	this.init();
}

PLUS360DEGREES.DOM = function(){}

PLUS360DEGREES.DOM.div = function( id )
{
	if( document.getElementById( id ) == null )
	{
		var _div = document.createElement( 'div' );
		_div.id = id;
		return _div;
	} else {
		return document.getElementById( id );
	}
}

PLUS360DEGREES.DOM.canvas = function( id )
{
	if( document.getElementById( id ) == null )
	{
		var _canvas = document.createElement( 'canvas' );
		_canvas.id = id;
		return _canvas;
	} else {
		return document.getElementById( id );
	}
}

PLUS360DEGREES.DOM.element = function( domElementName, id )
{
	var _element = document.createElement( domElementName );
	_element.id = id;
	return _element;
}

PLUS360DEGREES.Utils = function(){};

PLUS360DEGREES.Utils.randomNumber = function( from, to )
{
	return Math.floor( Math.random() * ( to - from + 1 ) + from );
}

PLUS360DEGREES.MeshUtils = function(){}

PLUS360DEGREES.MeshUtils.loadMesh = function( geometry, material )
{
	var mesh = new THREE.Mesh( geometry, material );
	return mesh;
}

PLUS360DEGREES.MeshUtils.toggleObject3D = function( object3d, visible )
{
	object3d.traverse( function( object ) { object.visible = visible; } );
}

PLUS360DEGREES.TextureUtils = function(){}

PLUS360DEGREES.TextureUtils.loadTexture = function( path, anisotropy, callback )
{
	var texture = THREE.ImageUtils.loadTexture( path, undefined, callback );
	texture.anisotropy = anisotropy || 8;
	return texture;
}

PLUS360DEGREES.TextureUtils.loadRepeatableTexture = function( path, repeatU, repeatV, anisotropy, callback )
{
	var texture = THREE.ImageUtils.loadTexture( path, undefined, callback );
	texture.anisotropy = anisotropy || 8;
	texture.wrapS = texture.wrapT = THREE.RepeatWrapping;
	texture.repeat.set( repeatU, repeatV );
	return texture;
}

PLUS360DEGREES.TextureUtils.loadCubeTextures = function( textures, anisotropy, callback )
{
	var _textures = THREE.ImageUtils.loadTextureCube( textures, undefined, callback );
	_textures.format = THREE.RGBFormat;
	_textures.anisotropy = anisotropy || 0;
	return _textures;
}

var Keyboard = { A:65,
					B:66,
					C:67,
					D:68,
					E:69,
					F:70,
					G:71,
					H:72,
					I:73,
					J:74,
					K:75,
					L:76,
					M:77,
					N:78,
					O:79,
					P:80,
					Q:81,
					R:82,
					S:83,
					T:84,
					U:85,
					V:86,
					W:87,
					X:88,
					Y:89,
					Z:90,
					NUMBER_0:48,
					NUMBER_1:49,
					NUMBER_2:50,
					NUMBER_3:51,
					NUMBER_4:52,
					NUMBER_5:53,
					NUMBER_6:54,
					NUMBER_7:55,
					NUMBER_8:56,
					NUMBER_9:57,
					NUMPAD_0:45,
					NUMPAD_1:35,
					NUMPAD_2:40,
					NUMPAD_3:34,
					NUMPAD_4:37,
					NUMPAD_5:12,
					NUMPAD_6:39,
					NUMPAD_7:36,
					NUMPAD_8:38,
					NUMPAD_9:33,
					NUMPAD_ADD:107,
					NUMPAD_SUBTRACT:109,
					NUMPAD_MULTIPLY:106,
					NUMPAD_DECIMAL:110,
					NUMPAD_DIVIDE:111,
					NUMPAD_ENTER:13,
					UP:38,
					DOWN:40,
					LEFT:37,
					RIGHT:39,
					PAGE_UP:33,
					PAGE_DOWN:34,
					ESCAPE:27,
					ENTER:13,
					SHIFT:16,
					CONTROL:17,
					ALT:18,
					INSERT:45,
					DELETE:46,
					HOME:36,
					END:35,
					NUMBER_PAD_LOCK:144,
					BACKSPACE:8,
					F1:112,
					F2:113,
					F3:114,
					F4:115,
					F5:116,
					F6:117,
					F7:118,
					F8:119,
					F9:120,
					F10:121,
					F11:122,
					F12:123 };

var MouseEvent = { CLICK:'click',
				   DOUBLE_CLICK:'dblclick',
 				   MOUSE_OVER:'mouseover', 
				   MOUSE_OUT:'mouseout',
				   MOUSE_ENTER:'mouseenter',
				   MOUSE_LEAVE:'mouseleave',
				   ROLL_OVER:'mouseenter',
				   ROLL_OUT:'mouseleave', 
				   MOUSE_DOWN:'mousedown',
				   MOUSE_UP:'mouseup',
				   MOUSE_MOVE:'mousemove',
				   WHEEL:'wheel',
				   MOUSE_WHEEL:'mousewheel',
				   DOM_MOUSE_SCROLL:'DOMMouseScroll',
				   DRAG:'drag',
				   DRAG_START:'dragstart',
				   DRAG_END:'dragend',
				   DRAG_ENTER:'dragenter',
				   DRAG_EXIT:'dragexit',
				   DRAG_OVER:'dragover',
				   DROP:'drop' };

var TouchEvent = { TOUCH_START:'touchstart',
				   TOUCH_END:'touchend',
				   TOUCH_MOVE:'touchmove',
				   TOUCH_CANCEL:'touchcancel' };

var Event = { EVENT:'Event',
			  LOAD:'load',
			  LOAD_START:'loadstart',
			  BEFORE_UNLOAD:'beforeunload',
			  UNLOAD:'unload',
			  LOADED_DATA:'loadeddata',
			  LOADED_METADATA:'loadedmetadata',
			  DOM_ACTIVE:'DOMActive',
			  ABORT:'abort',
			  ERROR:'error',
			  CANCEL:'cancel',
			  CLOSE:'close',
			  SELECT:'select',
			  RESIZE:'resize',
			  SCROLL:'scroll',
			  EMPTIED:'emptied',
			  DURATION_CHANGE:'durationchange',
			  INPUT:'input',
			  AFTER_PRINT:'afterprint',
			  BEFORE_PRINT:'beforeprint',
			  HASH_CHANGE:'hashchange',
			  MESSAGE:'message',
			  OFFLINE:'offline',
			  ONLINE:'online',
			  PAGE_HIDE:'pagehide',
			  PAGE_SHOW:'pageshow',
			  POP_STATE:'popstate',
			  STORAGE:'storage',
			  CONTEXT_MENU:'contextmenu',
			  BLUR:'blur',
			  DOM_FOCUS_IN:'DOMFocusIn',
			  DOM_FOCUS_OUT:'DOMFocusOut',
			  FOCUS:'focus',
			  FOCUS_IN:'focusin',
			  FOCUS_OUT:'focusout' };

var KeyboardEvent = { KEY_DOWN:'keydown',
					  KEY_UP:'keyup',
					  KEY_PRESS:'keypress' };

var FocusEvent = {  };

var CompositionEvent = { COMPOSITION_START:'compositionstart',
						 COMPOSITION_UPDATE:'compositionupdate',
						 COMPOSITION_END:'compositionend' };

var MutationEvent = { DOM_ATTR_MODIFIED:'DOMAttrModiified',
					  DOM_CHARACTER_DATA_MODIFIED:'DOMCharacterDataModified',
					  DOM_NODE_INSERTEED:'DOMNodeInserted',
					  DOM_NODE_INSERTEED_INTO_DOCUMENT:'DOMNodeInsertedIntoDocument',
					  DOM_NODE_REMOVED:'DOMNodeRemoved',
					  DOM_NODE_REMOVED_FROM_DOCUMENT:'DOMNodeRemovedFromDocument',
					  DOM_SUBTREE_MODIFIED:'DOMSubtreeModified' };

var AudioEvent = { PLAY:'play',
				   STOP:'stop',
				   PAUSE:'pause',
				   REWIND:'rewind',
				   FORWARD:'forward',
				   SEEK:'seek' };

var VideoEvent = { PLAY:'play',
				   STOP:'stop',
				   PAUSE:'pause',
				   REWIND:'rewind',
				   FORWARD:'forward',
				   SEEK:'seek' };

var Directions = { UP:'up', 
				   DOWN:'down', 
				   LEFT:'left', 
				   RIGHT:'right',
				   FRONT:'front',
				   BACK:'back',
				   CENTER:'center' };

THREE.OrbitControls = function ( object, domElement ) {

	this.object = object;
	this.domElement = ( domElement !== undefined ) ? domElement : document.body;

	// API

	// Set to false to disable this control
	this.enabled = true;

	// "target" sets the location of focus, where the control orbits around
	// and where it pans with respect to.
	this.target = new THREE.Vector3();

	// center is old, deprecated; use "target" instead
	this.center = this.target;

	// This option actually enables dollying in and out; left as "zoom" for
	// backwards compatibility
	this.noZoom = false;
	this.zoomSpeed = 0.3;

	// Limits to how far you can dolly in and out
	this.minDistance = 0;
	this.maxDistance = Infinity;

	// Set to true to disable this control
	this.noRotate = false;
	this.rotateSpeed = 0.3;

	// Set to true to disable this control
	this.noPan = true;
	this.keyPanSpeed = 7.0;	// pixels moved per arrow key push

	// Set to true to automatically rotate around the target
	this.autoRotate = false;
	this.autoRotateSpeed = 1.0; // 30 seconds per round when fps is 60
	this.phiRotationSpeed = 1.0;

	this.autoRotateDirection = Directions.RIGHT;
	this.enabledAutoRotatePhi = false;

	// How far you can orbit vertically, upper and lower limits.
	// Range is 0 to Math.PI radians.
	this.minPolarAngle = 0; // radians
	this.maxPolarAngle = Math.PI; // radians

	this.constraintPan = false;
	this.minPanAngle = 0;// radians
	this.maxPanAngle = Math.PI; //radians

	// Set to true to disable use of the keys
	this.noKeys = true;

	// The four arrow keys
	this.keys = { LEFT: 37, UP: 38, RIGHT: 39, BOTTOM: 40 };

	this.deceleration = 0.8;

	////////////
	// internals

	var scope = this;

	var EPS = 0.000001;

	var rotateStart = new THREE.Vector2();
	var rotateEnd = new THREE.Vector2();
	var rotateDelta = new THREE.Vector2();

	var panStart = new THREE.Vector2();
	var panEnd = new THREE.Vector2();
	var panDelta = new THREE.Vector2();
	var panOffset = new THREE.Vector3();

	var offset = new THREE.Vector3();

	var dollyStart = new THREE.Vector2();
	var dollyEnd = new THREE.Vector2();
	var dollyDelta = new THREE.Vector2();

	var phiDelta = 0;
	var thetaDelta = 0;
	var scale = 1;
	var pan = new THREE.Vector3();

	var lastPosition = new THREE.Vector3();

	var STATE = { NONE : -1, ROTATE : 0, DOLLY : 1, PAN : 2, TOUCH_ROTATE : 3, TOUCH_DOLLY : 4, TOUCH_PAN : 5 };

	var state = STATE.NONE;

	this.light = undefined;

	// for reset

	this.target0 = this.target.clone();
	this.position0 = this.object.position.clone();

	// so camera.up is the orbit axis

	var quat = new THREE.Quaternion().setFromUnitVectors( object.up, new THREE.Vector3( 0, 1, 0 ) );
	var quatInverse = quat.clone().inverse();

	// events

	var changeEvent = { type: 'change' };
	var startEvent = { type: 'start'};
	var endEvent = { type: 'end'};

	//auto rotate 
	var dirPhi = Directions.UP;
	var isDown = false;

	this.rotateTheta = function ( angle ) {

		if ( angle === undefined ) {

			angle = getAutoRotationAngle();

		}

		thetaDelta -= angle;

	};

	this.updateTheta = function( angle )
	{
		if( scope.autoRotate && isDown )
		{
			if( scope.autoRotateDirection == Directions.RIGHT )
			{
				thetaDelta -= angle;
			}

			if( scope.autoRotateDirection == Directions.LEFT )
			{
				thetaDelta += angle;
			}
		}
		else if( scope.autoRotate )
		{
			if( scope.autoRotateDirection == Directions.RIGHT )
			{
				thetaDelta += angle;
			}
/*
			if( scope.autoRotateDirection == Directions.LEFT )
			{
				thetaDelta -= angle;
			}
*/		}
		
		if( scope.autoRotateDirection == Directions.LEFT || !scope.autoRotate )
		{
			thetaDelta += angle;
		}
	}

	this.rotatePhi = function ( angle ) {

		if ( angle === undefined ) {

			angle = getAutoRotationAngle();

		}

		phiDelta -= angle;

	};

	this.updatePhi = function( angle, phi )
	{
		if( scope.enabledAutoRotatePhi && !isDown ){

			if( phi < scope.maxPolarAngle-0.01 && dirPhi == Directions.DOWN )
			{
				phiDelta += ( angle * scope.phiRotationSpeed );
			}
			else if( phi > scope.maxPolarAngle-0.02 && dirPhi == Directions.DOWN )
			{
				dirPhi = Directions.UP;
			}
			else if( phi > scope.minPolarAngle+0.01 && dirPhi == Directions.UP )
			{
				phiDelta -= ( angle * scope.phiRotationSpeed );
			}
			else if( phi < scope.minPolarAngle+0.02 && dirPhi == Directions.UP )
			{
				dirPhi = Directions.DOWN;
			}
		}
		else {
			phiDelta -= angle;
		}
	}

	// pass in distance in world space to move left
	this.panLeft = function ( distance ) {

		var te = this.object.matrix.elements;

		// get X column of matrix
		panOffset.set( te[ 0 ], te[ 1 ], te[ 2 ] );
		panOffset.multiplyScalar( - distance );
		
		pan.add( panOffset );

	};

	// pass in distance in world space to move up
	this.panUp = function ( distance ) {

		var te = this.object.matrix.elements;

		// get Y column of matrix
		panOffset.set( te[ 4 ], te[ 5 ], te[ 6 ] );
		panOffset.multiplyScalar( distance );
		
		pan.add( panOffset );

	};
	
	// pass in x,y of change desired in pixel space,
	// right and down are positive
	this.pan = function ( deltaX, deltaY ) {

		var element = scope.domElement === document ? scope.domElement.body : scope.domElement;

		if ( scope.object.fov !== undefined ) {

			// perspective
			var position = scope.object.position;
			var offset = position.clone().sub( scope.target );
			var targetDistance = offset.length();

			// half of the fov is center to top of screen
			targetDistance *= Math.tan( ( scope.object.fov / 2 ) * Math.PI / 180.0 );

			// we actually don't use screenWidth, since perspective camera is fixed to screen height
			scope.panLeft( 2 * deltaX * targetDistance / element.clientHeight );
			scope.panUp( 2 * deltaY * targetDistance / element.clientHeight );

		} else if ( scope.object.top !== undefined ) {

			// orthographic
			scope.panLeft( deltaX * (scope.object.right - scope.object.left) / element.clientWidth );
			scope.panUp( deltaY * (scope.object.top - scope.object.bottom) / element.clientHeight );

		} else {

			// camera neither orthographic or perspective
			console.warn( 'WARNING: OrbitControls.js encountered an unknown camera type - pan disabled.' );

		}

	};

	this.dollyIn = function ( dollyScale ) {

		if ( dollyScale === undefined ) {

			dollyScale = getZoomScale();

		}

		scale /= dollyScale;

	};

	this.dollyOut = function ( dollyScale ) {

		if ( dollyScale === undefined ) {

			dollyScale = getZoomScale();

		}

		scale *= dollyScale;

	};

	this.addLight = function( light )
	{
		this.light = light;
	}

	this.update = function () {

		var position = this.object.position;

		offset.copy( position ).sub( this.target );

		// rotate offset to "y-axis-is-up" space
		offset.applyQuaternion( quat );

		// angle from z-axis around y-axis

		var theta = Math.atan2( offset.x, offset.z );

		// angle from y-axis

		var phi = Math.atan2( Math.sqrt( offset.x * offset.x + offset.z * offset.z ), offset.y );

		if ( this.autoRotate ) {

			if( !isDown )
			{
				this.updateTheta( getAutoRotationAngle() );

				if( this.enabledAutoRotatePhi ) {
					this.updatePhi( getAutoRotationAngle(), phi );
				}
			}
		}

		theta += thetaDelta;
		phi += phiDelta;

		thetaDelta *= this.deceleration;
		phiDelta *= this.deceleration;

		// restrict phi to be between desired limits
		phi = Math.max( this.minPolarAngle, Math.min( this.maxPolarAngle, phi ) );

		// restrict phi to be betwee EPS and PI-EPS
		phi = Math.max( EPS, Math.min( Math.PI - EPS, phi ) );

		var radius = offset.length() * scale;

		if( this.constraintPan ) {
			theta = Math.max( this.minPanAngle, Math.min( this.maxPanAngle, theta ) );
		}

		// restrict radius to be between desired limits
		radius = Math.max( this.minDistance, Math.min( this.maxDistance, radius ) );
		
		// move target to panned location
		this.target.add( pan );

		offset.x = radius * Math.sin( phi ) * Math.sin( theta );
		offset.y = radius * Math.cos( phi );
		offset.z = radius * Math.sin( phi ) * Math.cos( theta );

		// rotate offset back to "camera-up-vector-is-up" space
		offset.applyQuaternion( quatInverse );

		position.copy( this.target ).add( offset );

		this.object.lookAt( this.target );

		if( this.light ) {
			this.light.position = position;
		}

//		thetaDelta = 0;
//		phiDelta = 0;
		scale = 1;
		pan.set( 0, 0, 0 );

		if ( lastPosition.distanceToSquared( this.object.position ) > EPS ) {

			this.dispatchEvent( changeEvent );

			lastPosition.copy( this.object.position );

		}

	};

	this.reset = function () {

		state = STATE.NONE;

		this.target.copy( this.target0 );
		this.object.position.copy( this.position0 );

		this.update();

	};

	function getAutoRotationAngle() {

		return 2 * Math.PI / 60 / 60 * scope.autoRotateSpeed;

	}

	function getZoomScale() {

		return Math.pow( 0.95, scope.zoomSpeed );

	}

	function onMouseDown( event ) {

		if ( scope.enabled === false ) return;
		event.preventDefault();

		if ( event.button === 0 ) {
			if ( scope.noRotate === true ) return;

			state = STATE.ROTATE;

			rotateStart.set( event.clientX, event.clientY );

		} else if ( event.button === 1 ) {
			if ( scope.noZoom === true ) return;

			state = STATE.DOLLY;

			dollyStart.set( event.clientX, event.clientY );

		} else if ( event.button === 2 ) {
			if ( scope.noPan === true ) return;

			state = STATE.PAN;

			panStart.set( event.clientX, event.clientY );

		}
		isDown = true;
//		scope.domElement.addEventListener( 'mousemove', onMouseMove, false );
//		scope.domElement.addEventListener( 'mouseup', onMouseUp, false );
		scope.dispatchEvent( startEvent );

	}

	function onMouseMove( event ) {

		if ( scope.enabled === false ) return;

		event.preventDefault();

		var element = scope.domElement === document ? scope.domElement.body : scope.domElement;

		if ( state === STATE.ROTATE ) {

			if ( scope.noRotate === true ) return;

			rotateEnd.set( event.clientX, event.clientY );
			rotateDelta.subVectors( rotateEnd, rotateStart );

			// rotating across whole screen goes 360 degrees around
			scope.rotateTheta( 2 * Math.PI * rotateDelta.x / element.clientWidth * scope.rotateSpeed );

			// rotating up and down along whole screen attempts to go 360, but limited to 180
			scope.rotatePhi( 2 * Math.PI * rotateDelta.y / element.clientHeight * scope.rotateSpeed );

			rotateStart.copy( rotateEnd );

		} else if ( state === STATE.DOLLY ) {

			if ( scope.noZoom === true ) return;

			dollyEnd.set( event.clientX, event.clientY );
			dollyDelta.subVectors( dollyEnd, dollyStart );

			if ( dollyDelta.y > 0 ) {

				scope.dollyIn();

			} else {

				scope.dollyOut();

			}

			dollyStart.copy( dollyEnd );

		} else if ( state === STATE.PAN ) {

			if ( scope.noPan === true ) return;

			panEnd.set( event.clientX, event.clientY );
			panDelta.subVectors( panEnd, panStart );
			
			scope.pan( panDelta.x, panDelta.y );

			panStart.copy( panEnd );
		}

//		scope.update();

	}

	function onMouseUp( event ) {

		if ( scope.enabled === false ) return;

		scope.dispatchEvent( endEvent );
		state = STATE.NONE;

		isDown = false;

	}

	function onMouseWheel( event ) {

		if ( scope.enabled === false || scope.noZoom === true ) return;

		event.preventDefault();
		event.stopPropagation();

		var delta = 0;

		if ( event.wheelDelta !== undefined ) { // WebKit / Opera / Explorer 9

			delta = event.wheelDelta;

		} else if ( event.detail !== undefined ) { // Firefox

			delta = - event.detail;

		}

		if ( delta > 0 ) {

			scope.dollyOut();

		} else {

			scope.dollyIn();

		}

//		scope.update();
		scope.dispatchEvent( startEvent );
		scope.dispatchEvent( endEvent );

	}

	function onKeyDown( event ) {

		if ( scope.enabled === false || scope.noKeys === true || scope.noPan === true ) return;
		
		switch ( event.keyCode ) {

			case scope.keys.UP:
				scope.pan( 0, scope.keyPanSpeed );
				scope.update();
				break;

			case scope.keys.BOTTOM:
				scope.pan( 0, - scope.keyPanSpeed );
				scope.update();
				break;

			case scope.keys.LEFT:
				scope.pan( scope.keyPanSpeed, 0 );
				scope.update();
				break;

			case scope.keys.RIGHT:
				scope.pan( - scope.keyPanSpeed, 0 );
				scope.update();
				break;

		}

	}

	function touchstart( event ) {

		if ( scope.enabled === false ) return;

		switch ( event.touches.length ) {

			case 1:	// one-fingered touch: rotate

				if ( scope.noRotate === true ) return;

				state = STATE.TOUCH_ROTATE;

				rotateStart.set( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY );
				break;

			case 2:	// two-fingered touch: dolly

				if ( scope.noZoom === true ) return;

				state = STATE.TOUCH_DOLLY;

				var dx = event.touches[ 0 ].pageX - event.touches[ 1 ].pageX;
				var dy = event.touches[ 0 ].pageY - event.touches[ 1 ].pageY;
				var distance = Math.sqrt( dx * dx + dy * dy );
				dollyStart.set( 0, distance );
				break;

			case 3: // three-fingered touch: pan

				if ( scope.noPan === true ) return;

				state = STATE.TOUCH_PAN;

				panStart.set( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY );
				break;

			default:

				state = STATE.NONE;

		}

		scope.dispatchEvent( startEvent );

	}

	function touchmove( event ) {

		if ( scope.enabled === false ) return;

		event.preventDefault();
		event.stopPropagation();

		var element = scope.domElement === document ? scope.domElement.body : scope.domElement;

		switch ( event.touches.length ) {

			case 1: // one-fingered touch: rotate

				if ( scope.noRotate === true ) return;
				if ( state !== STATE.TOUCH_ROTATE ) return;

				rotateEnd.set( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY );
				rotateDelta.subVectors( rotateEnd, rotateStart );

				// rotating across whole screen goes 360 degrees around
				scope.rotateTheta( 2 * Math.PI * rotateDelta.x / element.clientWidth * scope.rotateSpeed );
				// rotating up and down along whole screen attempts to go 360, but limited to 180
				scope.rotatePhi( 2 * Math.PI * rotateDelta.y / element.clientHeight * scope.rotateSpeed );

				rotateStart.copy( rotateEnd );

				scope.update();
				break;

			case 2: // two-fingered touch: dolly

				if ( scope.noZoom === true ) return;
				if ( state !== STATE.TOUCH_DOLLY ) return;

				var dx = event.touches[ 0 ].pageX - event.touches[ 1 ].pageX;
				var dy = event.touches[ 0 ].pageY - event.touches[ 1 ].pageY;
				var distance = Math.sqrt( dx * dx + dy * dy );

				dollyEnd.set( 0, distance );
				dollyDelta.subVectors( dollyEnd, dollyStart );

				if ( dollyDelta.y > 0 ) {

					scope.dollyOut();

				} else {

					scope.dollyIn();

				}

				dollyStart.copy( dollyEnd );

				scope.update();
				break;

			case 3: // three-fingered touch: pan

				if ( scope.noPan === true ) return;
				if ( state !== STATE.TOUCH_PAN ) return;

				panEnd.set( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY );
				panDelta.subVectors( panEnd, panStart );
				
				scope.pan( panDelta.x, panDelta.y );

				panStart.copy( panEnd );

				scope.update();
				break;

			default:

				state = STATE.NONE;

		}

	}

	function touchend( /* event */ ) {

		if ( scope.enabled === false ) return;

		scope.dispatchEvent( endEvent );
		state = STATE.NONE;

	}

	this.enabledAll = function( value )
	{
		if( value )
		{
			this.domElement.addEventListener( 'mousedown', onMouseDown, false );
			this.domElement.addEventListener( 'mousewheel', onMouseWheel, false );
			this.domElement.addEventListener( 'DOMMouseScroll', onMouseWheel, false ); // firefox
			this.domElement.addEventListener( 'mousemove', onMouseMove, false );
			this.domElement.addEventListener( 'mouseup', onMouseUp, false );

			this.domElement.addEventListener( 'touchstart', touchstart, false );
			this.domElement.addEventListener( 'touchend', touchend, false );
			this.domElement.addEventListener( 'touchmove', touchmove, false );
			window.addEventListener( 'keydown', onKeyDown, false );
			this.noRotate = false;
			this.noZoom = false;
		}
		else
		{
			this.domElement.removeEventListener( 'mousedown', onMouseDown, false );
			this.domElement.removeEventListener( 'mousewheel', onMouseWheel, false );
			this.domElement.removeEventListener( 'DOMMouseScroll', onMouseWheel, false ); // firefox
			this.domElement.removeEventListener( 'mousemove', onMouseMove, false );
			this.domElement.removeEventListener( 'mouseup', onMouseUp, false );

			this.domElement.removeEventListener( 'touchstart', touchstart, false );
			this.domElement.removeEventListener( 'touchend', touchend, false );
			this.domElement.removeEventListener( 'touchmove', touchmove, false );
			window.removeEventListener( 'keydown', onKeyDown, false );
			this.noRotate = true;
			this.noZoom = true;
		}
		this.enabled = value;
		
	}

	
	this.domElement.addEventListener( 'contextmenu', function ( event ) { event.preventDefault(); }, false );
	
	// force an update at start
	this.update();
	this.enabledAll( true );

};

THREE.OrbitControls.prototype = Object.create( THREE.EventDispatcher.prototype );

THREE.ShaderLib['mirror'] = {

	uniforms: { "mirrorColor": { type: "c", value: new THREE.Color(0xeeeeee) },
				"mirrorSampler": { type: "t", value: null },
				"textureMatrix" : { type: "m4", value: new THREE.Matrix4() },
				"opacity": { type:"f", value: 1 }
	},

	vertexShader: [

		"uniform mat4 textureMatrix;",

		"varying vec4 mirrorCoord;",

		"void main() {",

			"vec4 mvPosition = modelViewMatrix * vec4( position, 1.0 );",
			"vec4 worldPosition = modelMatrix * vec4( position, 1.0 );",
			"mirrorCoord = textureMatrix * worldPosition;",

			"gl_Position = projectionMatrix * mvPosition;",

		"}"

	].join("\n"),

	fragmentShader: [

		"uniform vec3 mirrorColor;",
		"uniform sampler2D mirrorSampler;",
		"uniform float opacity;",

		"varying vec4 mirrorCoord;",

		"float blendOverlay(float base, float blend) {",
			"return( base < 0.5 ? ( 2.0 * base * blend ) : (1.0 - 2.0 * ( 1.0 - base ) * ( 1.0 - blend ) ) );",
		"}",
		
		"void main() {",

			"vec4 color = texture2DProj(mirrorSampler, mirrorCoord);",
			"color = vec4(blendOverlay(mirrorColor.r, color.r), blendOverlay(mirrorColor.g, color.g), blendOverlay(mirrorColor.b, color.b), opacity);",

			"gl_FragColor = color;",

		"}"

	].join("\n")

};

THREE.Mirror = function( renderer, camera, options ) {

	THREE.Object3D.call( this );

	this.name = 'mirror_' + this.id;

	options = options || {};

	this.matrixNeedsUpdate = true;

	var width = options.textureWidth !== undefined ? options.textureWidth : 512;
	var height = options.textureHeight !== undefined ? options.textureHeight : 512;

	this.clipBias = options.clipBias !== undefined ? options.clipBias : 0.0;

	var mirrorColor = options.color !== undefined ? new THREE.Color(options.color) : new THREE.Color(0x7F7F7F);

	this.renderer = renderer;
	this.mirrorPlane = new THREE.Plane();
	this.normal = new THREE.Vector3( 0, 0, 1 );
	this.mirrorWorldPosition = new THREE.Vector3();
	this.cameraWorldPosition = new THREE.Vector3();
	this.rotationMatrix = new THREE.Matrix4();
	this.lookAtPosition = new THREE.Vector3(0, 0, -1);
	this.clipPlane = new THREE.Vector4();
	
	// For debug only, show the normal and plane of the mirror
	var debugMode = options.debugMode !== undefined ? options.debugMode : false;

	if ( debugMode ) {

		var arrow = new THREE.ArrowHelper(new THREE.Vector3( 0, 0, 1 ), new THREE.Vector3( 0, 0, 0 ), 10, 0xffff80 );
		var planeGeometry = new THREE.Geometry();
		planeGeometry.vertices.push( new THREE.Vector3( -10, -10, 0 ) );
		planeGeometry.vertices.push( new THREE.Vector3( 10, -10, 0 ) );
		planeGeometry.vertices.push( new THREE.Vector3( 10, 10, 0 ) );
		planeGeometry.vertices.push( new THREE.Vector3( -10, 10, 0 ) );
		planeGeometry.vertices.push( planeGeometry.vertices[0] );
		var plane = new THREE.Line( planeGeometry, new THREE.LineBasicMaterial( { color: 0xffff80 } ) );

		this.add(arrow);
		this.add(plane);

	}

	if ( camera instanceof THREE.PerspectiveCamera ) {

		this.camera = camera;

	} else {

		this.camera = new THREE.PerspectiveCamera();
		console.log( this.name + ': camera is not a Perspective Camera!' );

	}

	this.textureMatrix = new THREE.Matrix4();

	this.mirrorCamera = this.camera.clone();

	this.texture = new THREE.WebGLRenderTarget( width, height );
	this.tempTexture = new THREE.WebGLRenderTarget( width, height );

	var mirrorShader = THREE.ShaderLib[ "mirror" ];
	var mirrorUniforms = THREE.UniformsUtils.clone( mirrorShader.uniforms );

	this.material = new THREE.ShaderMaterial( {

		fragmentShader: mirrorShader.fragmentShader,
		vertexShader: mirrorShader.vertexShader,
		uniforms: mirrorUniforms

	} );

	this.material.uniforms.mirrorSampler.value = this.texture;
	this.material.uniforms.mirrorColor.value = mirrorColor;
	this.material.uniforms.textureMatrix.value = this.textureMatrix;

	if ( !THREE.Math.isPowerOfTwo(width) || !THREE.Math.isPowerOfTwo( height ) ) {

		this.texture.generateMipmaps = false;
		this.tempTexture.generateMipmaps = false;

	}

	this.updateTextureMatrix();
	this.render();

};

THREE.Mirror.prototype = Object.create( THREE.Object3D.prototype );

THREE.Mirror.prototype.renderWithMirror = function ( otherMirror ) {

	// update the mirror matrix to mirror the current view
	this.updateTextureMatrix();
	this.matrixNeedsUpdate = false;

	// set the camera of the other mirror so the mirrored view is the reference view
	var tempCamera = otherMirror.camera;
	otherMirror.camera = this.mirrorCamera;

	// render the other mirror in temp texture
	otherMirror.renderTemp();
	otherMirror.material.uniforms.mirrorSampler.value = otherMirror.tempTexture;

	// render the current mirror
	this.render();
	this.matrixNeedsUpdate = true;

	// restore material and camera of other mirror
	otherMirror.material.uniforms.mirrorSampler.value = otherMirror.texture;
	otherMirror.camera = tempCamera;

	// restore texture matrix of other mirror
	otherMirror.updateTextureMatrix();
};

THREE.Mirror.prototype.updateTextureMatrix = function () {

	var sign = THREE.Math.sign;

	this.updateMatrixWorld();
	this.camera.updateMatrixWorld();

	this.mirrorWorldPosition.setFromMatrixPosition( this.matrixWorld );
	this.cameraWorldPosition.setFromMatrixPosition( this.camera.matrixWorld );

	this.rotationMatrix.extractRotation( this.matrixWorld );

	this.normal.set( 0, 0, 1 );
	this.normal.applyMatrix4( this.rotationMatrix );

	var view = this.mirrorWorldPosition.clone().sub( this.cameraWorldPosition );
	view.reflect( this.normal ).negate();
	view.add( this.mirrorWorldPosition );

	this.rotationMatrix.extractRotation( this.camera.matrixWorld );

	this.lookAtPosition.set(0, 0, -1);
	this.lookAtPosition.applyMatrix4( this.rotationMatrix );
	this.lookAtPosition.add( this.cameraWorldPosition );

	var target = this.mirrorWorldPosition.clone().sub( this.lookAtPosition );
	target.reflect( this.normal ).negate();
	target.add( this.mirrorWorldPosition );

	this.up.set( 0, -1, 0 );
	this.up.applyMatrix4( this.rotationMatrix );
	this.up.reflect( this.normal ).negate();

	this.mirrorCamera.position.copy( view );
	this.mirrorCamera.up = this.up;
	this.mirrorCamera.lookAt( target );

	this.mirrorCamera.updateProjectionMatrix();
	this.mirrorCamera.updateMatrixWorld();
	this.mirrorCamera.matrixWorldInverse.getInverse( this.mirrorCamera.matrixWorld );

	// Update the texture matrix
	this.textureMatrix.set( 0.5, 0.0, 0.0, 0.5,
							0.0, 0.5, 0.0, 0.5,
							0.0, 0.0, 0.5, 0.5,
							0.0, 0.0, 0.0, 1.0 );
	this.textureMatrix.multiply( this.mirrorCamera.projectionMatrix );
	this.textureMatrix.multiply( this.mirrorCamera.matrixWorldInverse );

	// Now update projection matrix with new clip plane, implementing code from: http://www.terathon.com/code/oblique.html
	// Paper explaining this technique: http://www.terathon.com/lengyel/Lengyel-Oblique.pdf
	this.mirrorPlane.setFromNormalAndCoplanarPoint( this.normal, this.mirrorWorldPosition );
	this.mirrorPlane.applyMatrix4( this.mirrorCamera.matrixWorldInverse );

	this.clipPlane.set( this.mirrorPlane.normal.x, this.mirrorPlane.normal.y, this.mirrorPlane.normal.z, this.mirrorPlane.constant );

	var q = new THREE.Vector4();
	var projectionMatrix = this.mirrorCamera.projectionMatrix;

	q.x = ( sign(this.clipPlane.x) + projectionMatrix.elements[8] ) / projectionMatrix.elements[0];
	q.y = ( sign(this.clipPlane.y) + projectionMatrix.elements[9] ) / projectionMatrix.elements[5];
	q.z = - 1.0;
	q.w = ( 1.0 + projectionMatrix.elements[10] ) / projectionMatrix.elements[14];

	// Calculate the scaled plane vector
	var c = new THREE.Vector4();
	c = this.clipPlane.multiplyScalar( 2.0 / this.clipPlane.dot(q) );

	// Replacing the third row of the projection matrix
	projectionMatrix.elements[2] = c.x;
	projectionMatrix.elements[6] = c.y;
	projectionMatrix.elements[10] = c.z + 1.0 - this.clipBias;
	projectionMatrix.elements[14] = c.w;

};

THREE.Mirror.prototype.render = function () {

	if ( this.matrixNeedsUpdate ) this.updateTextureMatrix();

	this.matrixNeedsUpdate = true;

	// Render the mirrored view of the current scene into the target texture
	var scene = this;

	while ( scene.parent !== undefined ) {

		scene = scene.parent;

	}

	if ( scene !== undefined && scene instanceof THREE.Scene) {

		this.renderer.render( scene, this.mirrorCamera, this.texture, true );

	}

};

THREE.Mirror.prototype.renderTemp = function () {

	if ( this.matrixNeedsUpdate ) this.updateTextureMatrix();

	this.matrixNeedsUpdate = true;

	// Render the mirrored view of the current scene into the target texture
	var scene = this;

	while ( scene.parent !== undefined ) {

		scene = scene.parent;

	}

	if ( scene !== undefined && scene instanceof THREE.Scene) {

		this.renderer.render( scene, this.mirrorCamera, this.tempTexture, true );

	}

};

// IMPORTANT: iLuminosity elements from here. MIT LICENSE DOES NOT APPLY FOR THE CODE BELOW

PLUS360DEGREES.BodyMaterial = function()
{
	THREE.MeshPhongMaterial.call( this );

	this.color = new THREE.Color( 0x3e8d31 );
	this.specular = new THREE.Color( 0x090909 );
	this.shininess = 200;
}

PLUS360DEGREES.BodyMaterial.prototype = Object.create( THREE.MeshPhongMaterial.prototype );

PLUS360DEGREES.Lamp = function( bodyMaterial, callback )
{
	THREE.Object3D.call( this );
	var _this = this;

	var base,
		baseCover,
		a1, a1_pivot = new THREE.Object3D(),
		b1,
		c1,
		b2,	b2_pivot = new THREE.Object3D(),
		a2, a2_pivot = new THREE.Object3D(),
		b3,
		c2,
		b4, b4_pivot = new THREE.Object3D(),
		head, head_pivot = new THREE.Object3D(),
		headLight;

	var baseMaterial = new THREE.MeshPhongMaterial( {
		color: 0x232323,
		shininess: 400,
		specular: 0x222222
	} );

	var headLightMaterial = new THREE.MeshBasicMaterial( {
		color: 0xcccccc
	} );

	loader.load( "models/base.js", 
		function( geometry ) {
			base = PLUS360DEGREES.MeshUtils.loadMesh( geometry, baseMaterial );
			_this.add( base );

			loader.load( "models/base_cover.js", 
			function( geometry ){

				baseCover = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
				_this.add( baseCover );

				loader.load( "models/a1.js", 
				function( geometry ) {
					a1 = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
					a1_pivot.add( a1 );
					a1_pivot.position.set( 0, 39.94, 78.86 );
					baseCover.add( a1_pivot );
					_this.a1 = a1_pivot;

					loader.load( "models/b1.js", 
					function( geometry ) {
						b1 = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
						b1.position.set( 0, 189.94 - a1_pivot.position.y, 78.87 - a1_pivot.position.z );
						a1.add( b1 );

						loader.load( "models/c1.js", 
						function( geometry ) {
							c1 = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
							c1.position.set( 0, 211.35 - a1_pivot.position.y, 80.28 - a1_pivot.position.z );
							a1.add( c1 );

							loader.load( "models/b2.js", 
							function( geometry ) {
								b2 = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
								b2_pivot.position.set( 0, 42.83, 0 );
								b2_pivot.rotation.x = THREE.Math.degToRad( -45 );
								b2_pivot.add( b2 );
								c1.add( b2_pivot );
								_this.b2 = b2_pivot;

								loader.load( "models/a2.js", 
								function( geometry ) {
									a2 = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
									a2.position.set( 0, 21.37, -1.4 );
									a2_pivot.rotation.x = THREE.Math.degToRad( 45 );
									a2_pivot.add( a2 );
									b2.add( a2_pivot );

									loader.load( "models/b3.js", 
									function( geometry ) {
										b3 = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
										b3.position.set( 0, 171.37, -1.4 );
										a2_pivot.add( b3 );

										loader.load( "models/c2.js", 
										function( geometry ) {
											c2 = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
											c2.position.set( 0, 192.79, 0 );
											a2_pivot.add( c2 );

											loader.load( "models/b4.js", 
											function( geometry ) {
												b4 = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
												b4_pivot.add( b4 );
												b4_pivot.position.set( 0, 42.86, 0 );
												b4_pivot.rotation.x = THREE.Math.degToRad( -45 );
												c2.add( b4_pivot );
												_this.b4 = b4_pivot;

												loader.load( "models/head.js", 
												function( geometry ) {
													head = PLUS360DEGREES.MeshUtils.loadMesh( geometry, bodyMaterial );
													head.position.set( 0, 1.43, -21.4 );
													head_pivot.rotation.x = THREE.Math.degToRad( 45 );
													head_pivot.add( head );
													b4.add( head_pivot );
													_this.head = head;

													loader.load( "models/headlight.js", 
													function( geometry ) {
														headLight = PLUS360DEGREES.MeshUtils.loadMesh( geometry, headLightMaterial );
														head.add( headLight );

														if(callback && typeof(callback) === "function") { callback(); }
													} );

												} );

											} );

										} );

									} );

								} );

							} );

						} );
						
					} );

				} );

			} );

		} );
	
}

PLUS360DEGREES.Lamp.prototype = Object.create( THREE.Object3D.prototype );

PLUS360DEGREES.Lamp.prototype.rotateA1 = function()
{
	this.a1.rotation.y += 0.005;
	if(this.a1.rotation.y >= THREE.Math.degToRad( 360 ) ){
		this.a1.rotation.y = THREE.Math.degToRad( 0 );
	}
}

PLUS360DEGREES.Lamp.prototype.rotateA2 = function()
{
	this.b2.rotation.y += 0.005;
	if(this.b2.rotation.y >= THREE.Math.degToRad( 360 ) ){
		this.b2.rotation.y = THREE.Math.degToRad( 0 );
	}
}

PLUS360DEGREES.Lamp.prototype.rotateA3 = function()
{
	this.b4.rotation.y += 0.005;
	if(this.b4.rotation.y >= THREE.Math.degToRad( 360 ) ){
		this.b4.rotation.y = THREE.Math.degToRad( 0 );
	}
}

PLUS360DEGREES.Lamp.prototype.rotateHead = function()
{
	this.head.rotation.z += 0.005;
	if(this.head.rotation.z >= THREE.Math.degToRad( 360 ) ){
		this.head.rotation.z = THREE.Math.degToRad( 0 );
	}
}

PLUS360DEGREES.Lamp.prototype.reset = function()
{
	TweenMax.to( [ this.a1.rotation, this.b2.rotation, this.b4.rotation ], 1.0, { y:THREE.Math.degToRad( 0 ) } );
	TweenMax.to( this.head.rotation, 1.0, { z:THREE.Math.degToRad( 0 ) } );
}

PLUS360DEGREES.Magazine = function( callback )
{
	THREE.Object3D.call( this );

	var _this = this;

	var mesh;

	var _diffuse = PLUS360DEGREES.TextureUtils.loadTexture( "textures/mag_diffuse.jpg", 8, 
		function()
		{
			var _lightMap = PLUS360DEGREES.TextureUtils.loadTexture( "textures/mag_lightmap.jpg", 8, 
				function()
				{
					var material = new THREE.MeshLambertMaterial( {
								map: _diffuse,
								lightMap: _lightMap
							} );
					loader.load( "models/magazine.js", 
						function( geometry )
						{
							mesh = PLUS360DEGREES.MeshUtils.loadMesh( geometry, material );
							_this.add( mesh );

							var _luvs = mesh.geometry.faceVertexUvs[0];
							mesh.geometry.faceVertexUvs.push( _luvs );

							if(callback && typeof(callback) === "function") { callback(); }
						} );
				} );
		} );

}

PLUS360DEGREES.Magazine.prototype = Object.create( THREE.Object3D.prototype );