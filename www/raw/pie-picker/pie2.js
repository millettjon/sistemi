// Namespace
var SM = SM || {};

// Focuses by outlining the entire swatch.
function focusSwatch1(ctx, center, band, index, focus) {
  var clockwise = false;  // TODO: factor out
  var counterClockwise = true; // TODO: factor out

  var radiusInner = band.radius - band.width;
  var palette = band.swatches.map(function(swatch) {return swatch.color});

  // Calculate start and end angles for slice.
  var startAngle = (Math.PI*2)*(index/palette.length);
  var endAngle = (Math.PI*2)*((index+1)/palette.length);

  // Draw inner arc.
  ctx.beginPath();
  var startX = radiusInner * Math.cos(startAngle) + center.x;
  var startY = radiusInner * Math.sin(startAngle) + center.y;
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
  // TODO: ? Is closeShape useful?
  ctx.lineTo(startX, startY);

  var oldLineWidth = ctx.lineWidth;
  var oldStyle = ctx.strokeStyle;
  if (focus) {
    ctx.lineWidth = SM.state.swatch.border_width - 1;
    ctx.strokeStyle = "#fff";
  }
  else {
    ctx.lineWidth = SM.state.swatch.border_width;
    ctx.strokeStyle = "#000";
  }
  ctx.stroke();
  ctx.lineWidth = oldLineWidth;
  ctx.strokeStyle = oldStyle;
}

