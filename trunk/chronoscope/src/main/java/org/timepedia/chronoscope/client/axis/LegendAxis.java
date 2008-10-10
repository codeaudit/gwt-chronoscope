package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.LegendAxisRenderer;
import org.timepedia.chronoscope.client.render.ZoomListener;

/**
 * An implementation of ValueAxis to render a chart Legend.
 */
public class LegendAxis extends ValueAxis {
  private LegendAxisRenderer renderer;
  private XYPlot plot;
  private View view;
  private ZoomListener zoomListener;
  
  public LegendAxis(XYPlot plot, AxisPanel panel, ZoomListener zoomListener, 
       String title) {
    
    super(title, "");
    this.plot = plot;
    this.view = plot.getChart().getView();
    this.zoomListener = zoomListener;
    this.axisPanel = panel;
    this.renderer = new LegendAxisRenderer(this);
  }

  public boolean click(int x, int y) {
    return renderer.click(x, y);
  }

  public double dataToUser(double dataValue) {
    throw new UnsupportedOperationException();
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer.drawLegend(plot.getDomain(), layer, axisBounds, gridOnly);
  }

  public double getHeight() {
    return renderer.getHeight();
  }

  public double getRangeHigh() {
    throw new UnsupportedOperationException();
  }

  public double getRangeLow() {
    throw new UnsupportedOperationException();
  }

  public double getWidth() {
    return view.getWidth();
  }

  public void init() {
    renderer.init(plot, view, zoomListener);
  }

  protected void layout() {
    renderer = new LegendAxisRenderer(this);
    renderer.init(plot, view, zoomListener);
  }
}
