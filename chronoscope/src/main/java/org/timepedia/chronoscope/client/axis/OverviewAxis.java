package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.OverviewAxisRenderer;

/**
 * An implementation of ValueAxis which renders a miniature zoomed-out overview
 * of the whole chart.
 */
public class OverviewAxis extends ValueAxis {

  private final OverviewAxisRenderer renderer;

  private Bounds bounds;

  private DefaultXYPlot plot;

  public OverviewAxis(DefaultXYPlot plot, AxisPanel panel, String title) {
    super(plot.getChart(), title, "");
    this.plot = plot;
    setAxisPanel(panel);
    renderer = new OverviewAxisRenderer();
    renderer.init(plot, this);
  }

  // N/A
  public double dataToUser(double dataValue) {
    return 0;
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer
        .drawOverview(plot, layer, bounds = new Bounds(axisBounds), gridOnly);
  }

  public void drawAxis(View view, Layer layer, Bounds axisBounds,
      String panelName, int position, boolean gridOnly) {
    bounds = new Bounds(axisBounds);
  }

  public Bounds getBounds() {
    return bounds;
  }
  
  /**
   * Returns the bounds of the highlighted area of the overview axis, or
   * null if nothing is highlighted.
   */
  public Bounds getHighlightBounds() {
    return renderer.getHighlightBounds();
  }

  public double getHeight() {
    return renderer.getOverviewHeight();
  }

  // N/A
  public double getRangeHigh() {
    throw new UnsupportedOperationException();
  }

  // N/A
  public double getRangeLow() {
    throw new UnsupportedOperationException();
  }

  public double getWidth() {
    return plot.getOverviewLayer().getWidth();
  }

  public void init() {
    // no-op
  }

  // N/A
  public double userToData(double screenPosition) {
    throw new UnsupportedOperationException();
  }
}