// Focuses by drawing a concentric arc around outside of swatch.
function focusSwatch(ctx, center, band, index, focus) {
  var clockwise = false;  // TODO: factor out

  var radius = band.radius + 2;
  var palette = band.swatches.map(function(swatch) {return swatch.color});

  // Calculate start and end angles for slice.
  var startAngle = (Math.PI*2)*(index/palette.length);
  var endAngle = (Math.PI*2)*((index+1)/palette.length);
  if (!focus) {
    // Make unfocus angle wider to erase all traces
    startAngle -= 0.02
    endAngle += 0.02
  }

  // Draw arc.
  ctx.beginPath();
  var startX = radius * Math.cos(startAngle) + center.x;
  var startY = radius * Math.sin(startAngle) + center.y;
  ctx.moveTo(startX, startY);
  ctx.arc(center.x,
          center.y,
          radius,
          startAngle,
          endAngle,
          clockwise);

  var oldLineWidth = ctx.lineWidth;
  var oldStyle = ctx.strokeStyle;
  if (focus) {
    ctx.lineWidth = SM.state.swatch.border_width + 1;
    ctx.strokeStyle = "#fff";
  }
  else {
    // make unfocus line width wider to erase all traces
    ctx.lineWidth = SM.state.swatch.border_width + 2;
    ctx.strokeStyle = "#000";
  }
  ctx.stroke();
  ctx.lineWidth = oldLineWidth;
  ctx.strokeStyle = oldStyle;
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

    ctx.fillStyle = palette[i];
    ctx.fill();

    startAngle = endAngle;
  }
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
  //console.log("bucketize: numBuckets: ", numBuckets);
  //console.log("bucketize: bucketSize: ", bucketSize);
  // Note: Make sure items are evenly distributed.
  for (i=1; i <= numBuckets; i++) {
    endIndex = Math.round(i*bucketSize);
    buckets.push(items.slice(startIndex, endIndex));
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

// Returns the average color in a palette.
// Note: expects and returns unpacked values.
SM.averageColor = function(palette) {
  var total = palette.reduce(function(previous, current, index, array) {
    var p = previous;
    var c = current;
    p[0] += c[0];
    p[1] += c[1];
    p[2] += c[2];
    return p
  }, [0,0,0]);

  var t = total.map(function(i) {return i/palette.length});
  return t;
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
    color = FB.pack(SM.averageColor(palette.map(FB.unpack)));
    break;
  default:
    throw("bad color_fn: " + s.color_fn);
  }

  // Maximize the saturation.
  hsl = FB.RGBToHSL(FB.unpack(color));
  if (s.force_full_saturation) {
    hsl[1] = 1.0;
  }

  // Force brightness to midrange.
  if (s.force_midrange_brightness) {
    hsl[2] = 0.5;
  }
  color = FB.pack(FB.HSLToRGB(hsl));

  // Remove the swatch color from the palette.
  // TODO: WTF? Why does not splicing this muck things up?
  var colorIndex = 0;
  // DON'T CRASH
  //palette.splice(colorIndex, 1);
  //palette.splice(colorIndex, 5);
  //palette.shift();
  //palette[0]="#FFFFFF";

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
      var hslA = FB.RGBToHSL(FB.unpack(a));
      var hslB = FB.RGBToHSL(FB.unpack(b));
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


// From farbtastic.
var FB = FB || {};

FB.pack = function (rgb) {
  var r = Math.round(rgb[0] * 255);
  var g = Math.round(rgb[1] * 255);
  var b = Math.round(rgb[2] * 255);
  return '#' + (r < 16 ? '0' : '') + r.toString(16) +
    (g < 16 ? '0' : '') + g.toString(16) +
    (b < 16 ? '0' : '') + b.toString(16);
};

FB.unpack = function (color) {
  if (color.length == 7) {
    return [parseInt('0x' + color.substring(1, 3)) / 255,
            parseInt('0x' + color.substring(3, 5)) / 255,
            parseInt('0x' + color.substring(5, 7)) / 255];
  }
  else if (color.length == 4) {
    return [parseInt('0x' + color.substring(1, 2)) / 15,
            parseInt('0x' + color.substring(2, 3)) / 15,
            parseInt('0x' + color.substring(3, 4)) / 15];
  }
};

FB.RGBToHSL = function (rgb) {
    var min, max, delta, h, s, l;
    var r = rgb[0], g = rgb[1], b = rgb[2];
    min = Math.min(r, Math.min(g, b));
    max = Math.max(r, Math.max(g, b));
    delta = max - min;
    l = (min + max) / 2;
    s = 0;
    if (l > 0 && l < 1) {
      s = delta / (l < 0.5 ? (2 * l) : (2 - 2 * l));
    }
    h = 0;
    if (delta > 0) {
      if (max == r && max != g) h += (g - b) / delta;
      if (max == g && max != b) h += (2 + (b - r) / delta);
      if (max == b && max != r) h += (4 + (r - g) / delta);
      h /= 6;
    }
  return [h, s, l];
};

FB.hueToRGB = function (m1, m2, h) {
  h = (h < 0) ? h + 1 : ((h > 1) ? h - 1 : h);
  if (h * 6 < 1) return m1 + (m2 - m1) * h * 6;
  if (h * 2 < 1) return m2;
  if (h * 3 < 2) return m1 + (m2 - m1) * (0.66666 - h) * 6;
  return m1;
};

FB.HSLToRGB = function (hsl) {
  var m1, m2, r, g, b;
  var h = hsl[0], s = hsl[1], l = hsl[2];
  m2 = (l <= 0.5) ? l * (s + 1) : l + s - l*s;
  m1 = l * 2 - m2;
  return [this.hueToRGB(m1, m2, h+0.33333),
          this.hueToRGB(m1, m2, h),
          this.hueToRGB(m1, m2, h-0.33333)];
};

// Returns the hue of an rgb value.
FB.hue = function(rgb) {
  var unpacked = FB.unpack(rgb);
  var h = FB.RGBToHSL(unpacked)[0];
  //console.log(rgb, h);
  return h;
};

SM.point = function(x,y) {
  return {"x": x, "y": y};
};

// Returns the x, y offset of an event in its target.
SM.offset = function(e) {
  var xpos = e.offsetX;
  var ypos = e.offsetY;

  // Hack for Firefox
  if (e.offsetX==undefined) {
    //xpos = e.pageX - $('#colorwheel').offset().left;
    xpos = e.pageX - e.target.offsetLeft;
    //ypos = e.pageY - $('#colorwheel').offset().top;
    ypos = e.pageY - e.target.offsetTop;
  }             
  return SM.point(xpos, ypos);
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
// store color and band indexes for both current and selected
// one which mouse is over
// actual saved color, and associated indices
SM.state = {
  "bucket": 0,
  "cursor": {"color": null, "indices": null},
  "selected": {"color": null, "indices": null},
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

// Handle mouse move events.
SM.mouseMove = function(e) {
  var band = e.data;
  var offset = SM.offset(e);
  var center = SM.center(e);

  var distance = SM.distance(center, offset);

  // Are we within the outer band?
  if ((distance <= band.radius) && (distance >= (band.radius - band.width))) {
    // Which swatch are we over?
    // Find polar angle.
    p = SM.pointDiff(offset, center);
    theta = Math.atan2(p.y, p.x);

    // Normalize angle to find arc length.
    var arcLength = (theta >= 0) ?
      theta :
      theta + Math.PI*2;

    // Find bucket index.
    var bucketArc = (Math.PI*2) / band.swatches.length;
    var bucketIndex = Math.floor(arcLength/bucketArc);

    // Display
    // Only draw if moving to a new bucket.
//    var outerIndex = SM.state.cursor.indices[0];
    if (bucketIndex != SM.state.bucket) {
      var ctx = e.target.getContext('2d');
      // Highlight current swatch in outer band.
      focusSwatch(ctx, center, band, SM.state.bucket, false);
      focusSwatch(ctx, center, band, bucketIndex, true);

      // Redraw inner band.
      drawColorBand(ctx, center, band.swatches[bucketIndex].band);
      SM.state.bucket = bucketIndex;
    }
  }

  // Are we within the inner band?
  else if (distance <= (band.radius - band.width - SM.state.band.margin)) {
    // need an inner bucket index
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
    palette.sort(function(a,b) {return FB.hue(a) - FB.hue(b)});
  }

  var band = SM.makeBand(radius, width, palette);

  // Draw the outer band of the wheel.
  // TODO: Enhance cener function to take a dom element as an argument.
  var center = SM.point(canvas.width/2, canvas.height/2);
  drawColorBand(ctx, center, band);
  focusSwatch(ctx, center, band, SM.state.bucket, true);
  drawColorBand(ctx, center, band.swatches[SM.state.bucket].band);

  // draw some text
  ctx.fillStyle = "#777";
  ctx.font = "bold 12px Arial";

  // Draw at center.
  // ctx.textAlign = "center";
  // ctx.fillText("RAL", center.x, center.y - 5, 50 );
  // ctx.fillText("1000", center.x, center.y + 9 );

  // Draw at upperleft.
  ctx.textAlign = "left";
  ctx.fillText("RAL", 5, 15);

  // Draw at upper right.
  ctx.textAlign = "right";
  ctx.fillText("1015", canvas.width - 5, 15);
  //ctx.clearRect(0, 0, canvas.width, canvas.height);  // sample of how to clear a rectangle

  // Hookup event handlers.
  $('#colorwheel').off('mousemove.piepick');
  $('#colorwheel').on('mousemove.piepick', null, band, SM.mouseMove);
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

// TODO: fix bug where including mid range color breaks the sub palette
// TODO: fix bug where adding a margin breaks rendering
// TODO: auto balance width of outer and inner buckets
// TODO: release on github
// TODO: blog
// TODO: add additional mouse event handlers to handle touch interfaces properly
// TODO: detect mouse over sub palette, and update color description (ral name and number)
// TODO: detect mouse click on sub palette, call callbacks (update text area, update shelf model).
// TODO: add circle indicators to display selected swatch. Is this
//       even needed? Maybe just on the interior swatch?
// TODO: add ability to set color (e.g., from text input)
// TODO: ? put a narrow black band in between?
// TODO: ? Can three.js be used to anti alias the rough edges? (anti-aliasing)
//       - see sub pixel rendering: http://www.html5rocks.com/en/tutorials/canvas/performance/
// TODO: try a margin between bands
// TODO: try making border 2px
// TODO: try hand calculating arc points
// TODO: support using predefined pie image
//       - just need to map mouse coords to correct pie slice
// TODO: ? support valchromat texture?
//        http://jsfiddle.net/3U9pm/
// TODO: ? is the trick of adding 0.5 to x and y coords useful?
//       - http://stackoverflow.com/questions/195262/can-i-turn-off-antialiasing-on-an-html-canvas-element

// TODO: add demo checkbox for using fully saturated hues in outer band

// TODO: rename swatch to bucket?
// TODO: ? should the bucketing algorithm take into account distance
//         from the mean of each bucket?

// TODO: use background layer?
//       http://www.html5rocks.com/en/tutorials/canvas/performance/

// TODO: highlight selected pie slices
// TODO: ? use functional js library?
// TODO: ? use clojurescript?
// TODO: make stroke optional
// TODO: adjust padding
// TODO: move all state and configuration into a single data structure


// ? is a 3 level wheel useful? probably not as the colors will be too similar
// ? is a full color picker useful? not really as it doesn't narrow down enough

// ? how to better determine segment widths?
// - figure out max supported total colors based on number of bands
//   and the outer circumferences of each band
// - figure out the ratio of the max segments outer band / max segments inner band
// - find factors that multiply to the palette size and have
//   roughly the same ratio as above

// ? how to find the index of the selected color?
//   ? search each swatch in outer band?
//   ? search once and save index?
//     - selected.color = #fab
//     - selected.index = [indexOuter, indexInner]
// ? use gray arc to indicate selected color?
