package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.LegendAxisRenderer;

/**
 * An implementation of ValueAxis to render a chart Legend.
 */
public class LegendAxis extends ValueAxis {

  private LegendAxisRenderer renderer;

  private XYPlot plot;

  public LegendAxis(XYPlot plot, AxisPanel panel, String title) {
    super(title, "");
    this.plot = plot;
    this.axisPanel = panel;
    this.renderer = new LegendAxisRenderer(this);
  }

  public boolean click(int x, int y) {
    return renderer.click(plot, x, y);
  }

  public double dataToUser(double dataValue) {
    return 0;
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer.drawLegend(plot, layer, axisBounds, gridOnly);
  }

  public double getHeight() {
    Layer layer = plot.getChart().getView().getCanvas().getRootLayer();
    return renderer.getHeight(plot, layer);
  }

  public double getRangeHigh() {
    throw new UnsupportedOperationException();
  }

  public double getRangeLow() {
    throw new UnsupportedOperationException();
  }

  public double getWidth() {
    return plot.getChart().getView().getWidth();
  }

  public void init() {
    renderer.init(plot, this);
  }

  protected void layout() {
    renderer = new LegendAxisRenderer(this);
    renderer.init(plot, this);
  }
}
