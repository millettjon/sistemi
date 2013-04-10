// Namespace
var SM = SM || {};

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
  console.log(palette);

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

    ctx.stroke();
    ctx.fillStyle = palette[i];
    ctx.fill();

    startAngle = endAngle;
  }
}

SM.maxSwatches = function(radius) {
  var minSwatchWidth = 35; // size in pix of minimum swatch that can be touched with a finger
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

  // Helper fn to make a swatch.
  makeSwatch = function(palette) {
    // Handle simple color swatch.
    if (! (palette instanceof Array))
      return {"type": "swatch",
              "color": palette}

    // Use a mid range color to represent the sub palette.
    var colorIndex = Math.floor(palette.length/2);

    // Use the first color.
    //var colorIndex = 0;

    var color = palette[colorIndex];

    // Maximize the saturation.
    hsl = FB.RGBToHSL(FB.unpack(color));
    hsl[1] = 1.0;
    //hsl[2] = 0.5;  // mid range brightness yields the purest color
    color = FB.pack(FB.HSLToRGB(hsl));

    // Remove the swatch color from the palette.
    // TODO: WTF? Why does not splicing this muck things up?
    palette.splice(colorIndex, 1);

    // Sort the pallet by saturation and brightness. When mapped onto
    // a circle, brighter colors should be on top, darker on bottom,
    // more saturated on the left, and less saturated on the right.
    //
    // Steps:
    // - saturation and brightness range from 0 to 1
    // - re-normalize ranges to -0.5 to 0.5 for polar conversion to map onto circle
    // - convert to polar
    // - compare angle
    palette.sort(function(a,b) {
      var hslA = FB.RGBToHSL(FB.unpack(a));
      var hslB = FB.RGBToHSL(FB.unpack(b));
      var thetaA = Math.atan2(hslA[2] - 0.5, hslA[1] - 0.5);
      var thetaB = Math.atan2(hslB[2] - 0.5, hslB[1] - 0.5);
      return thetaA - thetaB;
    });

    return {"type": "swatch",
            "color": color,
            "band": SM.makeBand(radius - width, width, palette)}
  }

  var band = {"type": "band",
              "radius": radius,
              "width": width};

  var maxSwatches = SM.maxSwatches(radius);
  if (maxSwatches >= palette.length) {
    // Handle case where entire palette fits.
    band.swatches = palette.map(makeSwatch);
  }
  else {
    // Divide palette into sub palettes.
    var buckets = SM.bucketize(palette, maxSwatches);
    band.swatches = buckets.map(makeSwatch);
  }

  return band;
}

// From farbtastic.
var FB = FB || {}

FB.pack = function (rgb) {
  var r = Math.round(rgb[0] * 255);
  var g = Math.round(rgb[1] * 255);
  var b = Math.round(rgb[2] * 255);
  return '#' + (r < 16 ? '0' : '') + r.toString(16) +
    (g < 16 ? '0' : '') + g.toString(16) +
    (b < 16 ? '0' : '') + b.toString(16);
}

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
}

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
}

FB.hueToRGB = function (m1, m2, h) {
  h = (h < 0) ? h + 1 : ((h > 1) ? h - 1 : h);
  if (h * 6 < 1) return m1 + (m2 - m1) * h * 6;
  if (h * 2 < 1) return m2;
  if (h * 3 < 2) return m1 + (m2 - m1) * (0.66666 - h) * 6;
  return m1;
}

FB.HSLToRGB = function (hsl) {
  var m1, m2, r, g, b;
  var h = hsl[0], s = hsl[1], l = hsl[2];
  m2 = (l <= 0.5) ? l * (s + 1) : l + s - l*s;
  m1 = l * 2 - m2;
  return [this.hueToRGB(m1, m2, h+0.33333),
          this.hueToRGB(m1, m2, h),
          this.hueToRGB(m1, m2, h-0.33333)];
}

