package org.timepedia.chronoscope.client.canvas;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.render.LinearGradient;

/**
 * Layer is a core Chronoscope drawing abstraction
 * <p/>
 * It is the key immediate-mode render API used for drawing shapes, corresponding very close to the Safari/WHATWG
 * Javascript CANVAS.
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

    int Z_LAYER_BACKGROUND = 0;
    int Z_LAYER_PLOTAREA = 1;
    int Z_LAYER_AXIS = 2;
    int Z_LAYER_HIGHLIGHT = 3;


    /**
     * Return the Canvas which created this Layer
     *
     * @return
     */
    Canvas getCanvas();

    void fillRect(double startx, double starty, double width, double height);

    LinearGradient createLinearGradient(double startx, double starty, double endx, double endy);

    double getHeight();

    double getWidth();

    void setFillColor(String color);

    void setLinearGradient(LinearGradient lingrad);

    void fillRect();

    void beginPath();

    void setStrokeColor(String color);

    void setLineWidth(double width);

    void save();

    void moveTo(double x, double y);

    void lineTo(double x, double y);

    void stroke();

    void restore();

    /**
     * if false, this layer will not be renderered when endFrame() is called
     *
     * @param visibility
     */
    void setVisibility(boolean visibility);

    void closePath();

    void fill();

    void setTransparency(float value);

    void setComposite(int mode);

    void translate(double x, double y);

    void arc(double x, double y, double radius, double startAngle, double endAngle, int clockwise);


    /**
     * Return the string width in pixels of a horizontally positioned string with the given style
     *
     * @param string
     * @param font
     * @param bold
     * @param size
     * @return
     */
    int stringWidth(String string, String font, String bold, String size);

    /**
     * Return the string height in pixels of a horizontally positioned string with the given style
     *
     * @param string
     * @param font
     * @param bold
     * @param size
     * @return
     */
    int stringHeight(String string, String font, String bold, String size);

    void clearRect(double x, double y, double width, double height);

    void rect(double x, double y, double width, double height);

    void setShadowBlur(double width);

    void setShadowColor(String color);

    void setShadowOffsetX(double x);

    void setShadowOffsetY(double y);

    void clip(double x, double y, double width, double height);


    void drawImage(Layer layer, double x, double y, double width, double height);

    void scale(double sx, double sy);

    /**
     * Each layer has an associated textLayer, this call essentially erases all text drawn that was tagged with this
     * layer.
     *
     * @param textLayer
     */
    void clearTextLayer(String textLayer);


    /**
     * Draw text at the given x,y coordinates within this layer, on the given textLayer
     *
     * @param x
     * @param y
     * @param label
     * @param fontFamily
     * @param fontWeight
     * @param fontSize
     * @param textLayer
     */
    void drawText(double x, double y, String label, String fontFamily, String fontWeight, String fontSize,
                  String textLayer);

    /**
     * Essentially sets the clipping region for the named text layer
     *
     * @param textLayer
     * @param textLayerBounds
     */
    void setTextLayerBounds(String textLayer, Bounds textLayerBounds);

    PaintStyle createPattern(String imageUri);


    void setFillColor(PaintStyle p);

    void setCanvasPattern(CanvasPattern canvasPattern);

    void setRadialGradient(RadialGradient radialGradient);

    void setShadowColor(Color shadowColor);

    void setStrokeColor(PaintStyle p);


    RadialGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1);

    String getStrokeColor();

    String getTransparency();

    void drawImage(Layer layer, double sx, double sy, double swidth, double sheight, double dx, double dy,
                   double dwidth, double dheight);

    /**
     * Draws text rotated at an arbitrary angle. May use a server-side fontbook service for better quality
     *
     * @param x
     * @param y
     * @param v
     * @param label
     * @param fontFamily
     * @param fontWeight
     * @param fontSize
     * @param layerName
     * @param chart
     */
    void drawRotatedText(double x, double y, double v, String label, String fontFamily, String fontWeight,
                         String fontSize, String layerName, Chart chart);

    /**
     * Return the width on the x-axis of a rotated string
     *
     * @param str
     * @param rotationAngle
     * @param fontFamily
     * @param fontWeight
     * @param fontSize
     * @return
     */
    int rotatedStringWidth(String str, double rotationAngle, String fontFamily, String fontWeight, String fontSize);

    /**
     * Return the heighton the y-axis of a rotated string
     *
     * @param str
     * @param rotationAngle
     * @param fontFamily
     * @param fontWeight
     * @param fontSize
     * @return
     */
    int rotatedStringHeight(String str, double rotationAngle, String fontFamily, String fontWeight, String fontSize);

    /**
     * Return the ID of this layer
     *
     * @return
     */
    String getLayerId();

    /**
     * Return the bounds (within the Canvas/View) of this layer
     *
     * @return
     */
    Bounds getBounds();

    /**
     * Sets the Z order of this layer with respect to others, which controls the flattening process during
     * endFrame()
     *
     * @param zorder
     */
    void setLayerOrder(int zorder);

    /**
     * a layer may be created bigger than its actual bounds, in which case there are hidden areas which are clipped.
     * This returns the x coordinate of the upper left of the drawing surface relative to the visible bounds.
     *
     * @return
     */
    int getScrollLeft();

    /**
     * a layer may be created bigger than its actual bounds, in which case there are hidden areas which are clipped.
     * This sets the x coordinate of the upper left of the drawing surface relative to the visible bounds.
     *
     * @return
     */
    void setScrollLeft(int i);

    /**
     * Set the alpha transparency of the whole layer, used when the layers are flattened in endFrame()
     *
     * @param alpha
     */
    void setLayerAlpha(float alpha);

    /**
     * Return the Z order of this layer
     *
     * @return
     */
    int getLayerOrder();

    /**
     * Returns whether this layer is visible
     *
     * @return
     */
    boolean isVisible();

    /**
     * Returns the alpha transparency of this layer
     *
     * @return
     */
    float getLayerAlpha();

    /**
     * Create a DisplayList tied to this layer with the given unique identifier. DisplayLists can not be shared
     * across Canvas or Layer instances.
     *
     * @param id a unique identifier
     * @return
     */
    DisplayList createDisplayList(String id);


}
