package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Fired by plot implementations when click occurs on chart which is not handled
 * by a marker or by a focus-on-point click event.
 */
@ExportPackage("chronoscope")
public class ChartClickEvent extends PlotEvent<ChartClickHandler> implements Exportable {

  public static Type<ChartClickHandler> TYPE
      = new Type<ChartClickHandler>();

  private int x;

  @Export
  /**
   * X coordinate of click event relative to left border of plot area.
   */
  public double getX() {
    return x - getPlot().getBounds().x;
  }

  /**
   * Y coordinate of click event relative to top border of plot area.
   */
  @Export
  public double getY() {
    return y - getPlot().getBounds().y;
  }

  private int y;

  public ChartClickEvent(XYPlot plot, int x, int y) {
    super(plot);
    this.x = x;
    this.y = y;
  }
  
  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(ChartClickHandler chartClickHandler) {
    chartClickHandler.onChartClick(this);
  }
}