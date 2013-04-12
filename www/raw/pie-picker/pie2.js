// Namespace
var SM = SM || {};

function focusSwatch(ctx, center, band, index,
                     focus,  // if true focus, otherwise unfocus
                     isInner // true if this is an inner swatch
                    ) {
  var clockwise = false;  // TODO: factor out

  var radius = isInner ?
    (band.radius - band.width - 2):
    (band.radius + 2);

  var palette = band.swatches.map(function(swatch) {return swatch.color});

  // Calculate start and end angles for slice.
  var startAngle = (Math.PI*2)*(index/palette.length);
  var endAngle = (Math.PI*2)*((index+1)/palette.length);
  if (!focus) {
    // Make unfocus angle wider to erase all traces
    startAngle -= 0.02;
    endAngle += 0.02;
  }

  // Draw arc.
  ctx.beginPath();
  ctx.arc(center.x,
          center.y,
          radius,
          startAngle,
          endAngle,
          clockwise);

  var oldLineWidth = ctx.lineWidth;
  var oldStyle = ctx.strokeStyle;
  if (focus) {
    ctx.lineWidth = 3
    ctx.strokeStyle = "#fff";
  }
  else {
    // make unfocus line width wider to erase all traces
    ctx.lineWidth = 4
    ctx.strokeStyle = "#000";
  }
  ctx.stroke();
  ctx.lineWidth = oldLineWidth;
  ctx.strokeStyle = oldStyle;
}

// Sets the focus on the outer band.
function setOuterFocus(e) {
  var ctx = e.target.getContext('2d');
  var center = SM.center(e);
  var band = e.data;
  var outerBucketIndex = SM.getBucketIndex(e, band);
  focusSwatch(ctx, center, band, outerBucketIndex, true, false);
  SM.state.cursor.indices[0] = outerBucketIndex;
}

// Clears the focus on the outer band.
function clearOuterFocus(e) {
  var ctx = e.target.getContext('2d');
  var center = SM.center(e);
  var band = e.data;
  var outerIndex = SM.state.cursor.indices[0];
  if (outerIndex != null) {
    focusSwatch(ctx, center, band, outerIndex, false, false);
    SM.state.cursor.indices[0] = null;
  }
}

function clearColorLabel(e) {
  // TODO: calculate a bounding box based on the diameter of the inner
  //       circle and clear it.
  var ctx = e.target.getContext('2d');
  var center = SM.center(e);
  ctx.clearRect(center.x - 25, center.y - 10 + 4 , 50, 20);
}

function drawColorLabel(e) {
  // Draw color label.
  var ctx = e.target.getContext('2d');
  var center = SM.center(e);
  var color = SM.getColor(e);
  ctx.fillStyle = "#AAA";
  ctx.font = "bold 12px Arial";
  ctx.textAlign = "center";
  clearColorLabel(e);
  ctx.fillText(color.ral, center.x, center.y + 4, 50);
}

// Sets the focus on the inner band.
function setInnerFocus(e) {
  var ctx = e.target.getContext('2d');
  var center = SM.center(e);
  var outerIndex = SM.state.cursor.indices[0];
  if (outerIndex != null) {
    var outerBand = e.data;
    var innerBand = outerBand.swatches[outerIndex].band;
    var innerBucketIndex = SM.getBucketIndex(e, innerBand);
    focusSwatch(ctx, center, innerBand, innerBucketIndex, true, true);
    SM.state.cursor.indices[1] = innerBucketIndex;
    drawColorLabel(e);
  }
}
  

// Clear focus on inner band.
function clearInnerFocus(e) {
  var outerIndex = SM.state.cursor.indices[0];
  if (outerIndex != null) {
    var innerIndex = SM.state.cursor.indices[1];
    if (innerIndex != null) {
      var outerBand = e.data;
      var ctx = e.target.getContext('2d');
      var center = SM.center(e);
      var outerBand = e.data;
      var innerBand = outerBand.swatches[outerIndex].band;
      focusSwatch(ctx, center, innerBand, innerIndex, false, true);
      SM.state.cursor.indices[1] = null;
      clearColorLabel(e);
    }
  }
}


