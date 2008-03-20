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

  private static String posExponentLabels[] = {"", "(Tens)", "(Hundreds)",
      "(Thousands)", "(Tens of Thousands)", "(Hundreds of Thousands)",
      "(Millions)", "(Tens of Millions)", "(Hundreds of Millions)",
      "(Billions)", "(Tens of Billions)", "(Hundreds of Billions)",
      "(Trillions)", "(Tens of Trillions)", "(Hundreds of Trillions)"};

  private static String negExponentLabels[] = {"", "(Tenths)", "(Hundredths)",
      "(Thousandths)", "(Ten Thousandths)", "(Hundred Thousandths)",
      "(Millionths)", "(Ten Millionths)", "(Hundred Millionths)",
      "(Billionths)", "(Ten Billionths)", "(Hundred Billionths)",
      "(Trillionths)", "(Ten Trillionths)", "(Hundred Trillionths)"};

  public static double[] computeLinearTickPositions(double rangeLow,
      double rangeHigh, double axisHeight, double tickLabelHeight) {
    double range = rangeHigh - rangeLow;
    int maxNumLabels = (int) Math
        .floor(axisHeight / (2 * tickLabelHeight));

    double roughInterval = range / maxNumLabels;

    int logRange = ((int) Math.floor(Math.log(roughInterval) / Math.log(10)))
        - 1;
    double exponent = Math.pow(10, logRange);
    int smoothSigDigits = (int) (roughInterval / exponent);
    smoothSigDigits = smoothSigDigits + 5;
    smoothSigDigits = smoothSigDigits - (smoothSigDigits % 5);

    double smoothInterval = smoothSigDigits * exponent;

    double axisStart = rangeLow - rangeLow % smoothInterval;
    int numTicks = (int) (Math.ceil((rangeHigh - axisStart) / smoothInterval));
    double tickPositions[] = new double[numTicks];
    for (int i = 0; i < tickPositions.length; i++) {
      tickPositions[i] = axisStart;
      axisStart += smoothInterval;
    }
    return tickPositions;
  }

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

  private boolean allowScientificNotation = true;

  private boolean forceScientificNotation = false;

  private boolean showExponents = false;

  private int maxDigits = 4;

  private double scale = Double.NaN;

  public RangeAxis(Chart chart, String label, String units, int axisNum,
      double rangeLow, double rangeHigh, AxisPanel panel) {
    super(chart, label, units);
    this.axisNum = axisNum;
    setAxisPanel(panel);
    renderer = new RangeAxisRenderer(this);
    this.rangeLow = rangeLow;
    this.rangeHigh = rangeHigh;
  }

  public double[] computeTickPositions(double rangeLow, double rangeHigh,
      double axisHeight, double tickLabelHeight) {
    return computeLinearTickPositions(rangeLow, rangeHigh, axisHeight,
        tickLabelHeight);
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

  public String getDummyLabel() {
    return "0" + (maxDigits == 1 ? ""
        : "." + "000000000".substring(0, maxDigits - 1));
  }

  public double getHeight() {
    if (axisPanel.getOrientation() == AxisPanel.HORIZONTAL_AXIS) {
      return getMaxLabelHeight() + 5 + axisLabelHeight + 2;
    } else {
      return getChart().getPlotForAxis(this).getPlotBounds().height;
    }
  }

  public String getLabel() {
    double s = Double.isNaN(getScale()) ? 1.0 : getScale();
    return super.getLabel() + getLabelSuffix(getRange());
  }

  public String getLabelSuffix(double range) {
    if (isForceScientificNotation() || (isAllowScientificNotation() && renderer
        .isScientificNotationOn())) {
      return "";
    }
    if (!Double.isNaN(getScale())) {
      int intDigits = (int) Math.floor(Math.log(getRange() + 1) / Math.log(10));
      if (intDigits > 0) {
        return " " + (intDigits < posExponentLabels.length
            ? posExponentLabels[intDigits] : "E" + intDigits);
      } else if (intDigits < 0) {
        return " " + (-intDigits < negExponentLabels.length
            ? negExponentLabels[-intDigits] : "E" + intDigits);
      }
    }

    return "";
  }

  public int getMaxDigits() {
    return maxDigits;
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

  public double getScale() {
    return scale;
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

  public boolean isAllowScientificNotation() {
    return allowScientificNotation;
  }

  public boolean isAutoZoomVisibleRange() {
    return autoZoom;
  }

  public boolean isForceScientificNotation() {
    return forceScientificNotation;
  }

  public boolean isShowScale() {
    return showExponents;
  }

  /**
   * If enabled (true by default), when maxTickLabelDigits is exceeded, labels
   * will be rendered in scientific notation.
   *
   * @gwt.export
   */
  public void setAllowScientificNotation(boolean enable) {
    allowScientificNotation = enable;
  }

  /**
   * @gwt.export
   */
  public void setAutoZoomVisibleRange(boolean autoZoom) {

    this.autoZoom = autoZoom;
  }

  /**
   * Force tick labels to always be rendered in scientific notation. (Default
   * false);
   *
   * @gwt.export
   */
  public void setForceScientificNotation(boolean force) {
    forceScientificNotation = force;
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
   * The maximum number of digits allowed in a tick label, if scientific
   * notation is enabled, it will automatically switch after this limit is
   * reached. Minimum is 1 digit.
   */
  public void setMaxTickLabelDigits(int digits) {
    maxDigits = Math.max(1, digits);
  }

  /**
   * Set a scale factor for displaying axis tick values
   *
   * @gwt.export
   */
  public void setScale(double scale) {
    this.scale = scale;
  }

  public void setShowExponents(boolean showExponents) {
    this.showExponents = showExponents;
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

    maxLabelWidth = renderer.getLabelWidth(view, getDummyLabel(), 0) + 10;
    maxLabelHeight = renderer.getLabelHeight(view, getDummyLabel(), 0) + 10;
    axisLabelHeight = renderer
        .getLabelHeight(view, getLabel(), getRotationAngle());
    axisLabelWidth = renderer
        .getLabelWidth(view, getLabel(), getRotationAngle());
  }
}
