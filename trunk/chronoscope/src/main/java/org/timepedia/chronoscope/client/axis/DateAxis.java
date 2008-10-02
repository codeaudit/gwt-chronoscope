package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.AxisPanel.Orientation;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.DomainAxisRenderer;

public class DateAxis extends ValueAxis {

  private int maxLabelWidth;

  private int maxLabelHeight;

  private final int tickHeight = 10;

  private DomainAxisRenderer renderer = null;

  private int axisLabelHeight;

  private int axisLabelWidth;

  private XYPlot plot;

  public DateAxis(XYPlot plot, AxisPanel domainPanel) {
    super(plot.getChart(), "Time", "s");
    this.plot = plot;
    setAxisPanel(domainPanel);

    renderer = new DomainAxisRenderer(this);
  }

  public double dataToUser(double dataValue) {
    return (dataValue - getRangeLow()) / getRange();
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer.drawAxis(plot, layer, axisBounds, gridOnly);
  }

  public int getAxisHeight() {
    return maxLabelHeight + tickHeight;
  }

  public double getAxisLabelWidth() {

    return axisLabelWidth;
  }

  public double getHeight() {
    if (getOrientation() == Orientation.HORIZONTAL) {
      return getMaxLabelHeight() + 5 + axisLabelHeight + 2;
    } else {
      return plot.getInnerBounds().height;
    }
  }

  public int getMaxLabelHeight() {
    return maxLabelHeight;
  }

  public int getMaxLabelWidth() {
    return maxLabelWidth;
  }

  public double getMinimumTickSize() {
    return renderer.getMinimumTickSize();
  }

  public double getRangeHigh() {
    return plot.getDomain().getEnd();
  }

  public double getRangeLow() {
    return plot.getDomain().getStart();
  }

  public int getTickInterval() {
    return maxLabelWidth + 10;
  }

  public double getWidth() {
    if (getOrientation() == Orientation.VERTICAL) {
      return getMaxLabelWidth() + 5 + axisLabelWidth + 10;
    } else {
      return plot.getInnerBounds().width;
    }
  }

  public void init() {
    boolean isHorizontal = getOrientation() == Orientation.HORIZONTAL;
    final String axisLabel = "(Time)";
    View view = getChart().getView();
    renderer.init(view);

    maxLabelWidth = renderer.getLabelWidth(view, "XXX'00");
    maxLabelHeight = renderer.getLabelHeight(view, "XXXX");
    axisLabelWidth = renderer
        .getLabelWidth(view, isHorizontal ? axisLabel : "X");

    axisLabelHeight = 0;
    if (renderer.isAxisLabelVisible()) {
      if (isHorizontal) {
        axisLabelHeight = renderer.getLabelHeight(view, axisLabel);
      } else {
        axisLabelHeight = renderer.getLabelHeight(view, "X") * axisLabel
            .length();
      }
    }
  }

  protected void layout() {
    renderer = new DomainAxisRenderer(this);
    renderer.init(getChart().getView());
  }

  public boolean isVisible(double tickPos) {
    return true;
  }
}