// Draws a color band a circular color band of the specified width
// and palette.
function drawColorBand(ctx, center, band) {

  var radiusInner = band.radius - band.width;
  var startAngle = 0;
  var endAngle;
  var startX, startY;
  var clockwise = false;
  var counterClockwise = true;

  var palette = band.swatches.map(function(swatch) {return swatch.color});

  // Draw outer circle.
  for (var i = 0; i < palette.length; i++) {
    // Calculate end angle for current slice.
    endAngle = (Math.PI*2)*((i+1)/palette.length);

    // Draw inner arc.
    ctx.beginPath();
    startX = radiusInner * Math.cos(startAngle) + center.x;
    startY = radiusInner * Math.sin(startAngle) + center.y;
    ctx.moveTo(startX, startY);
    ctx.arc(center.x,
            center.y,
            radiusInner,
            startAngle,
            endAngle,
            clockwise);

    // Draw outer arc.
    ctx.arc(center.x,
            center.y,
            band.radius,
            endAngle,
            startAngle,
            counterClockwise);

    // Move back to start to complete shape.
    ctx.lineTo(startX, startY);

    var bw = SM.state.swatch.border_width;
    if (bw > 0) {
      ctx.lineWidth = bw;
      ctx.stroke();
    }

    ctx.fillStyle = palette[i].rgb;
    ctx.fill();

    startAngle = endAngle;
  }
}

// Erases the inner color circle.
function clearInnerColorBand(e) {
  var ctx = e.target.getContext('2d');
  var center = SM.center(e);
  var band = e.data;
  var radius = band.swatches[0].band.radius;

  ctx.beginPath();
  ctx.arc(center.x, center.y, radius, 0, 2 * Math.PI, false);
  var oldFillStyle = ctx.fillStyle;
  ctx.fillStyle = 'black';
  ctx.fill();
  ctx.fillStyle = oldFillStyle;
}

SM.maxSwatches = function(radius) {
  //var minSwatchWidth = 35; // size in pix of minimum swatch that can be touched with a finger
  // size in pix of minimum swatch that can be touched with a finger
  var minSwatchWidth = SM.state.swatch.min_width;
  return Math.floor(2*Math.PI*radius/minSwatchWidth);
}

// Partitions an array into numBuckets buckets.
SM.bucketize = function(items, numBuckets) {
  var l = items.length;
  var buckets = [];
  var i = 0;
  var startIndex = 0;
  var endIndex;
  var bucketSize = items.length/numBuckets;

  // Note: Make sure items are evenly distributed.
  for (i=1; i <= numBuckets; i++) {
    endIndex = Math.round(i*bucketSize);
    buckets.push(items.slice(startIndex, endIndex + 1));
    startIndex = endIndex + 1;
  }
  return buckets;
}

// Makes a color band.
// Data Shape:
// band:
//   radius
//   width
//   swatches
//
// swatch:
//   color
//   band
SM.makeBand = function(radius, width, palette) {
  var band = {"type": "band",
              "radius": radius,
              "width": width};

  var maxSwatches = SM.maxSwatches(radius);
  // TODO: Handle case where total max items is exceeded.
  //       It fails trying to make empty sub bands.
  if (maxSwatches >= palette.length) {
    // Handle case where entire palette fits.
    band.swatches = palette.map(function(e) {return SM.makeSwatch(null, e)});
  }
  else {
    // Divide palette into sub palettes.
    var buckets = SM.bucketize(palette, maxSwatches);
    band.swatches = buckets.map(function(e) {
      return SM.makeSwatch(band, e)});
  }

  return band;
};

// Makes a swatch
SM.makeSwatch = function(band,   // parent band
                  palette // palette of colors
                 ) {
  // Handle simple color swatch.
  if (! (palette instanceof Array)) {
    return {"type": "swatch", "color": palette}
  };

  var s = SM.state.outer_band;

  // Pick a color to represent the palette.
  var color;
  // Use the first color.
  switch (s.color_fn) {
  case 'first':
    color = palette[0];
    break;
  case 'median':
    // Use the color with the median hue value.
    color = palette[Math.floor(palette.length/2)];
    break;
  case 'average':
    // Use the average color value.
    color = Color.average(palette);
    break;
  default:
    throw("bad color_fn: " + s.color_fn);
  }

  // Maximize the saturation.
  hsl = Color.hsl(color);
  if (s.force_full_saturation) {
    hsl[1] = 1.0;
  }

  // Force brightness to midrange.
  if (s.force_midrange_brightness) {
    hsl[2] = 0.5;
  }

  color = Color.fromHSL(hsl);

  var colorIndex = 0;

  // Sort the pallet by saturation and brightness. When mapped onto
  // a circle, brighter colors should be on top, darker on bottom,
  // more saturated on the left, and less saturated on the right.
  //
  // Steps:
  // - saturation and brightness range from 0 to 1
  // - re-normalize ranges to -0.5 to 0.5 for polar conversion to map onto circle
  // - convert to polar
  // - compare angle
  if (SM.state.inner_band.sort) {
    palette.sort(function(a,b) {
      var hslA = Color.hsl(a);
      var hslB = Color.hsl(b);
      var thetaA = Math.atan2(hslA[2] - 0.5, hslA[1] - 0.5);
      var thetaB = Math.atan2(hslB[2] - 0.5, hslB[1] - 0.5);
      return thetaA - thetaB;
    });
  };

  // A margin of 1, makes for a cleaner redraw since the perimeter
  // stroke doesn't overlap the outer band.
  var margin = SM.state.band.margin;
  return {"type": "swatch",
          "color": color,
          "band": SM.makeBand(band.radius - band.width - margin, band.width, palette)};
};

