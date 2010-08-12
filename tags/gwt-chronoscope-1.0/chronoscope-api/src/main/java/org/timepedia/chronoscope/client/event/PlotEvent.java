package org.timepedia.chronoscope.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public abstract class PlotEvent<H extends EventHandler> extends GwtEvent<H>
    implements Exportable {

  private XYPlot plot;

  protected PlotEvent(XYPlot plot) {
    this.plot = plot;
  }

  @Export
  public XYPlot getPlot() {
    return plot;
  }
}
