/**
 * 
 */
package org.timepedia.chronoscope.client.render;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * UI panel containing the clickable zoom levels (e.g. "1d 5d 1m 6m 1y ...").
 * Listeners interested in knowing when the user clicks on a zoom level
 * should register themselves via {link {@link #addListener(ZoomListener)}.
 * 
 * @author Chad Takahashi
 */
public class ZoomPanel {
  
  // TODO: Add a getBounds() method
  
  
  private static final int MAX_ZOOM_LINKS = 20;

  private static final String SPACE = "\u00A0";
  private static final String ZOOM_PREFIX = "Zoom:";

  private Bounds bounds;
  private int fullZoomStringWidth;
  private GssProperties gssProperties;
  private List<ZoomListener> listeners;
  private int spaceWidth = -1;
  private String textLayerName;
  private int zoomLinkHeight = -1;
  private int[] zoomLinkWidths = new int[MAX_ZOOM_LINKS];
  private int zoomPrefixWidth = -1;
  private ZoomIntervals zooms;

  public ZoomPanel() {
    this.listeners = new ArrayList<ZoomListener>();
  }

  public void setZoomIntervals(ZoomIntervals zooms) {
    this.zooms = zooms;
  }
  
  public void setTextLayerName(String textLayerName) {
    this.textLayerName = textLayerName;
  }

  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
  }

  public void setGssProperties(GssProperties gssProperties) {
    this.gssProperties = gssProperties;
  }

  public void addListener(ZoomListener l) {
    this.listeners.add(l);
  }

  public boolean click(int x, int y) {
    ZoomInterval zoom = detectHit(x, y);
    boolean hitDetected = (zoom != null);

    if (hitDetected) {
      for (ZoomListener listener : listeners) {
        listener.onZoom(zoom.getInterval());
      }
    }

    return hitDetected;
  }

  public void draw(Layer layer) {
    layer.setStrokeColor(gssProperties.color);

    double zx = bounds.x;
    double zy = bounds.y;

    computeMetrics(layer);
    drawZoomLink(layer, zx, zy, ZOOM_PREFIX, false);
    zx += zoomPrefixWidth + spaceWidth;

    int i = 0;
    for (ZoomInterval zoom : zooms) {
      drawZoomLink(layer, zx, zy, zoom.getName(), true);
      zx += zoomLinkWidths[i++] + spaceWidth;
    }
  }

  /**
   * Calculates the screen width for the specified string.
   */
  private int calcWidth(String s, Layer layer) {
    return layer.stringWidth(s, gssProperties.fontFamily,
        gssProperties.fontWeight, gssProperties.fontSize);
  }

  private void computeMetrics(Layer layer) {
    boolean isInitialized = (zoomLinkHeight != -1);
    
    if (!isInitialized) {
      spaceWidth = calcWidth(SPACE, layer) + 1; // Question: Whats "+1" for?
      zoomPrefixWidth = calcWidth(ZOOM_PREFIX, layer);
  
      fullZoomStringWidth = zoomPrefixWidth;
      int i = 0;
      for (ZoomInterval zoom : zooms) {
        fullZoomStringWidth += spaceWidth;
        int w = calcWidth(zoom.getName(), layer);
        zoomLinkWidths[i++] = w;
        fullZoomStringWidth += w;
      }
  
      zoomLinkHeight = layer.stringHeight("X", gssProperties.fontFamily,
          gssProperties.fontWeight, gssProperties.fontSize);
    }
  }

  private void drawZoomLink(Layer layer, double zx, double zy, String label,
      boolean clickable) {
    layer.drawText(zx, zy, label, gssProperties.fontFamily,
        gssProperties.fontWeight, gssProperties.fontSize, textLayerName,
        clickable ? Cursor.CLICKABLE : Cursor.DEFAULT);
  }

  /**
   * If (x,y) falls on a zoom link, then return the corresponding ZoomInterval, or null if nothing was "hit".
   */
  private ZoomInterval detectHit(int x, int y) {
    /*
    if (zoomLinkHeight == -1) {
      Layer layer = plot.getChart().getView().getCanvas().getRootLayer();
      computeMetrics(layer, true);
    }
     */

    if (!MathUtil.isBounded(y, bounds.y, bounds.y + zoomLinkHeight)) {
      return null;
    }

    double bx = bounds.x;
    double be = bounds.x + fullZoomStringWidth;
    if (!MathUtil.isBounded(x, bx, be)) {
      return null;
    }

    // Move cursor to the 1st zoom link
    bx = bounds.x + zoomPrefixWidth + spaceWidth;

    // Rifle through the zoom links and see if any of them were clicked on.
    int i = 0;
    for (ZoomInterval zoom : zooms) {
      be = bx + this.zoomLinkWidths[i++];
      if (MathUtil.isBounded(x, bx, be)) {
        return zoom;
      }
      bx = be + spaceWidth;
    }

    return null;
  }
}