SM.point = function(x,y) {
  return {"x": x, "y": y};
};

// Returns the x, y offset of an event in its target.
SM.offset = function(e) {
  // Mouse event.
  if (e.offsetX != undefined)
    return SM.point(e.offsetX, e.offsetY);

  var ox = e.target.offsetLeft;
  var oy = e.target.offsetTop;
  // Firefox mouse event.
  if (e.pageX != undefined)
    return SM.point(e.pageX - ox, e.pageY - oy);

  // Lookup touches from the original event since jquery doesn't
  // integrate w/ touch data.
  var t = e.originalEvent.touches[0];
  return SM.point(t.pageX - ox, t.pageY - oy);
};

// Returns the center point of the event's target.
SM.center = function(e) {
  var t = e.target;
  var x = t.width/2;
  var y = t.height/2;
  return SM.point(x, y);
};

// Returns the distance between two points.
SM.distance = function(p1, p2) {
  xd = p1.x - p2.x;
  yd = p1.y - p2.y;
  return Math.sqrt(xd*xd + yd*yd);
};

// Difference between two points.
SM.pointDiff = function(p1, p2) {
  return SM.point(p1.x - p2.x, p1.y - p2.y);
};

// current/selected
// actual/selected
// selected/hovered
// selected/cursor

// {
//   "selected": {"color": "#xyz",
//              "indices": [0,3]}
  
//   "cursor": {"color": "#xyz",
//              "indices": [0,3]}
// }

// Global state.
SM.state = {
  // state
  "cursor": {"color": null, "indices": [null, null]},
  "selected": {"color": null, "indices": [null, null]},

  // configuration
  "outer_band": {
    "color_fn": "average",
    "force_full_saturation": false,
    "force_midrange_brightness": false
  },
  "band": {"margin": 1},
  "swatch": {
    "min_width": 32,
    "border_width": 2
  },
  "palette": {"sort": true},
  "inner_band": {"sort": true}
};
// TODO: Add different default sets.
SM.state.band.margin = 0;
SM.state.swatch.min_width = 25;
SM.state.swatch.border_width = 0;
SM.state.palette.sort = false;
SM.state.inner_band.sort = false;

// Returns the index of the buck which the mouse is in.
SM.getBucketIndex = function(e, band) {
  var offset = SM.offset(e);
  var center = SM.center(e);

  // Which swatch are we over?
  // Find polar angle.
  var p = SM.pointDiff(offset, center);
  var theta = Math.atan2(p.y, p.x);

  // Normalize angle to find arc length.
  var arcLength = (theta >= 0) ?
    theta :
    theta + Math.PI*2;

  // Find bucket index.
  var bucketArc = (Math.PI*2) / band.swatches.length;
  var bucketIndex = Math.floor(arcLength/bucketArc);
  return bucketIndex;
};

// Returns the color under point (if any).
SM.getColor = function(e) {
  if (!SM.inInnerBand(e))
    return null;

  // find the selected color
  var band = e.data;
  var ids = SM.state.cursor.indices;
  var oi = ids[0];
  var ii = ids[1];

  if ((oi == null) || (ii == null))
    return null;

  return band.swatches[oi].band.swatches[ii].color;
}

SM.mousedown = function(e) {
  if (SM.inInnerBand(e)) {
    var color = SM.getColor(e);

    // TODO: call event handlers to set color
    console.log("color", color);
  }
}

// Returns true of the event coordinates are in the inner band.
SM.inInnerBand = function(e) {
  var band = e.data;
  var offset = SM.offset(e);
  var center = SM.center(e);
  var distance = SM.distance(center, offset);
  var radius = band.radius - band.width - SM.state.band.margin;
  return (distance <= radius && (distance >= (radius = band.width))) 
}

function clearLog() {
  $('#message').html('&nbsp;');
}

