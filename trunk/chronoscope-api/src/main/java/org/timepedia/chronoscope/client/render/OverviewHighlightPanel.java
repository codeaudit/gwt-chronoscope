package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.*;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.List;

/**
 * UI panel containing the overview highlight area focus region rendered above
 * the OverviewAxisPanel
 *
 */
public class OverviewHighlightPanel extends AbstractPanel implements
   GssElement, Exportable {

  // The singleton avoids excess creation of Bounds objects
  private Bounds highlightBoundsSingleton;

  private List<ZoomListener> listeners;

  protected XYPlot<?> plot;
  protected View view;

  public Layer maskLayer;
  public boolean highlightComputed = false;

  public OverviewHighlightPanel() {
    highlightBoundsSingleton = new Bounds();
    listeners = new ArrayList<ZoomListener>();
  }

  public void dispose() {
    super.dispose();
    listeners.clear();
    listeners = null;
    plot = null;
    view = null;
    highlightBoundsSingleton = null;
  }

  public void reset() {
    highlightBoundsSingleton = new Bounds();
    highlightComputed = false;
  }

  public void remove(Panel panel) {
    return; // no sub panels
  }

  public String getType() {
    return "overviewHighlight";
  }

  public String getTypeClass() {
    return null;
  }

  public final GssElement getParentGssElement() {
    return (OverviewAxisPanel)this.parent;
  }

  public Bounds getHighlightBounds() {
    if (highlightComputed) {
      return bounds;
    } else {
      drawHighlight();
      return bounds;
    }
  }

  public void init() {
    if (null == bounds) {
      bounds = plot.getOverlayLayer().getBounds();
    }
    if (null == layer) {
      layer = view.getCanvas().createLayer(Layer.OVERVIEW_SMALL_OVERLAY, bounds);
    }
    if (null == maskLayer) {
      maskLayer = view.getCanvas().createLayer(Layer.OVERVIEW_SMALL_MASK, bounds);
      initMaskLayer();
    }
  }

  public void addListener(ZoomListener l) {
    this.listeners.add(l);
  }

  public void clearDrawCaches() {
    highlightComputed = false;
  }

  public void initMaskLayer() {
    log("init mask");
    maskLayer.save();
    maskLayer.setBounds(plot.getOverviewAxisPanel().getLayer().getBounds());
    maskLayer.setVisibility(true);
    maskLayer.restore();
  }


  public void draw() {
    drawHighlight();
/*
    if (null != bounds) {
      layer.save();
      layer.setStrokeColor(gssProperties.color);
      layer.setFillColor(Color.TRANSPARENT);
      layer.fillRect(0, 0, bounds.width, bounds.height);
      layer.restore();
    }
*/
  }


  private void fillRectPixelAligned(double x, double y, double w, double h) {
    layer.fillRect(Math.floor(x), Math.floor(y), Math.ceil(w), Math.floor(h));
  }


  /**
   * Draw a Rectangle with different Line Thickness
   */
    private void drawRect(Layer layer){
//      double highlightBoundsX = highlightBounds.x;
//      double highlightBoundsY = highlightBounds.y;
//      double highlightBoundsXAddWidth = highlightBounds.x + highlightBounds.width;
//      double highlightBoundsYAddHeight = highlightBounds.y + highlightBounds.height;
//      List<RectLine> listLine = new ArrayList<RectLine>();

        // if borders < 0 use linethickness
        double borderTop = gssProperties.borderTop < 0 ? gssProperties.lineThickness : gssProperties.borderTop;
        double borderBottom = gssProperties.borderBottom < 0 ? gssProperties.lineThickness : gssProperties.borderBottom;
        double borderLeft = gssProperties.borderLeft < 0 ? gssProperties.lineThickness : gssProperties.borderLeft;
        double borderRight = gssProperties.borderRight < 0 ? gssProperties.lineThickness : gssProperties.borderRight;

        layer.setFillColor(gssProperties.color);

        // borderTop
        layer.setLineWidth(borderTop);
        fillRectPixelAligned(bounds.x, bounds.y,
                              bounds.width + borderLeft/2 + borderRight/2, borderTop);

        // borderBottom
        layer.setLineWidth(borderBottom);
        fillRectPixelAligned(bounds.x, bounds.y + bounds.height - borderBottom,
                              bounds.width + borderLeft/2 + borderRight/2, borderBottom);

        // borderLeft
        layer.setLineWidth(borderLeft);
        fillRectPixelAligned(bounds.x - borderLeft/2, bounds.y,
                              borderLeft, bounds.height );

        // borderRight
        layer.setLineWidth(borderRight);
        fillRectPixelAligned(bounds.x + bounds.width + borderRight/2, bounds.y,
                              borderRight, bounds.height );
     }


  /*
   * Calculates the bounds of the highlighted area of the overview axis.
   *
   */

  private void drawHighlight() {

    double globalDomainMin = plot.getWidestDomain().getStart();
    double globalDomainWidth = plot.getWidestDomain().length();
    double visibleDomainMin = plot.getDomain().getStart();
    double visibleDomainWidth = plot.getDomain().length();

    Bounds overviewBounds = plot.getOverviewAxisPanel().getBounds();

    // TODO - use same calc as limiting zoom out, rather than just x%
    // TODO - global bounds should allow edge point +max radii for max(hover,focus,etc)
    if (!plot.getOverviewAxisPanel().visible || ((globalDomainWidth - .05*visibleDomainWidth) <= visibleDomainWidth)) {
      // The viewport (i.e. the portion of the domain that is visible within the
      // plot area) is at least as wide as the global domain, so don't highlight.
      if (layer.isVisible()) {
        layer.save();
        layer.setVisibility(false);
        layer.restore();
      }

      if (maskLayer.isVisible()) {
        maskLayer.save();
        maskLayer.setVisibility(false);
        maskLayer.restore();
      }
      // return null;
    } else {
      bounds = highlightBoundsSingleton;
      bounds.x = Math.floor(overviewBounds.x + ((visibleDomainMin - globalDomainMin) / globalDomainWidth * overviewBounds.width));
      bounds.y = overviewBounds.y;
      bounds.height = overviewBounds.height;
      bounds.width = Math.floor(overviewBounds.width * visibleDomainWidth/globalDomainWidth);

      layer.save();

      layer.setBounds(bounds);
      if (!layer.isVisible()){
        layer.setVisibility(true);
      }

      layer.restore();


      maskLayer.save();

      if (!overviewBounds.equals(maskLayer.getBounds())) {
        maskLayer.setBounds(overviewBounds);
      }
      // FIXME - mask should take advantage of accelerated layers/compositing
      //  ie it should be sliding divs not two canvas fills
      log("drawing mask layer "+maskLayer.getLayerId()+maskLayer.getBounds());
      maskLayer.setTransparency(0.5f);
      maskLayer.setFillColor(new Color(222, 222, 222, 64));
      maskLayer.fillRect(0, 5, bounds.x - overviewBounds.x, overviewBounds.height);
      maskLayer.fillRect(bounds.x - overviewBounds.x + bounds.width, 5,
              overviewBounds.width - ((bounds.x - overviewBounds.x) + bounds.width), overviewBounds.height);
      if (!maskLayer.isVisible()) {
        maskLayer.setVisibility(true);
      }

      maskLayer.restore();
    }
    highlightComputed = true;
  }

  public void setPlot(XYPlot<?> plot) {
    this.plot = plot;
  }

  public void setView(View view) {
    this.view = view;
  }

  private static void log(String msg) {
    System.out.println("OverviewHighlightPanel> "+msg);
  }
}
