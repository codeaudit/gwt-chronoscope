package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
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
    if (getOrientation() == AxisPanel.HORIZONTAL_AXIS) {
      return getMaxLabelHeight() + 5 + axisLabelHeight + 2;
    } else {
      return plot.getInnerPlotBounds().height;
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
    return plot.getDomainOrigin() + plot.getCurrentDomain();
  }

  public double getRangeLow() {
    return plot.getDomainOrigin();
  }

  public int getTickInterval() {
    return maxLabelWidth + 10;
  }

  public double getWidth() {
    if (getOrientation() == AxisPanel.VERTICAL_AXIS) {
      return getMaxLabelWidth() + 5 + axisLabelWidth + 10;
    } else {
      return plot.getInnerPlotBounds().width;
    }
  }

  public void init() {
    renderer.init(getChart().getView());

    maxLabelWidth = renderer.getLabelWidth(getChart().getView(), "XXX'00");
    maxLabelHeight = renderer.getLabelHeight(getChart().getView(), "XXXX");
    axisLabelHeight = renderer.isAxisLabelVisible() ? renderer
        .getLabelHeight(getChart().getView(), getOrientation() == AxisPanel
            .HORIZONTAL_AXIS ? "(Time)" : "X") * (
        getOrientation() == AxisPanel.HORIZONTAL_AXIS ? 1 : "(Time)".length())
        : 0;
    axisLabelWidth = renderer.getLabelWidth(getChart().getView(),
        getOrientation() == AxisPanel.HORIZONTAL_AXIS ? "(Time)" : "X");
  }

  public boolean isVisible(double tickPos) {
    return true;
  }

  public double userToData(double userValue) {
    return getRangeLow() + userValue * getRange();
  }
}
