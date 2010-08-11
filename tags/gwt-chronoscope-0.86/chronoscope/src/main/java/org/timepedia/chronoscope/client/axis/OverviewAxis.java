package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.util.Interval;

/**
 * An implementation of ValueAxis which renders a miniature zoomed-out overview
 * of the whole chart.
 */
public class OverviewAxis extends ValueAxis {

  private XYPlot plot;

  public OverviewAxis(XYPlot plot, String title) {
    super(title, "");    
    this.plot = plot;
  }

  // N/A
  public double dataToUser(double dataValue) {
    throw new UnsupportedOperationException();
  }

  public Interval getExtrema() {
    return plot.getWidestDomain();
  }

  public double userToData(double userValue) {
    return plot.getWidestDomain().getPointFromRatio(userValue);
  }
}
