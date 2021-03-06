package org.timepedia.chronoscope.client.canvas;

import com.google.gwt.user.client.ui.Image;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Layer is a core Chronoscope drawing abstraction <p/> It is the key
 * immediate-mode render API used for drawing shapes, corresponding very close
 * to the Safari/WHATWG Javascript CANVAS.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public interface Layer {

  int COPY = 0;

  int SRC_ATOP = 1;

  int SRC_IN = 2;

  int SRC_OUT = 3;

  int SRC_OVER = 4;

  int DEST_ATOP = 5;

  int DEST_IN = 6;

  int DEST_OUT = 7;

  int DEST_OVER = 8;

  int DARKER = 9;

  int LIGHTER = 10;

  int XOR = 11;

  // layers in roughly increasing order of expected redraw
  public static String ROOT = "rootLayer";  // use background instead

  public static String BACKGROUND = "backgroundLayer";

  public static String TOP = "topLayer";
  public static String DATERANGE = "daterangeLayer";
  public static String ZOOMLEVEL = "zoomlevelLayer";
  public static String LEGEND = "legendLayer";

  public static String OVERVIEW_SMALL = "overviewSmallLayer";
  public static String OVERVIEW_SMALL_MASK = "overviewSmallMaskLayer";
  public static String OVERVIEW_SMALL_OVERLAY = "overviewSmallOverlayLayer";

  public static String RANGE_AXIS = "rangeAxisLayer";
  public static String RANGE_AXIS_LEFT = "rangeAxisLeftLayer";
  public static String RANGE_AXIS_RIGHT = "rangeAxisRightLayer";
  public static String RANGE_AXIS_OVERLAY = "rangeAxisOverlayLayer";
  public static String RANGE_AXIS_HOVER = "rangeAxisHoverLayer";

  public static String BOTTOM = "bottomLayer"; // bottom-most on screen enclosing overview, etc  not lowest z-order
  public static String DOMAIN_AXIS = "domainAxisLayer";
  public static String DOMAIN_AXIS_OVERLAY = "domainAxisOverlayLayer";
  public static String DOMAIN_AXIS_HOVER = "domainAxisHoverLayer";

  public static String PLOTAREA = "plotLayer";
  public static String PLOTAREA_OVERLAY = "plotOverlayLayer";
  public static String PLOTAREA_HOVER = "plotHoverLayer";
  public static String PLOTAREA_RANGE = "plotRangeLayer"; // axes lines, ticks, labels drawn on plot area
  public static String PLOTAREA_DOMAIN = "plotDomainLayer"; // axes lines, ticks, labels drawn on plot area

  public static String OVERVIEW_LARGE = "overviewLargeLayer";
  public static String OVERVIEW_LARGE_OVERLAY = "overviewLargeOverlayLayer";

  /**
   * Z_ORDER.indexOf(layerID)*3 can be used for the layer's Z_order as long as the layerID is from the options above
   *   of course layers here are listed in increasing Z order
   */
  public static List<String> Z_ORDER = new ArrayList<String>(){
    private static final long serialVersionUID = 1L;
    {
      add(ROOT); // deprecated
      add(BACKGROUND);

      add(TOP);
      add(LEGEND);
      add(ZOOMLEVEL);
      add(DATERANGE);

      add(PLOTAREA);
      add(PLOTAREA_OVERLAY);
      add(PLOTAREA_HOVER);
      add(PLOTAREA_RANGE);
      add(PLOTAREA_DOMAIN);

      add(OVERVIEW_SMALL);
      add(OVERVIEW_SMALL_OVERLAY);
      add(OVERVIEW_SMALL_MASK);

      add(RANGE_AXIS);
      add(RANGE_AXIS_LEFT);
      add(RANGE_AXIS_RIGHT);
      add(RANGE_AXIS_OVERLAY);
      add(RANGE_AXIS_HOVER);

      add(BOTTOM);
      add(DOMAIN_AXIS);
      add(DOMAIN_AXIS_OVERLAY);
      add(DOMAIN_AXIS_HOVER);

      add(OVERVIEW_LARGE);
      add(OVERVIEW_LARGE_OVERLAY);

    }
  };


  int TEXT_ALIGN_START = 0;
  int TEXT_ALIGN_END = 1;
  int TEXT_ALIGN_LEFT = 2;
  int TEXT_ALIGN_RIGHT = 3;
  int TEXT_ALIGN_CENTER = 4;

  int TEXT_BASELINE_TOP = 0;
  int TEXT_BASELINE_HANGING = 1;
  int TEXT_BASELINE_MIDDLE = 2;
  int TEXT_BASELINE_ALPHABETIC = 3;
  int TEXT_BASELINE_IDEOGRAPHIC = 4;
  int TEXT_BASELINE_BOTTOM = 5;

  void arc(double x, double y, double radius, double startAngle,
      double endAngle, int clockwise);

  void beginPath();

  void clear();

  void clearRect(double x, double y, double width, double height);

  /**
   * Each layer has an associated textLayer, this call essentially erases all
   * text drawn that was tagged with this layer.
   */
  void clearTextLayer(String textLayer);

  void dispose();

  void clip(double x, double y, double width, double height);

  void closePath();

  /**
   * Create a DisplayList tied to this layer with the given unique identifier.
   * DisplayLists can not be shared across Canvas or Layer instances.
   *
   * @param id a unique identifier
   */
  DisplayList createDisplayList(String id);

  LinearGradient createLinearGradient(double startx, double starty, double endx,
      double endy);

  PaintStyle createPattern(String imageUri);

  RadialGradient createRadialGradient(double x0, double y0, double r0,
      double x1, double y1, double r1);

  void drawImage(Layer layer, double x, double y, double width, double height);

  void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight);

  void drawImage(CanvasImage image, double dx, double dy, double dwidth, 
      double dheight);
  /**
   * Draws text rotated at an arbitrary angle. May use a server-side fontbook
   * service for better quality
   */
  void drawRotatedText(double x, double y, double v, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart);

  /**
   * Draw text at the given x,y coordinates within this layer, on the given
   * textLayer
   */
  void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String textLayer, Cursor cursorStyle);

  void fill();

  void fillRect(double startx, double starty, double width, double height);

  void fillRect();

  /**
   * Return the bounds (within the Canvas/View) of this layer
   */
  Bounds getBounds();

  void setBounds(Bounds b);

  /**
   * Return the Canvas which created this Layer
   */
  Canvas getCanvas();

  double getHeight();

  /**
   * Returns the alpha transparency of this layer
   */
  float getLayerAlpha();

  /**
   * Return the ID of this layer
   */
  String getLayerId();

  /**
   * Return the Z order of this layer
   */
  int getLayerOrder();

  /**
   * a layer may be created bigger than its actual bounds, in which case there
   * are hidden areas which are clipped. This returns the x coordinate of the
   * upper left of the drawing surface relative to the visible bounds.
   */
  int getScrollLeft();

  String getStrokeColor();

  String getTransparency();

  double getWidth();

  /**
   * Returns whether this layer is visible
   */
  boolean isVisible();

  void lineTo(double x, double y);

  void moveTo(double x, double y);

  void rect(double x, double y, double width, double height);

  void rotate(double angle);
  
  void restore();

  /**
   * Return the heighton the y-axis of a rotated string
   */
  int rotatedStringHeight(String str, double rotationAngle, String fontFamily,
      String fontWeight, String fontSize);

  /**
   * Return the width on the x-axis of a rotated string
   */
  int rotatedStringWidth(String str, double rotationAngle, String fontFamily,
      String fontWeight, String fontSize);

  void save();

  void scale(double sx, double sy);

  void setCanvasPattern(CanvasPattern canvasPattern);

  void setComposite(int mode);

  void setFillColor(PaintStyle p);

  /**
   * Set the alpha transparency of the whole layer, used when the layers are
   * flattened in endFrame()
   */
  void setLayerAlpha(float alpha);

  /**
   * Sets the Z order of this layer with respect to others, which controls the
   * flattening process during endFrame()
   */
  void setLayerOrder(int zorder);

  void setLinearGradient(LinearGradient lingrad);

  void setLineWidth(double width);

  void setRadialGradient(RadialGradient radialGradient);

  /**
   * a layer may be created bigger than its actual bounds, in which case there
   * are hidden areas which are clipped. This sets the x coordinate of the upper
   * left of the drawing surface relative to the visible bounds.
   */
  void setScrollLeft(int i);

  void setShadowBlur(double width);

  void setShadowColor(String color);

  void setShadowColor(Color shadowColor);

  void setShadowOffsetX(double x);

  void setShadowOffsetY(double y);

  void setStrokeColor(PaintStyle p);

  /**
   * Essentially sets the clipping region for the named text layer
   */
  void setTextLayerBounds(String textLayer, Bounds textLayerBounds);

  void setTransparency(float value);

  /**
   * if false, this layer will not be renderered when endFrame() is called
   */
  void setVisibility(boolean visibility);

  /**
   * Return the string height in pixels of a horizontally positioned string with
   * the given style
   */
  int stringHeight(String string, String font, String bold, String size);

  /**
   * Return the string width in pixels of a horizontally positioned string with
   * the given style
   */
  int stringWidth(String string, String font, String bold, String size);

  void stroke();

  void translate(double x, double y);
}
