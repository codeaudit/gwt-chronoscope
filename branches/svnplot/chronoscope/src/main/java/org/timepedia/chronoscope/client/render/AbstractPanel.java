package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Skeletal implementation of {@link Panel}, which also provides some helper
 * methods that subclasses will typically need.
 * 
 * @author Chad Takahashi
 */
public abstract class AbstractPanel implements Panel {
  protected GssProperties gssProperties;
  protected String textLayerName;
  protected double x, y, width, height;
  
  public final void setGssProperties(GssProperties gssProperties) {
    this.gssProperties = gssProperties;
  }
  
  public final void setTextLayerName(String textLayerName) {
    this.textLayerName = textLayerName;
  }

  public double getHeight() {
    return height;
  }

  public double getWidth() {
    return width;
  }

  public void setLocation(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public void resizeToIdealWidth() {
    throw new UnsupportedOperationException();
  }

  public void resizeToMinimalWidth() {
    throw new UnsupportedOperationException();
  }

  protected final int calcHeight(String s, Layer layer) {
    GssProperties gss = gssProperties;
    return layer.stringHeight(s, gss.fontFamily, gss.fontWeight, gss.fontSize);
  }

  protected final int calcWidth(String s, Layer layer) {
    GssProperties gss = gssProperties;
    return layer.stringWidth(s, gss.fontFamily, gss.fontWeight, gss.fontSize);
  }
  
}