// Returns the hue of an rgb value.
FB.hue = function(rgb) {
  var unpacked = FB.unpack(rgb);
  var h = FB.RGBToHSL(unpacked)[0];
  //console.log(rgb, h);
  return h;
}

SM.point = function(x,y) {
  return {"x": x, "y": y};
}

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
}

// Returns the center point of the event's target.
SM.center = function(e) {
  var t = e.target;
  var x = t.width/2;
  var y = t.height/2;
  return SM.point(x, y);
}

// Returns the distance between two points.
SM.distance = function(p1, p2) {
  xd = p1.x - p2.x;
  yd = p1.y - p2.y;
  return Math.sqrt(xd*xd + yd*yd);
}

// Difference between two points.
SM.pointDiff = function(p1, p2) {
  return SM.point(p1.x - p2.x, p1.y - p2.y);
}

// Global state.
SM.state = {}
SM.state.bucket = 0;

// Handle mouse move events.
SM.mouseMove = function(band) {
  return function(e) {
    var offset = SM.offset(e);
    var center = SM.center(e);

    // Figure out which outer slice we are over.
    // Are we within the outer band?
    var distance = SM.distance(center, offset);
    if ((distance <= band.radius) && (distance >= (band.radius - band.width))) {
      // Which bucket does it fall into.
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
      if (bucketIndex != SM.state.bucket) {
        var ctx = e.target.getContext('2d');
        drawColorBand(ctx, center, band.swatches[bucketIndex].band);
        SM.state.bucket = bucketIndex;
      }
    }
  };
};

function makeColorWheel() {
  var canvas = $('#colorwheel').get(0);
  var ctx = canvas.getContext('2d');
 
  // Make the graph data structure representing the color wheel.
  var radius = canvas.height/2 - 1; // leave a 1 px buff, to prevent edges of circle from touching bounding box
  var width = radius * 1/3;
  var palette = ralPalette;
  palette.sort(function(a,b) {return FB.hue(a) - FB.hue(b)});    // Sort RAL colors by hue.
  var band = SM.makeBand(radius, width, palette);

  // Draw the outer band of the wheel.
  // TODO: Enhance cener function to take a dom element as an argument.
  var center = SM.point(canvas.width/2, canvas.height/2);
  drawColorBand(ctx, center, band);
  //drawColorBand(ctx, center, band.swatches[0].band);

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
  $('#colorwheel').mousemove(SM.mouseMove(band));
}

//console.log(Math.atan2(1,0)); // 1.57
//console.log(Math.atan2(1,1)); // 0.78
//console.log(Math.atan2(0,1)); // 0
//console.log(Math.atan2(-1,0)); // - pi/2
// wtf, atan2 parameter order is (y,x)?
// 0 is ----->
// degress proceed in counter clockwise fashion (as y axis is reversed)
//
//        -pi/2
//  pi/-pi      0
//        pi/2


jQuery(document).ready(function() { makeColorWheel(); });

// TODO: fix bug where including mid range color breaks the sub palette
// TODO: detect mouse over sub palette, and update color description (ral name and number)
// TODO: detect mouse click on sub palette, call callbacks (update text area, update shelf model).
// TODO: add circle indicators to display selected swatch. Is this
//       even needed? Maybe just on the interior swatch?
// TODO: add ability to set color (e.g., from text input)
// TODO: ? put a narrow black band in between?
// TODO: ? Can three.js be used to anti alias the rough edges? (anti-aliasing)
// TODO: try a margin between bands
// TODO: try making border 2px
// TODO: try hand calculating arc points
// TODO: sort the inner bucket based on polar coordinates
// TODO: support using predefined pie image
//       - just need to map mouse coords to correct pie slice
// TODO: ? support valchromat texture?
//        http://jsfiddle.net/3U9pm/
// TODO: ? is the trick of adding 0.5 to x and y coords useful?
//       - http://stackoverflow.com/questions/195262/can-i-turn-off-antialiasing-on-an-html-canvas-element

// TODO: add demo checkbox for using fully saturated hues in outer band

// TODO: rename swatch to bucket?