function log(s) {
  $('#message').text(s);
  //$('#message').append(s + " ");
  setTimeout(clearLog, 2000);
}

SM.touchmove = function(e) {
  log("touchmove");
  var oe = e.originalEvent;
  if ( oe.touches.length == 1 ) {
    e.preventDefault();
    SM.mousemove(e);
  }
}

SM.touchstart = function(e) {
  var oe = e.originalEvent;
  if ( oe.touches.length == 1 ) {
    e.preventDefault();
    // needed for firefox on android to focus a swatch on tap
    SM.mousemove(e);
  }
}

// Handle mouse move events.
SM.mousemove = function(e) {
  var ctx = e.target.getContext('2d');
  var band = e.data;
  var offset = SM.offset(e);
  var center = SM.center(e);
  var distance = SM.distance(center, offset);
  var outerIndex = SM.state.cursor.indices[0];

  // Are we in the outer band?
  if ((distance <= band.radius) && (distance >= (band.radius - band.width))) {

    // Display
    // Only draw if moving to a new bucket.
    var outerBucketIndex = SM.getBucketIndex(e, band);
    if (outerBucketIndex != outerIndex) {
      clearInnerFocus(e);
      clearOuterFocus(e);
      setOuterFocus(e);
      drawColorBand(ctx, center, band.swatches[outerBucketIndex].band);
    }
  }

  // Are we in the inner band?
  else if (SM.inInnerBand(e)) {
    if (outerIndex != null) {
      // Only draw if moving to a new bucket.
      var innerBand = band.swatches[outerIndex].band;
      var innerBucketIndex = SM.getBucketIndex(e, innerBand);
      var innerIndex = SM.state.cursor.indices[1];

      if (innerBucketIndex != innerIndex) {
        clearInnerFocus(e);
        setInnerFocus(e);
      }
    }
  }

  // Are we outside the outer band?
  else if (distance > band.radius) {
    if (outerIndex != null) {
      clearInnerFocus(e);
      clearOuterFocus(e);
      render(); // hack to delete inner band; useful to clean up glitches
      //clearInnerColorBand(e);
    }
  }

  // We must be inside the inner band.
  else {
    clearInnerFocus(e);
  }

};

function makeColorWheel() {
  var canvas = $('#colorwheel').get(0);
  var ctx = canvas.getContext('2d');

  ctx.clearRect(0, 0, canvas.width, canvas.height);
 
  // Make the graph data structure representing the color wheel.
  // Note: 1px margin if not focusing, 4 px if focusing
  var radius = canvas.height/2 - 4; // leave a small marging, to prevent edges of circle from touching bounding box
  var width = radius * 1/3;
  var palette = ralPalette.slice(0); // shallow clone

  if (SM.state.palette.sort) {
    // Sort RAL colors by hue.
    palette.sort(function(a,b) {return Color.hue(a) - Color.hue(b)});
  }

  var band = SM.makeBand(radius, width, palette);

  // Draw the outer band of the wheel.
  // TODO: Enhance center function to take a dom element as an argument.
  var center = SM.point(canvas.width/2, canvas.height/2);
  drawColorBand(ctx, center, band);
  // TODO: if focused on a swatch in the outer band, focus it and draw
  // the inner band.

  // draw some text
  ctx.fillStyle = "#777";
  ctx.font = "bold 12px Arial";

  // Draw at upperleft.
  ctx.textAlign = "left";
  ctx.fillText("RAL", 5, 15);

  // Draw at upper right.
  // ctx.textAlign = "right";
  // ctx.fillText("1015", canvas.width - 5, 15);
  //ctx.clearRect(0, 0, canvas.width, canvas.height);  // sample of how to clear a rectangle

  // Hookup event handlers.
  $('#colorwheel').off('mousemove.piepick');
  $('#colorwheel').on('mousemove.piepick', null, band, SM.mousemove);
  $('#colorwheel').off('mousedown.piepick');
  $('#colorwheel').on('mousedown.piepick', null, band, SM.mousedown);

  $('#colorwheel').on('touchmove.piepick', null, band, SM.touchmove);
  $('#colorwheel').on('touchstart.piepick', null, band, SM.touchstart);
}

// Note: Degrees proceed in counter clockwise fashion (as y axis is reversed).
//
//        -pi/2
//  pi/-pi      0
//        pi/2


function render() {
  makeColorWheel();
};

