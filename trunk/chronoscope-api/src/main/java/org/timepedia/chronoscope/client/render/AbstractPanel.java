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
  protected String textLayerName;
  protected StringSizer stringSizer;
  protected Panel parent;

  private double layerOffsetX, layerOffsetY;

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
    this.layer = layer;
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
    bounds.setPosition(x, y);
    
    Panel parentPanel = getParent();
    layerOffsetX = x + parentPanel.getLayerOffsetX();
    layerOffsetY = y + parentPanel.getLayerOffsetY();
  }

  public void layout() {
  }

  public final void setStringSizer(StringSizer stringSizer) {
    this.stringSizer = stringSizer;
  }
  
  public String toString() {
    String layerBounds = (null==layer) ? "null" : layer.getBounds().toString();
    return "bounds=" + this.bounds +
        "; layerOffset=(" + layerOffsetX + ", " + layerOffsetY + ")" +
        "; layerBounds=" + layerBounds;
  }
}
