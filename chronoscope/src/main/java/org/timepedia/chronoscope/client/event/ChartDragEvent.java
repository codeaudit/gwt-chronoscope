package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChartDragEvent extends PlotEvent<ChartDragHandler> implements Exportable {

  public static Type<ChartDragHandler> TYPE
      = new Type<ChartDragHandler>();
  private int currentX;

  public ChartDragEvent(XYPlot plot, int currentX) {
    super(plot);
    this.currentX = currentX;
  }

  public int getCurrentX() {
    return currentX;
  }

  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(ChartDragHandler chartDragHandler) {
    chartDragHandler.onDrag(this);
  }
}