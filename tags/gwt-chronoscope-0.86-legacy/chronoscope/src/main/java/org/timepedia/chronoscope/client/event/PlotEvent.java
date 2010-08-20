package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
 *
 */
@ExportPackage("chronoscope")
public abstract class PlotEvent extends AbstractEvent implements Exportable {

  private XYPlot plot;

  protected PlotEvent(XYPlot plot) {
    this.plot = plot;
  }

  @Export
  public XYPlot getPlot() {
    return plot;
  }
}
