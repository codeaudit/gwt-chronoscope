package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;

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

  public double getRangeHigh() {
    return plot.getDatasets().getMaxDomain();
  }

  public double getRangeLow() {
    return plot.getDatasets().getMinDomain();
  }

  public final double userToData(double userValue) {
    // Use the userToData() implementation on the domain axis so that the 
    // user-to-data mapping function is consistent with this axis... but need
    // to pass in the overview-specific domain interval.
    double myRangeLow = getRangeLow();
    double myRangeHigh = getRangeHigh();
    return plot.getDomainAxis().userToData(myRangeLow, myRangeHigh, userValue);
  }

}
