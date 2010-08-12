package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChartDragEndEvent extends PlotEvent<ChartDragEndHandler> implements Exportable {

  public static Type<ChartDragEndHandler> TYPE
      = new Type<ChartDragEndHandler>();

  private int endX;

  public ChartDragEndEvent(XYPlot plot, int endX) {
    super(plot);
    this.endX = endX;
  }

  public int getEndX() {
    return endX;
  }

  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(ChartDragEndHandler chartDragEndHandler) {
    chartDragEndHandler.onDragEnd(this);
  }
}