jQuery(document).ready(function() {

  // --------------------------------------------------
  // HOOKUP FROB EVENT HANDLERS

  var s_os = SM.state.outer_band;

  // OUTER SEGMENT COLOR_FN
  var radios = $('input:radio[name="outer_band_color_fn"]');
  radios.filter('[value="' + s_os.color_fn + '"]').attr('checked', true);
  radios.change(function() {
    s_os.color_fn = $(this).val();
    render();
  });

  // var frob = $('#outer_band<_color_fn');
  // frob.val(s_os.color_fn);
  // frob.change(function() {
  //   s_os.color_fn = $(this).val();
  //   render();
  // });

  // FORCE FULL SATURATION
  var frob = $('#force_full_saturation');
  frob.prop('checked', s_os.force_full_saturation);
  frob.change(function() {
    s_os.force_full_saturation = $(this).is(':checked');
    render();
  });

  // FORCE MIDRANGE BRIGHTNESS
  var frob = $('#force_midrange_brightness');
  frob.prop('checked', s_os.force_midrange_brightness);
  frob.change(function() {
    s_os.force_midrange_brightness = $(this).is(':checked');
    render();
  });

  // SWATCH MIN WIDTH
  var frob = $('#swatch_min_width');
  var s_sw = SM.state.swatch;
  frob.val(s_sw.min_width);
  frob.change(function() {
    s_sw.min_width = $(this).val();
    render();
  });

  // SWATCH BORDER WIDTH
  var frob = $('#swatch_border_width');
  var s_sw = SM.state.swatch;
  frob.val(s_sw.border_width);
  frob.change(function() {
    s_sw.border_width = $(this).val();
    render();
  });

  // BAND MARGIN
  var frob = $('#band_margin');
  var s_bnd = SM.state.band;
  frob.val(s_bnd.margin);
  frob.change(function() {
    s_bnd.margin = $(this).val();
    render();
  });

  // PALETTE NUM COLORS
  // TODO: hookup to actual control.
  // TODO: move ral to its own namespace.
  // TODO: move to stats area.
  $('#palette_num_colors').text(ralPalette.length);

  // PALETTE SORT
  var frob = $('#palette_sort');
  var s_pl = SM.state.palette;
  frob.prop('checked', s_pl.sort);
  frob.change(function() {
    s_pl.sort = $(this).is(':checked');
    render();
  });

  // INNER BAND SORT
  var frob = $('#inner_band_sort');
  var s_ib = SM.state.inner_band;
  frob.prop('checked', s_ib.sort);
  frob.change(function() {
    s_ib.sort = $(this).is(':checked');
    render();
  });

  // --------------------------------------------------
  // DRAW THE COLOR WHEEL
  render();

});

// TODO: relax restriction on min swatch width. auto balance width of
//       outer and inner buckets based on a ratio
//       ? how to better determine segment widths?
//         - figure out max supported total colors based on number of bands
//           and the outer circumferences of each band
//         - figure out the ratio of the max segments outer band / max segments inner band
//         - find factors that multiply to the palette size and have
//           roughly the same ratio as above
// TODO: see if black and white colors can be in their own pie slice
// TODO: group into buckets based on distance between colors
//       See: http://stackoverflow.com/questions/480316/how-do-i-group-objects-in-a-set-by-proximity
//       k-means or voronoi clustering, possibly beforehand
//       See: http://code.google.com/p/hdict/source/browse/gae/files/kmeans.js
//       See: https://github.com/harthur/clusterfck
// TODO: add additional mouse event handlers to handle touch interfaces properly
// TODO: detect mouse click on sub palette, call callbacks (update text area, update shelf model).
// TODO: add ability to set color (e.g., from text input)
//       ? should the picker show this color by default? when not
//       selecting a new color?
// TODO: add a few configuation themes to frob multiple controls at once
// TODO: support using predefined pie image
//       - just need to map mouse coords to correct pie slice
// TODO: ? support valchromat texture?
//        http://jsfiddle.net/3U9pm/
// TODO: ? is the trick of adding 0.5 to x and y coords useful?
//       - http://stackoverflow.com/questions/195262/can-i-turn-off-antialiasing-on-an-html-canvas-element
// TODO: namespace all functions
// TODO: make it work as a standard jquery plugin

// TODO: ? should the bucketing algorithm take into account distance
//         from the mean of each bucket?

// TODO: ? use background layer for performance improvement?
//       http://www.html5rocks.com/en/tutorials/canvas/performance/

// NOTES
// ? is a 3 level wheel useful? probably not as the colors will be too similar
// ? is a full color picker useful? not really as it doesn't narrow down enough

// TESTED BROWSERS
// test: linux firefox
// test: linux crhome
// test: android browser
// test: android firefox
// test: android chrome
