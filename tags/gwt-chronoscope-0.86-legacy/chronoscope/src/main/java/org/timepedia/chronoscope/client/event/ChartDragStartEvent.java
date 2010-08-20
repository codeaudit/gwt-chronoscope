package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChartDragStartEvent extends PlotEvent implements Exportable {

  public static Type<ChartDragStartEvent, ChartDragStartHandler> TYPE
      = new Type<ChartDragStartEvent, ChartDragStartHandler>() {
    @Override
    protected void fire(ChartDragStartHandler chartDragStartHandler,
        ChartDragStartEvent event) {
      chartDragStartHandler.onDragStart(event);
    }
  };

  private int startX;

  public ChartDragStartEvent(XYPlot plot, int startX) {
    super(plot);
    this.startX = startX;
  }

  public int getStartX() {
    return startX;
  }

  protected Type getType() {
    return TYPE;
  }
}

