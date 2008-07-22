package org.timepedia.chronoscope.client.render;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.axis.LegendAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.TimeUnit;

/**
 * Renderer used to draw Legend.
 */
public class LegendAxisRenderer implements AxisRenderer, GssElement,
    ZoomListener {

  /**
   * Dictates the Y-padding between the top of the legend item bounds
   * and the bottom of the zoom and date range panels.
   */
  private static final int LEGEND_Y_TOP_PAD = 3;

  private LegendAxis axis;

  private GssProperties axisProperties;

  private Bounds bounds;

  private GssProperties labelProperties;

  private String textLayerName;

  private DatasetLegendPanel dsLegendPanel;
  
  private ZoomPanel zoomPanel;

  private DateRangePanel dateRangePanel;

  private XYPlot plot;

  public LegendAxisRenderer(LegendAxis axis) {
    ArgChecker.isNotNull(axis, "axis");
    this.axis = axis;
  }

  public boolean click(XYPlot plot, int x, int y) {
    zoomPanel.setLocation(bounds.x, bounds.y);
    return zoomPanel.click(x, y);
  }

  public void drawLegend(XYPlot xyplot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    
    DefaultXYPlot plot = (DefaultXYPlot) xyplot;
    final int labelHeight = (int)this.zoomPanel.height;

    copyState(axisBounds, bounds);
    clearAxis(layer, axisBounds);
    
    // Position and size the panels
    zoomPanel.setLocation(axisBounds.x, axisBounds.y);
    double startDate = plot.getDomainOrigin();
    double endDate = startDate + plot.getCurrentDomain();
    dateRangePanel.updateDomainInterval(startDate, endDate);
    rightJustify(dateRangePanel, axisBounds);
    layoutPanels(axisBounds);
    dsLegendPanel.setLocation(axisBounds.x, axisBounds.y + labelHeight + LEGEND_Y_TOP_PAD);
    
    // Draw the panels
    zoomPanel.draw(layer);
    dateRangePanel.draw(layer);
    dsLegendPanel.draw(layer);
  }
  
  /**
   * Returns the total height of the rendered legend axis
   */
  public double getHeight(XYPlot plot, Layer layer, Bounds axisBounds) {
    double totalHeight = 0;
    totalHeight += zoomPanel.getHeight();
    totalHeight += LEGEND_Y_TOP_PAD;
    totalHeight += dsLegendPanel.getHeight();

    // Issue #41: For now, we add a LEGEND_Y_BOTTOM_PAD that's tall enough to
    // allow for the possibility of an extra row of legend labels. This is to
    // account for the case where the user hovers over a dataset point, causing
    // the corresponding range value to be appended to the legend label, which
    // in some cases could cause the remaining legend labels to run over into a
    // new row.
    totalHeight += zoomPanel.getHeight();

    return totalHeight;
  }

  public GssElement getParentGssElement() {
    return axis.getAxisPanel();
  }

  public String getType() {
    return "axislegend";
  }

  public String getTypeClass() {
    return null;
  }

  public void init(XYPlot plot, LegendAxis axis) {
    View view = plot.getChart().getView();
    
    if (axisProperties == null) {
      axisProperties = view.getGssProperties(this, "");
      labelProperties = view.getGssProperties(
          new GssElementImpl("label", this), "");
      textLayerName = axis.getAxisPanel().getPanelName()
          + axis.getAxisPanel().getAxisNumber(axis);
      
      ZoomIntervals zoomIntervals = createDefaultZoomIntervals();
      final double approxMinInterval = Math.max(0, calcApproxMinInterval(plot));
      zoomIntervals.applyFilter(plot.getDomainMin(), plot.getDomainMax(), approxMinInterval);
      
      Layer rootLayer = view.getCanvas().getRootLayer();
      
      dsLegendPanel = new DatasetLegendPanel();
      dsLegendPanel.setPlot((DefaultXYPlot)plot);
      dsLegendPanel.setGssProperties(labelProperties);
      dsLegendPanel.setTextLayerName(textLayerName);
      dsLegendPanel.init(rootLayer);
      
      zoomPanel = new ZoomPanel();
      zoomPanel.setGssProperties(labelProperties);
      zoomPanel.setTextLayerName(textLayerName);
      zoomPanel.addListener(this);
      zoomPanel.setZoomIntervals(zoomIntervals);
      zoomPanel.init(rootLayer);
      
      dateRangePanel = new DateRangePanel();
      dateRangePanel.setGssProperties(labelProperties);
      dateRangePanel.setTextLayerName(textLayerName);
      dateRangePanel.init(rootLayer);

      dateRangePanel.updateDomainInterval(plot.getDomainMin(),
          plot.getDomainMax());

      this.plot = plot;
      this.bounds = new Bounds();
    }
  }

  public void onZoom(double intervalInMillis) {
    if (intervalInMillis == Double.MAX_VALUE) {
      plot.maxZoomOut();
    } else {
      double cd = intervalInMillis;
      double dc = plot.getDomainOrigin() + plot.getCurrentDomain() / 2;
      plot.animateTo(dc - cd / 2, cd, XYPlotListener.ZOOMED, null);
    }
  }

  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    layer.setFillColor(axisProperties.bgColor);
    layer.translate(-1, bounds.y - 1);
    layer.scale(layer.getWidth() + 1, bounds.height + 1);
    layer.beginPath();
    layer.rect(0, 0, 1, 1);
    layer.fill();
    layer.restore();
    layer.clearTextLayer(textLayerName);
  }

  private static void copyState(Bounds source, Bounds target) {
    target.x = source.x;
    target.y = source.y;
    target.height = source.height;
    target.width = source.width;
  }

  private static ZoomIntervals createDefaultZoomIntervals() {
    ZoomIntervals zooms = new ZoomIntervals();
    
    zooms.add(new ZoomInterval("1d", TimeUnit.DAY.ms()));
    zooms.add(new ZoomInterval("5d", TimeUnit.DAY.ms() * 5));
    zooms.add(new ZoomInterval("1m", TimeUnit.MONTH.ms()));
    zooms.add(new ZoomInterval("3m", TimeUnit.MONTH.ms() * 3));
    zooms.add(new ZoomInterval("6m", TimeUnit.MONTH.ms() * 6));
    zooms.add(new ZoomInterval("1y", TimeUnit.YR.ms()));
    zooms.add(new ZoomInterval("5y", TimeUnit.YR.ms() * 5));
    zooms.add(new ZoomInterval("10y", TimeUnit.DECADE.ms()));
    zooms.add(new ZoomInterval("100y", TimeUnit.CENTURY.ms()));
    zooms.add(new ZoomInterval("1000y", TimeUnit.MILLENIUM.ms()));
    zooms.add(new ZoomInterval("max", Double.MAX_VALUE).filterExempt(true));
    
    return zooms;
  }
  
  /**
   * Currently, this method naively lays out the ZoomPanel and
   * DateRangePanel on the X-axis.  Ultimately, layout rules and 
   * heuristics will be split out into a separate LayoutStrategy 
   * interface of some sort.
   */
  private void layoutPanels(Bounds parentBounds) {
    // The minimum distance allowed between the zoom panel and the dataset 
    // legend panel.
    final int minCushion = 3;
    
    double parentWidth = parentBounds.width;
    
    dateRangePanel.resizeToIdealWidth();
    zoomPanel.resizeToIdealWidth();
    zoomPanel.show(true);
    double idealZoomPanelWidth = zoomPanel.getWidth();
    
    // First, see if the panels in their prettiest form will 
    //fit within the container's bounds
    double cushion = parentWidth - zoomPanel.getWidth() - dateRangePanel.getWidth();
    if (cushion >= minCushion) {
      return;
    }
    
    // Doesn't fit? Then compress only the date range panel
    dateRangePanel.resizeToMinimalWidth();
    rightJustify(dateRangePanel, parentBounds);
    cushion = parentWidth - idealZoomPanelWidth - dateRangePanel.getWidth();
    if (cushion >= minCushion) {
      return;
    }
    
    // Still doesn't fit? Then compress only the zoom link panel
    zoomPanel.resizeToMinimalWidth();
    dateRangePanel.resizeToIdealWidth();
    rightJustify(dateRangePanel, parentBounds);
    cushion = parentWidth - zoomPanel.getWidth() - dateRangePanel.getWidth();
    if (cushion >= minCushion) {
      return;
    }
    
    // Still doesn't fit? Then compress both panels
    dateRangePanel.resizeToMinimalWidth();
    rightJustify(dateRangePanel, parentBounds);
    cushion = parentWidth - zoomPanel.getWidth() - dateRangePanel.getWidth();
    if (cushion >= minCushion) {
      return;
    }
    
    // Still doesn't fit? Then hide the zoom links completely.
    zoomPanel.show(false);
  }
  
  private void rightJustify(Panel p, Bounds parentBounds) {
    p.setLocation(parentBounds.x + parentBounds.width - dateRangePanel.getWidth() - 2, parentBounds.y);
  }
  
  /**
   * Determines the approximate minimum domain interval across all datasets in the
   * specified plot.
   */
  private static double calcApproxMinInterval(XYPlot plot) {
    double min = Double.MAX_VALUE;
    for (int i = 0; i < plot.getNumDatasets(); i++) {
      min = Math.min(min, plot.getDataset(i).getApproximateMinimumInterval());
    }
    return min;
  }
  
  /**
   * For debugging purposes
   */
  private static void hiliteBounds(Bounds b, Layer layer) {
    layer.save();
   
    layer.setLayerOrder(1);
    //layer.setTransparency(.35f);
    layer.setFillColor("#50D0FF");
    layer.fillRect(b.x, b.y, b.width, b.height);

    layer.restore();
  }
  
  /**
   * For debugging purposes
   */
  private static void hiliteBounds(Panel p, Layer layer) {
    Bounds b = new Bounds(p.getX(), p.getY(), p.getWidth(), p.getHeight());
    hiliteBounds(b, layer);
  }
  
}
