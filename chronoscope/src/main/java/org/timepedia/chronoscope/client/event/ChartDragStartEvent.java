package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChartDragStartEvent extends PlotEvent<ChartDragStartHandler> implements Exportable {

  public static Type<ChartDragStartHandler> TYPE
      = new Type<ChartDragStartHandler>();

  private int startX;

  public ChartDragStartEvent(XYPlot plot, int startX) {
    super(plot);
    this.startX = startX;
  }

  public int getStartX() {
    return startX;
  }

  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(ChartDragStartHandler chartDragStartHandler) {
    chartDragStartHandler.onDragStart(this);
  }
}

