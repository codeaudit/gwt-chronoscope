package org.timepedia.chronoscope.client.canvas.mock;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.AbstractLayer;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.RadialGradient;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.PaintStyle;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.CanvasImage;
import org.timepedia.chronoscope.client.render.LinearGradient;

/**
 *
 */
public class MockLayer extends AbstractLayer {

  private String layerId;

  private Bounds bounds;

  private float alpha;

  private int order;

  private float transparency = 1.0f;

  private Color strokeColor;

  private boolean visible;

  private int composite;

  private Color fillColor;

  private RadialGradient radialGradient;

  private double lineWidth;

  private LinearGradient linearGradient;

  private CanvasPattern canvasPattern;

  public MockLayer(MockCanvas mockCanvas, String layerId, Bounds bounds) {
    super(mockCanvas);
    this.layerId = layerId;
    this.bounds = bounds;
  }

  public void dispose() {
    // ...
  }
  public void arc(double x, double y, double radius, double startAngle,
      double endAngle, int clockwise) {
  }

  public void beginPath() {
  }

  public void clearRect(double x, double y, double width, double height) {
  }

  public void clearTextLayer(String textLayer) {
  }

  public void closePath() {
  }

  public LinearGradient createLinearGradient(double startx, double starty,
      double endx, double endy) {
    return new LinearGradient() {

      public void addColorStop(double position, String color) {
      }
    };
  }

  public PaintStyle createPattern(String imageUri) {
    return new CanvasPattern() {

    };
  }

  public RadialGradient createRadialGradient(double x0, double y0, double r0,
      double x1, double y1, double r1) {
    return new RadialGradient() {
      public void addColorStop(double position, String color) {
      }
    };
  }

  public void drawImage(Layer layer, double x, double y, double width,
      double height) {
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
  }

  public void drawImage(CanvasImage image, double dx, double dy, double dwidth,
      double dheight) {
    
  }

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String textLayer,
      Cursor cursorStyle) {
  }

  public void fill() {
  }

  public void fillRect(double startx, double starty, double width,
      double height) {
  }

  public Bounds getBounds() {
    return bounds;
  }

  public void setBounds (Bounds bounds) {
    this.bounds = bounds;
  }
  public double getHeight() {
    return bounds.height;
  }

  public float getLayerAlpha() {
    return alpha;
  }

  public String getLayerId() {
    return layerId;
  }

  public int getLayerOrder() {
    return order;
  }

  public int getScrollLeft() {
    return 0;
  }

  public String getStrokeColor() {
    return strokeColor.toString();
  }

  public String getTransparency() {
    return "" + transparency;
  }

  public double getWidth() {
    return bounds.width;
  }

  public boolean isVisible() {
    return visible;
  }

  public void lineTo(double x, double y) {
  }

  public void moveTo(double x, double y) {
  }

  public void rect(double x, double y, double width, double height) {
  }

  public void restore() {
  }

  public void save() {
  }

  public void scale(double sx, double sy) {
  }

  public void setCanvasPattern(CanvasPattern canvasPattern) {
    this.canvasPattern = canvasPattern;
  }

  public void setComposite(int mode) {
    composite = mode;
  }

  public void setFillColor(String color) {
    fillColor = new Color(color);
  }

  public void setLayerAlpha(float alpha) {
    this.alpha = alpha;
  }

  public void setLayerOrder(int zorder) {
    this.order = zorder;
  }

  public void setLinearGradient(LinearGradient lingrad) {
    this.linearGradient = lingrad;
  }

  public void setLineWidth(double width) {
    this.lineWidth = width;
  }

  public void setRadialGradient(RadialGradient radialGradient) {
    this.radialGradient = radialGradient;
  }

  public void setScrollLeft(int i) {
  }

  public void setShadowBlur(double width) {
  }

  public void setShadowColor(String color) {
  }

  public void setShadowOffsetX(double x) {
  }

  public void setShadowOffsetY(double y) {
  }

  public void setStrokeColor(String color) {
    strokeColor = new Color(color);
  }

  public void setTextLayerBounds(String textLayer, Bounds textLayerBounds) {
  }

  public void setTransparency(float value) {
    transparency = value;
  }

  public void setVisibility(boolean visibility) {
    this.visible = visibility;
  }

  public int stringHeight(String string, String font, String bold,
      String size) {
    return 12;
  }

  public int stringWidth(String string, String font, String bold, String size) {
    return string.length() * 10;
  }

  public void stroke() {
  }

  public void translate(double x, double y) {
  }

  public void setFillColor(Color color) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setStrokeColor(Color color) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
