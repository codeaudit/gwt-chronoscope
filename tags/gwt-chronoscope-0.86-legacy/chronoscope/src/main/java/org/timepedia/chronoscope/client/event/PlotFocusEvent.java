package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
 * Fired by plot implementations when focus point changes.
 */
@ExportPackage("chronoscope")
public class PlotFocusEvent extends PlotEvent implements Exportable {

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
  
  @Export
  public double getDomain() {
    return getPlot().getDataX(focusDataset, focusPoint);
  }
      
  @Export
  public double getRange() {
    return getPlot().getDataY(focusDataset, focusPoint);
  }
  
  protected Type getType() {
    return TYPE;
  }
}