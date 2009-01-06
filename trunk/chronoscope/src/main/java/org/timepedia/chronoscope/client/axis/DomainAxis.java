package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;

public class DomainAxis extends ValueAxis {

  private XYPlot plot;
  
  public DomainAxis(XYPlot plot) {
    super("Time", "s");
    this.plot = plot;
  }

  public double dataToUser(double dataValue) {
    return (dataValue - getRangeLow()) / getRange();
  }

  public double getRangeHigh() {
    return plot.getDomain().getEnd();
  }

  public double getRangeLow() {
    return plot.getDomain().getStart();
  }
}
