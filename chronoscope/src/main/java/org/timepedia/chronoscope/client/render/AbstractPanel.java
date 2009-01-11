package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Bounds;
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
  protected Bounds bounds = new Bounds();
  protected Layer layer;
  
  public final void setGssProperties(GssProperties gssProperties) {
    this.gssProperties = gssProperties;
  }
  
  public final String getTextLayerName() {
    return this.textLayerName;
  }
  
  public final void setLayer(Layer layer) {
    this.layer = layer;
  }
  
  public final void setTextLayerName(String textLayerName) {
    this.textLayerName = textLayerName;
  }

  public final Bounds getBounds() {
    return this.bounds;
  }

  public final void setPosition(double x, double y) {
    bounds.setPosition(x, y);
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
