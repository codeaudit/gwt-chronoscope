package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.chronoscope.client.XYPlot;

/**
 *
 */
public abstract class PlotEvent extends AbstractEvent {

  private XYPlot plot;

  protected PlotEvent(XYPlot plot) {
    this.plot = plot;
  }

  public XYPlot getPlot() {
    return plot;
  }
}
