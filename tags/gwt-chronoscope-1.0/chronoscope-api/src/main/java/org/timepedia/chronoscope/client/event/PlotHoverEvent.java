package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;

/**
 * Fired by plot implementations when the set of hovered points changes.
 */
@ExportPackage("chronoscope")
public class PlotHoverEvent extends PlotEvent<PlotHoverHandler> implements Exportable {

  public static Type<PlotHoverHandler> TYPE
      = new Type<PlotHoverHandler>();

  private int[] hoverPoints;

  public PlotHoverEvent(XYPlot plot, int[] hoverPoints) {
    super(plot);
    this.hoverPoints = hoverPoints;
  }

  public int[] getHoverPoints() {
    return hoverPoints;
  }

  @Export
  public double[] getDomainPoints() {
    double[] d = new double[hoverPoints.length];
    for (int i = 0; i < d.length; i++) {
      d[i] = hoverPoints[i] != DefaultXYPlot.NO_SELECTION ? getPlot()
          .getDataX(i, hoverPoints[i]) : Double.NaN;
    }
    return d;
  }

  @Export
  public double[] getRangePoints() {
    double[] d = new double[hoverPoints.length];
    for (int i = 0; i < d.length; i++) {
      d[i] = hoverPoints[i] != DefaultXYPlot.NO_SELECTION ? getPlot()
          .getDataY(i, hoverPoints[i]) : Double.NaN;
    }
    return d;
  }
  
  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(PlotHoverHandler plotHoverHandler) {
    plotHoverHandler.onHover(this);
  }
}
