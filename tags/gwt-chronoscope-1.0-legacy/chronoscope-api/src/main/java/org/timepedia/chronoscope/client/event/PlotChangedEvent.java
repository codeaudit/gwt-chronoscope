package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Fired by plot implementations when the chart's state is modified and 
 * stabilizes.
 */
@ExportPackage("chronoscope")
public class PlotChangedEvent extends PlotEvent<PlotChangedHandler> implements Exportable {

  public static Type<PlotChangedHandler> TYPE
      = new Type<PlotChangedHandler>();

  private Interval domain;

  public PlotChangedEvent(XYPlot plot, Interval domain) {
    super(plot);

    this.domain = domain;
  }

  @Export
  public Interval getDomain() {
    return domain;
  }

  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(PlotChangedHandler plotChangedHandler) {
    plotChangedHandler.onChanged(this);
  }
}