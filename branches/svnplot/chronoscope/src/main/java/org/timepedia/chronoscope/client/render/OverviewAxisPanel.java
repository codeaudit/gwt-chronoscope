package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;

/**
 * Renders the overview axis.
 */
public class OverviewAxisPanel extends AxisPanel {

  private static final int MIN_OVERVIEW_HEIGHT = 60;
  
  private Bounds bounds = new Bounds();
  
  // The singleton avoids excess creation of Bounds objects
  private Bounds highlightBounds, highlightBoundsSingleton;
  
  public OverviewAxisPanel() {
    highlightBoundsSingleton = new Bounds();
  }
  
  public Bounds getBounds() {
    return this.bounds;
  }
  
  /**
   * Returns the bounds of the highlighted area of the overview axis, or
   * null if nothing is highlighted.
   */
  public Bounds getHighlightBounds() {
    return highlightBounds;
  }
  
  public void draw(Layer layer, Bounds axisBounds) {
    
    axisBounds.copyTo(bounds);
    
    Layer overviewLayer = plot.getOverviewLayer();

    layer.drawImage(overviewLayer, 0, 0, overviewLayer.getWidth(),
        overviewLayer.getHeight(), axisBounds.x, axisBounds.y, axisBounds.width,
        axisBounds.height);
    
    highlightBounds = calcHighlightBounds(plot, axisBounds);
    //GWT.log("TESTING: OverviewAxisRenderer: highlightBounds = " + highlightBounds, null);
    
    if (highlightBounds != null) {
      layer.save();
      layer.setFillColor(gssProperties.bgColor);
      layer.setTransparency((float) Math.max(0.5f, gssProperties.transparency));
      layer.fillRect(highlightBounds.x, highlightBounds.y,
          highlightBounds.width, highlightBounds.height);
      layer.setStrokeColor(gssProperties.color);
      layer.setTransparency(1.0f);
      layer.setLineWidth(gssProperties.lineThickness);
      layer.beginPath();
      // fix for Opera, on Firefox/Safari, rect() has implicit moveTo
      layer.moveTo(highlightBounds.x, highlightBounds.y + 1);  
      layer.rect(highlightBounds.x, highlightBounds.y + 1,
          highlightBounds.width, highlightBounds.height);
      layer.stroke();
      layer.setLineWidth(1);
      layer.restore();
      
      //plot.getChart().setCursor(Cursor.SELECTING);
    }
    else {
      //plot.getChart().setCursor(Cursor.DEFAULT);
    }
  }
  
  public String getType() {
    return "overview";
  }
  
  @Override 
  public double getWidth() {
    XYPlot plot = this.view.getChart().getPlot();
    return plot.getOverviewLayer().getWidth();
  }

  public String getTypeClass() {
    return null;
  }

  @Override
  protected void initHook() {
    height = gssProperties.height;
    if (height < MIN_OVERVIEW_HEIGHT) {
      height = MIN_OVERVIEW_HEIGHT;
    }
  }

  /*
   * Calculates the bounds of the highlighted area of the overview axis.
   * 
   * @return the bounds of the highlighted area, or <tt>null</tt> if no highlight
   * should be drawn.
   */
  private Bounds calcHighlightBounds(XYPlot plot, Bounds axisBounds) {
    double globalDomainMin = plot.getDatasets().getMinDomain();
    double globalDomainWidth = plot.getDatasets().getMaxDomain() - globalDomainMin;
    double visibleDomainMin = plot.getDomain().getStart();
    double visibleDomainWidth = plot.getDomain().length();
    
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
