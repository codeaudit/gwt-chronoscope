package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.chronoscope.client.XYPlot;

/**
 * Fired by plot implementations when focus point changes.
 */
public class PlotFocusEvent extends PlotEvent {

  public static Type<PlotFocusEvent, PlotFocusHandler> TYPE
      = new Type<PlotFocusEvent, PlotFocusHandler>() {
    @Override
    protected void fire(PlotFocusHandler plotFocusHandler,
        PlotFocusEvent event) {
      plotFocusHandler.onFocus(event);
    }
  };

  private int focusPoint;

  private int focusDataset;

  public PlotFocusEvent(XYPlot plot, int focusPoint, int focusDataset) {
    super(plot);
    this.focusPoint = focusPoint;
    this.focusDataset = focusDataset;
  }

  public int getFocusDataset() {
    return focusDataset;
  }

  public int getFocusPoint() {
    return focusPoint;
  }

  protected Type getType() {
    return TYPE;
  }
}