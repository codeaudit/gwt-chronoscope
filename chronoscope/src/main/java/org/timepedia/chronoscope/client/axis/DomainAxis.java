package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.View;

public class DomainAxis extends RangeAxis {

  private XYPlot plot;

  public DomainAxis(XYPlot plot, View view) {
    super(plot, view, "Time", "s", 0, plot.getDomain().getStart(),
        plot.getDomain().getEnd());
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

  public double[] computeTickPositions() {
   
    boolean horizontal = renderer.getParentPanel().getPosition().isHorizontal();

    ticks = computeLinearTickPositions(getRangeLow(),
        getRangeHigh(),
        horizontal ? renderer.getWidth() : renderer.getHeight(),
        horizontal ? renderer
            .getMaxLabelWidth() : renderer.getMaxLabelHeight(), false);

    return ticks;
  }
}
