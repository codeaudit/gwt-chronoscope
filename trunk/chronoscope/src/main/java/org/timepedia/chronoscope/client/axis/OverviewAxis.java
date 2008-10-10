package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.OverviewAxisRenderer;

/**
 * An implementation of ValueAxis which renders a miniature zoomed-out overview
 * of the whole chart.
 */
public class OverviewAxis extends ValueAxis {

  private OverviewAxisRenderer renderer;

  private Bounds bounds;

  private XYPlot plot;

  private View view;
  
  public OverviewAxis(XYPlot plot, View view, AxisPanel panel, String title) {
    super(title, "");
    this.plot = plot;
    this.view = view;
    this.axisPanel = panel;
    this.renderer = new OverviewAxisRenderer();
    this.renderer.setView(view);
    this.renderer.setValueAxis(this);
    this.renderer.init();
  }

  // N/A
  public double dataToUser(double dataValue) {
    throw new UnsupportedOperationException();
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer
        .drawOverview(plot, layer, bounds = new Bounds(axisBounds), gridOnly);
  }

  public Bounds getBounds() {
    return bounds;
  }

  /**
   * Returns the bounds of the highlighted area of the overview axis, or null if
   * nothing is highlighted.
   */
  public Bounds getHighlightBounds() {
    return renderer.getHighlightBounds();
  }

  public double getHeight() {
    return renderer.getOverviewHeight();
  }

  public double getRangeHigh() {
    return plot.getDatasets().getMaxDomain();
  }

  public double getRangeLow() {
    return plot.getDatasets().getMinDomain();
  }

  public double getWidth() {
    return plot.getOverviewLayer().getWidth();
  }

  public void init() {
    // no-op
  }

  public final double userToData(double userValue) {
    // Use the userToData() implementation on the domain axis so that the 
    // user-to-data mapping function is consistent with this axis... but need
    // to pass in the overview-specific domain interval.
    double myRangeLow = getRangeLow();
    double myRangeHigh = getRangeHigh();
    return plot.getDomainAxis().userToData(myRangeLow, myRangeHigh, userValue);
  }

  protected void layout() {
    renderer = new OverviewAxisRenderer();
    renderer.init();
  }
}
