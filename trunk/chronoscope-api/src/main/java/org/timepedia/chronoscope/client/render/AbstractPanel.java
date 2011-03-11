package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;

import java.util.Collections;
import java.util.List;

/**
 * Skeletal implementation of {@link Panel}, which also provides some helper
 * methods that subclasses will typically need.
 * 
 * @author Chad Takahashi
 */
@ExportPackage("chronoscope")
public abstract class AbstractPanel implements Panel, Exportable {
  protected Bounds bounds = new Bounds();
  protected GssProperties gssProperties;
  protected Layer layer;  
  protected String textLayerName;  // TODO - eliminate textlayers and just have layers
  protected Panel parent;

  private double layerOffsetX, layerOffsetY;

  public void dispose() {
    if (null != layer) {
      // layer.clearTextLayer(getTextLayerName());
      layer.dispose();
      layer = null;
    }
    if (null != parent) {
      parent.remove(this);
      parent = null;
    }
    gssProperties = null;
    textLayerName = null;
  }

  public void reset() {
    log("reset");
    this.layer = null;
    this.bounds = new Bounds();
  }

  public final void setGssProperties(GssProperties gssProperties) {
    this.gssProperties = gssProperties;
  }
  
  public int getChildCount() {
    return getChildren().size();
  }
  
  public List<Panel> getChildren() {
    return Collections.emptyList();
  }
  
  public Layer getLayer() {
    return this.layer;
  }
  
  public double getLayerOffsetX() {
    return this.layerOffsetX;
  }

  public double getLayerOffsetY() {
    return this.layerOffsetY;
  }

  public final Panel getParent() {
    return this.parent;
  }
  
  public final String getTextLayerName() {
    return this.textLayerName;
  }
  
  public void setLayer(Layer layer) {
    if (null == layer) { return; } else
    if (layer.equals(this.layer)) { return; } else
    if (this.layer != null) {
      log(this.layer.getLayerId()+".dispose()");
      this.layer.dispose();
    }
    log("setLayer "+layer.getLayerId() + " layer.bounds: "+layer.getBounds() + " bounds:"+bounds);

    this.layer = layer;
    this.textLayerName = layer.getLayerId();
  }
  
  public void setLayerOffset(double x, double y) {
    this.layerOffsetX = x;
    this.layerOffsetY = y;
  }
  
  public final void setParent(Panel parent) {
    this.parent = parent;
  }
  
  public final void setTextLayerName(String textLayerName) {
    this.textLayerName = textLayerName;
  }

  public final Bounds getBounds() {
    return this.bounds;
  }

  public final void setPosition(double x, double y) {
    log("setPosition "+x+", "+y);
    bounds.setPosition(x, y);
    if (null == layer) { return; }

    layer.save();

    Bounds lb = layer.getBounds();
    if (!bounds.equals(lb)) {
      lb.setPosition(x,y);
      layer.setBounds(lb);
    }

    layer.restore();

    // Panel parentPanel = getParent();
    // layerOffsetX = x + parentPanel.getLayerOffsetX();
    // layerOffsetY = y + parentPanel.getLayerOffsetY();
    // log ("setPosition "+parentPanel.getBounds() +" offset:"
       //     + parentPanel.getLayerOffsetX()+ "," +parentPanel.getLayerOffsetY());
    // log ("setPosition offset:"+layerOffsetX + ", "+layerOffsetY);
  }

  public void layout() { }

  public String toString() {
    String layerBounds = (null==layer) ? "null" : layer.getBounds().toString();
    return "bounds=" + this.bounds +
        "; layerOffset=(" + layerOffsetX + ", " + layerOffsetY + ")" +
        "; layerBounds=" + layerBounds;
  }

  private static void log (String msg) {
    System.out.println("AbstractPanel>  "+msg);
  }

}
