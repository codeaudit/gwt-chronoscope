package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.DomainAxisRenderer;

public class DateAxis extends ValueAxis {

  private int axisLabelWidth, axisLabelHeight;

  private int maxLabelWidth, maxLabelHeight;

  private XYPlot plot;
  
  private DomainAxisRenderer renderer = null;

  private View view;
  
  public DateAxis(XYPlot plot, View view, AxisPanel domainPanel) {
    super("Time", "s");
    this.plot = plot;
    this.view = view;
    this.axisPanel = domainPanel;
  }

  public double dataToUser(double dataValue) {
    return (dataValue - getRangeLow()) / getRange();
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer.drawAxis(plot, layer, axisBounds, gridOnly);
  }

  public double getAxisLabelWidth() {
    return axisLabelWidth;
  }

  public double getHeight() {
    if (axisPanel.getPosition().isHorizontal()) {
      return maxLabelHeight + 5 + axisLabelHeight + 2;
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
    if (axisPanel.getPosition().isHorizontal()) {
      return plot.getInnerBounds().width;
    } else {
      return maxLabelWidth + 5 + axisLabelWidth + 10;
    }
  }

  public void init() {
    boolean isHorizontal = axisPanel.getPosition().isHorizontal();
    final String axisLabel = "(Time)";

    renderer = new DomainAxisRenderer();
    renderer.setView(view);
    renderer.setValueAxis(this);
    renderer.init();

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
    renderer.init();
  }

  public boolean isVisible(double tickPos) {
    return true;
  }
}
