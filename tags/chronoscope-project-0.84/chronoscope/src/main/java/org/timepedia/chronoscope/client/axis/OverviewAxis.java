package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.OverviewAxisRenderer;

/**
 * An implementation of ValueAxis which renders a minature zoomed-out overview
 * of the whole chart
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

  public double getHeight() {
    return renderer.getOverviewHeight();
  }

  // N/A
  public double getRangeHigh() {
    return 0;
  }

  // N/A
  public double getRangeLow() {
    return 0;
  }

  public double getWidth() {
    return plot.getOverviewLayer().getWidth();
  }

  public void init() {
  }

  // N/A
  public double userToData(double screenPosition) {
    return 0;
  }
}
