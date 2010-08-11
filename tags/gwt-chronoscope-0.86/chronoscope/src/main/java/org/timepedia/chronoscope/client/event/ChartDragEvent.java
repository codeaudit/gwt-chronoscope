package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChartDragEvent extends PlotEvent implements Exportable {

  public static Type<ChartDragEvent, ChartDragHandler> TYPE
      = new Type<ChartDragEvent, ChartDragHandler>() {
    @Override
    protected void fire(ChartDragHandler chartDragHandler,
        ChartDragEvent event) {
      chartDragHandler.onDrag(event);
    }
  };

  private int currentX;

  public ChartDragEvent(XYPlot plot, int currentX) {
    super(plot);
    this.currentX = currentX;
  }

  public int getCurrentX() {
    return currentX;
  }

  protected Type getType() {
    return TYPE;
  }
}