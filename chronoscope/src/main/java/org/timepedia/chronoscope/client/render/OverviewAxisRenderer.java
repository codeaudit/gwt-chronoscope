package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Rendering code used to render OverviewAxis
 */
public class OverviewAxisRenderer implements AxisRenderer, GssElement {

  private static final int MIN_OVERVIEW_HEIGHT = 65;

  private int overviewHeight;

  private GssProperties axisProperties;

  private OverviewAxis axis;
  
  private Bounds highlightBoundsSingleton;
  
  public OverviewAxisRenderer() {
    highlightBoundsSingleton = new Bounds();
  }
  
  public void drawOverview(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    
    Layer overviewLayer = plot.getOverviewLayer();
    clearAxis(layer, axis, axisBounds);

    layer.drawImage(overviewLayer, 0, 0, overviewLayer.getWidth(),
        overviewLayer.getHeight(), axisBounds.x, axisBounds.y, axisBounds.width,
        axisBounds.height);
    
    Bounds highlightBounds = calcHighlightBounds(plot, axisBounds);
    if (highlightBounds != null) {
      layer.save();
      layer.setFillColor(axisProperties.bgColor);
      layer.setTransparency((float) Math.max(0.5f, axisProperties.transparency));
      layer.fillRect(highlightBounds.x, highlightBounds.y,
          highlightBounds.width, highlightBounds.height);
      layer.setStrokeColor(axisProperties.color);
      layer.setTransparency(1.0f);
      layer.setLineWidth(axisProperties.lineThickness);
      layer.beginPath();
      // fix for Opera, on Firefox/Safari, rect() has implicit moveTo
      layer.moveTo(highlightBounds.x, highlightBounds.y + 1);  
      layer.rect(highlightBounds.x, highlightBounds.y + 1,
          highlightBounds.width, highlightBounds.height);
      layer.stroke();
      layer.setLineWidth(1);
      layer.restore();
    }
  }

  public int getOverviewHeight() {
    return overviewHeight;
  }

  public GssElement getParentGssElement() {
    return axis.getAxisPanel();
  }

  public String getType() {
    return "overview";
  }

  public String getTypeClass() {
    return null;
  }

  public void init(XYPlot plot, OverviewAxis overviewAxis) {
    if (axisProperties == null) {
      axis = overviewAxis;

      axisProperties = plot.getChart().getView().getGssProperties(this, "");
      overviewHeight = axisProperties.height;
      if (overviewHeight < MIN_OVERVIEW_HEIGHT) {
        overviewHeight = MIN_OVERVIEW_HEIGHT;
      }
    }
  }

  private void clearAxis(Layer layer, OverviewAxis axis, Bounds bounds) {
  }
  
  /*
   * Calculates the bounds of the highlighted area of the overview axis.
   * 
   * @return the bounds of the highlighted area, or <tt>null</tt> if no highlight
   * should be drawn.
   */
  private Bounds calcHighlightBounds(XYPlot plot, Bounds axisBounds) {
    double globalDomainMin = plot.getDomainMin();
    double visibleDomainMin = plot.getDomainOrigin();
    double globalDomainWidth = plot.getDomainMax() - globalDomainMin;
    double visibleDomainWidth = plot.getCurrentDomain();
    
    Bounds b;
    
    if (globalDomainWidth <= visibleDomainWidth) {
      // The viewport (i.e. the portion of the domain that is visible within the
      // plot area) is at least as wide as the global domain, so don't highlight.
      b = null;
    }
    else {
      double beginHighlight = axisBounds.x +
          ((visibleDomainMin - globalDomainMin) / globalDomainWidth * axisBounds.width);
      beginHighlight = Math.max(beginHighlight, axisBounds.x);
      
      double endHighlight = axisBounds.x +
          ((visibleDomainMin - globalDomainMin + visibleDomainWidth) / globalDomainWidth * axisBounds.width);
      endHighlight = Math.min(endHighlight, axisBounds.x + axisBounds.width);
      
      b = highlightBoundsSingleton;
      b.x = beginHighlight;
      b.y = axisBounds.y;
      b.width = endHighlight - beginHighlight;
      b.height = axisBounds.height;
    }
    
    return b;
  }
}
