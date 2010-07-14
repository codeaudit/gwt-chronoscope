package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
 * Fired by plot implementations when focus point changes.
 */
@ExportPackage("chronoscope")
public class PlotFocusEvent extends PlotEvent<PlotFocusHandler> implements Exportable {

  public static Type<PlotFocusHandler> TYPE
      = new Type<PlotFocusHandler>();

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
    if (focusDataset < 0 || focusPoint < 0) {
        return Double.NaN;
    }
    return getPlot().getDataX(focusDataset, focusPoint);
  }
      
  @Export
  public double getRange() {
    if (focusDataset < 0 || focusPoint < 0) {
        return Double.NaN;
    }      
    return getPlot().getDataY(focusDataset, focusPoint);
  }
  
  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(PlotFocusHandler plotFocusHandler) {
    plotFocusHandler.onFocus(this);
  }
}