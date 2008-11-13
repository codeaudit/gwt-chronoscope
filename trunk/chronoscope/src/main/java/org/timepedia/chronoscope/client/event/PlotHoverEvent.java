package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.XYPlot;

/**
 * Fired by plot implementations when the set of hovered points changes.
 */
public class PlotHoverEvent extends PlotEvent {

  public static Type<PlotHoverEvent, PlotHoverHandler> TYPE
      = new Type<PlotHoverEvent, PlotHoverHandler>() {
    @Override
    protected void fire(PlotHoverHandler plotHoverHandler,
        PlotHoverEvent event) {
      plotHoverHandler.onHover(event);
    }
  };

  private int[] hoverPoints;
 
  public PlotHoverEvent(XYPlot plot, int[] hoverPoints) {
    super(plot);
    this.hoverPoints = hoverPoints;
  }

  public int[] getHoverPoints() {
    return hoverPoints;
  }

  protected Type getType() {
    return TYPE;
  }
}
