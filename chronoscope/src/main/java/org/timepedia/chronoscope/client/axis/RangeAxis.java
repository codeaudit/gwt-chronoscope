package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.RangeAxisRenderer;
import org.timepedia.exporter.client.Exportable;

/**
 * A RangeAxis is an ValueAxis that represents values, typically on the y-axis.
 *
 * @gwt.exportPackage chronoscope
 */
public class RangeAxis extends ValueAxis implements Exportable {

  private final int axisNum;

  private final double rangeLow;

  private final double rangeHigh;

  private RangeAxisRenderer renderer = null;

  private double maxLabelWidth;

  private double maxLabelHeight;

  private double axisLabelHeight;

  private double axisLabelWidth;

  private double visRangeMin;

  private double visRangeMax;

  private boolean autoZoom = false;

  public RangeAxis(Chart chart, String label, String units, int axisNum,
      double rangeLow, double rangeHigh, AxisPanel panel) {
    super(chart, label, units);
    this.axisNum = axisNum;
    setAxisPanel(panel);
    renderer = new RangeAxisRenderer(this);
    this.rangeLow = rangeLow;
    this.rangeHigh = rangeHigh;
  }

  public double dataToUser(double dataY) {
    return (dataY - getRangeLow()) / getRange();
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer.drawAxis(plot, layer, axisBounds, gridOnly);
  }

  public double getAxisLabelHeight() {
    return axisLabelHeight;
  }

  public double getAxisLabelWidth() {
    return axisLabelWidth;
  }

  public int getAxisNumber() {
    return axisNum;
  }

  public double getHeight() {
    if (axisPanel.getOrientation() == AxisPanel.HORIZONTAL_AXIS) {
      return getMaxLabelHeight() + 5 + axisLabelHeight + 2;
    } else {
      return getChart().getPlotForAxis(this).getPlotBounds().height;
    }
  }

  public double getMaxLabelHeight() {
    return maxLabelHeight;
  }

  public double getMaxLabelWidth() {
    return maxLabelWidth;
  }

  public double getRangeHigh() {
    return autoZoom ? visRangeMax : rangeHigh;
  }

  public double getRangeLow() {
    return autoZoom ? visRangeMin : rangeLow;
  }

  public double getRotationAngle() {
    return
        (getAxisPanel().getPosition() == AxisPanel.RIGHT ? 1.0 : -1.0) * Math.PI
            / 2;
  }

  public double getWidth() {
    if (axisPanel.getOrientation() == AxisPanel.VERTICAL_AXIS) {
      return maxLabelWidth + 10 + axisLabelWidth;
    } else {
      return getChart().getPlotForAxis(this).getPlotBounds().width;
    }
  }

  public void init() {
    computeLabelWidths(getChart().getView());
  }

  public void initVisibleRange() {
    visRangeMin = rangeLow;
    visRangeMax = rangeHigh;
  }

  public boolean isAutoZoomVisibleRange() {
    return autoZoom;
  }

  /**
   * @gwt.export
   */
  public void setAutoZoomVisibleRange(boolean autoZoom) {

    this.autoZoom = autoZoom;
  }

  /**
   * @gwt.export
   */
  public void setLabel(String label) {
    super.setLabel(label);
    getChart().damageAxes(this);
    computeLabelWidths(getChart().getView());
  }

  /**
   * @gwt.export
   */
  public void setVisibleRange(double visRangeMin, double visRangeMax) {

    this.visRangeMin = visRangeMin;
    this.visRangeMax = visRangeMax;
  }

  public double userToData(double userValue) {
    return getRangeLow() + userValue * getRange();
  }

  private void computeLabelWidths(View view) {
    renderer.init(view);

    maxLabelWidth = renderer.getLabelWidth(view, "0.12", 0) + 10;
    maxLabelHeight = renderer.getLabelHeight(view, "0.12", 0) + 10;
    axisLabelHeight = renderer
        .getLabelHeight(view, getLabel(), getRotationAngle());
    axisLabelWidth = renderer
        .getLabelWidth(view, getLabel(), getRotationAngle());
  }
}
