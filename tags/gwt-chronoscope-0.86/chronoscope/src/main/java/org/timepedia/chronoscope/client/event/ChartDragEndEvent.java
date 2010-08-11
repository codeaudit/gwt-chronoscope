package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChartDragEndEvent extends PlotEvent implements Exportable {

  public static Type<ChartDragEndEvent, ChartDragEndHandler> TYPE
      = new Type<ChartDragEndEvent, ChartDragEndHandler>() {
    @Override
    protected void fire(ChartDragEndHandler chartDragEndHandler,
        ChartDragEndEvent event) {
      chartDragEndHandler.onDragEnd(event);
    }
  };

  private int endX;

  public ChartDragEndEvent(XYPlot plot, int endX) {
    super(plot);
    this.endX = endX;
  }

  public int getEndX() {
    return endX;
  }

  protected Type getType() {
    return TYPE;
  }
}