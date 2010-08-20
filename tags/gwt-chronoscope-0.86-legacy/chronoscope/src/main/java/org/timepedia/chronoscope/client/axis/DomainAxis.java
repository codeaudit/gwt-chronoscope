package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.util.Interval;

public class DomainAxis extends ValueAxis {

  private XYPlot plot;
  
  public DomainAxis(XYPlot plot) {
    super("Time", "s");
    this.plot = plot;
  }

  public double dataToUser(double dataValue) {
    return plot.getDomain().getRatioFromPoint(dataValue);
  }

  public Interval getExtrema() {
    return plot.getDomain();
  }

  public double userToData(double userValue) {
    return plot.getDomain().getPointFromRatio(userValue);
  }
  
}
