package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.AxisPanel.Position;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.RangeAxisRenderer;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * A RangeAxis is an ValueAxis that represents values, typically on the y-axis.
 *
 * @gwt.exportPackage chronoscope
 */
@ExportPackage("chronoscope")
public class RangeAxis extends ValueAxis implements Exportable {

  class DefaultTickLabelNumberFormatter implements TickLabelNumberFormatter {

    String labelFormat = null;

    public String format(double value) {
      computeLabelFormat(value);
      return chart.getView().numberFormat(labelFormat, value);
    }

    private void computeLabelFormat(double label) {
      double scale = getScale();
      scale = Double.isNaN(scale) ? 1 : scale;

      int intDigits = (int) Math.floor(Math.log10(getRangeHigh()));
      if (isAllowAutoScale() && Double.isNaN(getScale())
          && intDigits + 1 > maxDigits) {
        scale = Math.pow(1000, intDigits / 3);
      }
      if (isForceScientificNotation() || (isAllowScientificNotation() && (
          intDigits + 1 > getMaxDigits()
              || Math.abs(intDigits) > getMaxDigits()))) {
        labelFormat = "0." + "0#########".substring(getMaxDigits()) + "E0";
        scientificNotationOn = true;
      } else if (intDigits > 0) {
        String digStr = "#########0";
        labelFormat = digStr
            .substring(Math.max(digStr.length() - intDigits, 0));
        int leftOver = Math.max(getMaxDigits() - intDigits, 0);
        if (leftOver > 0) {
          labelFormat += "." + "0#########".substring(leftOver);
        }
        scientificNotationOn = false;
      } else {
        labelFormat = "0." + "0#########".substring(getMaxDigits());
        scientificNotationOn = false;
      }
    }
  }

  private class UserTickLabelNumberFormatter
      implements TickLabelNumberFormatter {

    private View view;

    private String format;

    public UserTickLabelNumberFormatter(View view, String format) {
      this.view = view;
      this.format = format;
    }

    public String format(double value) {
      return view.numberFormat(format, value);
    }
  }

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

  public static double[] computeLinearTickPositions(double lrangeLow,
      double lrangeHigh, double axisHeight, double tickLabelHeight,
      boolean forceLastTick) {
    if (lrangeHigh == lrangeLow) {
      int logRange = ((int) Math.floor(Math.log10(lrangeHigh)));
      if (logRange < 0) {
        logRange += 1;
      }
      double exponent = Math.pow(10, logRange);
      double rounded = Math.floor(lrangeHigh / exponent);
      lrangeHigh = (rounded + 1) * exponent;
      lrangeLow = (rounded - 1) * exponent;
    }

    double range = lrangeHigh - lrangeLow;

    int maxNumLabels = (int) Math
        .floor(axisHeight / (2 * tickLabelHeight));

    double roughInterval = range / maxNumLabels;

    int logRange = ((int) Math.floor(Math.log10(roughInterval))) - 1;
    double exponent = Math.pow(10, logRange);
    int smoothSigDigits = (int) (roughInterval / exponent);
    smoothSigDigits = smoothSigDigits + 5;
    smoothSigDigits = smoothSigDigits - (int) MathUtil
        .mod(smoothSigDigits, 5.0);

    double smoothInterval = smoothSigDigits * exponent;

    double axisStart = lrangeLow - MathUtil.mod(lrangeLow, smoothInterval);
    int numTicks = (int) (Math.ceil((lrangeHigh - axisStart) / smoothInterval));

    if (axisStart + smoothInterval * (numTicks - 1) < lrangeHigh) {
      numTicks++;
    }

    double tickPositions[] = new double[numTicks];
    for (int i = 0; i < tickPositions.length; i++) {
      if (tickPositions.length == i + 1 && forceLastTick) {
        axisStart = lrangeHigh;
      }
      tickPositions[i] = axisStart;
      axisStart += smoothInterval;
    }
    return tickPositions;
  }

  private Chart chart;
  private double[] ticks;

  private boolean rangeOverriden;

  private TickLabelNumberFormatter DEFAULT_TICK_LABEL_Number_FORMATTER;

  private boolean scientificNotationOn;

  private TickLabelNumberFormatter tickLabelNumberFormatter;

  private final int axisNum;

  private double rangeLow;

  private double rangeHigh;

  private RangeAxisRenderer renderer = null;

  private double maxLabelWidth;

  private double maxLabelHeight;

  private double axisLabelHeight;

  private double axisLabelWidth;

  private double visRangeMin;

  private double visRangeMax;

  private boolean autoZoom = false;

  private boolean allowScientificNotation = false;

  private boolean forceScientificNotation = false;

  private boolean allowAutoScale = true;

  private boolean showExponents = false;

  private int maxDigits = 5;

  private double scale = Double.NaN;

  private double adjustedRangeLow, adjustedRangeHigh;

  public RangeAxis(Chart chart, String label, String units, int axisNum,
      double rangeLow, double rangeHigh, AxisPanel panel) {
    super(label, units);
    this.axisNum = axisNum;
    this.axisPanel = panel;
    this.chart = chart;
    tickLabelNumberFormatter = DEFAULT_TICK_LABEL_Number_FORMATTER
        = new DefaultTickLabelNumberFormatter();
    this.renderer = new RangeAxisRenderer(this);
    this.rangeLow = rangeLow;
    this.rangeHigh = rangeHigh;
    this.adjustedRangeLow = rangeLow;
    this.adjustedRangeHigh = rangeHigh;
  }

  public double[] computeTickPositions() {
    if (ticks != null) {
      return ticks;
    }
    ticks = computeLinearTickPositions(getUnadjustedRangeLow(),
        getUnadjustedRangeHigh(), getHeight(), getMaxLabelHeight(),
        rangeOverriden);
    adjustedRangeLow = rangeOverriden ? getUnadjustedRangeLow() : ticks[0];
    adjustedRangeHigh = getUnadjustedRangeHigh();
    for (int i = 0; i < ticks.length; i++) {
      if (ticks[i] >= getUnadjustedRangeHigh() && !rangeOverriden) {
        adjustedRangeHigh = ticks[i];
        break;
      }
    }

    return ticks;
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

  public String getFormattedLabel(double label) {
    if (!Double.isNaN(getScale())) {
      label /= getScale();
    }

    return tickLabelNumberFormatter.format(label);
  }

  public double getHeight() {
    if (axisPanel.getPosition().isHorizontal()) {
      return getMaxLabelHeight() + 5 + axisLabelHeight + 2;
    } else {
      return chart.getPlot().getInnerBounds().height;
    }
  }

  public String getLabel() {
//    double s = Double.isNaN(getScale()) ? 1.0 : getScale();
//    return super.getLabel() + getLabelSuffix(getRange());
    String axisId = getAxisId();
    if (axisId == null || "".equals(axisId)) {
      axisId = super.getLabel() + getLabelSuffix();
    }
    return axisId;
  }

  public String getLabelSuffix() {
    if (isForceScientificNotation() || (isAllowScientificNotation()
        && isScientificNotationOn())) {
      return "";
    }
    if (!Double.isNaN(getScale())) {
      int intDigits = (int) Math.floor(Math.log10(getRange() + 1));
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
    return adjustedRangeHigh;
  }

  public double getRangeLow() {
    return adjustedRangeLow;
  }

  public double getRotationAngle() {
    return
        (getAxisPanel().getPosition() == Position.RIGHT ? 1.0 : -1.0) * Math.PI
            / 2;
  }

  public double getScale() {
    return scale;
  }

  public TickLabelNumberFormatter getTickLabelFormatter() {
    return tickLabelNumberFormatter;
  }

  public double getWidth() {
    double computedAxisLabelWidth = renderer.isAxisLabelVisible() ?
        axisLabelWidth + 5 : 0;

    if (!axisPanel.getPosition().isHorizontal()) {
      boolean isLeft = axisPanel.getPosition() == Position.LEFT;
      boolean isInner = axisPanel.getAxisNumber(this) == (isLeft ?
          axisPanel.getAxisCount() - 1 : 0);
      if (isInner) {
        if (renderer.getTickPosition() == RangeAxisRenderer.TickPosition
            .INSIDE) {
          return computedAxisLabelWidth;
        } else {
          return maxLabelWidth + 5 + computedAxisLabelWidth;
        }
      } else {
        return maxLabelWidth + 5 + computedAxisLabelWidth;
      }
    } else {
      return chart.getPlot().getInnerBounds().width;
    }
  }

  public void init() {
    computeLabelWidths(chart.getView());
  }

  public void initVisibleRange() {
    ticks = null;
    computeTickPositions();
    visRangeMin = rangeLow;
    visRangeMax = rangeHigh;
  }

  public boolean isAllowAutoScale() {
    return allowAutoScale;
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

  public boolean isScientificNotationOn() {
    return scientificNotationOn;
  }

  public boolean isShowScale() {
    return showExponents;
  }

  /**
   * If set to true, allow axis ticks to be scaled automatically by powers of
   * thousand if they exceeed maxDigits settings. For example, if max digits is
   * 4, then the number 25000 will be rendered as 25, and the axis label will be
   * modified to include the word "Thousands". setAllowScientificNotation() will
   * override this and take priority, as well as setScale().
   *
   * @gwt.export
   */
  @Export
  public void setAllowAutoScale(boolean allowAutoScale) {
    this.allowAutoScale = allowAutoScale;
  }

  /**
   * If enabled (true by default), when maxTickLabelDigits is exceeded, labels
   * will be rendered in scientific notation.
   *
   * @gwt.export
   */
  @Export
  public void setAllowScientificNotation(boolean enable) {
    allowScientificNotation = enable;
  }

  /**
   * @gwt.export
   */
  @Export
  public void setAutoZoomVisibleRange(boolean autoZoom) {

    this.autoZoom = autoZoom;
  }

  /**
   * Force tick labels to always be rendered in scientific notation. (Default
   * false);
   *
   * @gwt.export
   */
  @Export
  public void setForceScientificNotation(boolean force) {
    forceScientificNotation = force;
  }

  public void setInitialRange(double initLow, double initHigh) {
    if (!rangeOverriden) {
      setRangeInternal(initLow, initHigh);
    }
  }

  /**
   * @gwt.export
   */
  @Export
  public void setLabel(String label) {
    super.setLabel(label);
    chart.getPlot().damageAxes(this);
    computeLabelWidths(chart.getView());
  }

  /**
   * The maximum number of digits allowed in a tick label, if scientific
   * notation is enabled, it will automatically switch after this limit is
   * reached. Minimum is 1 digit.
   */
  public void setMaxTickLabelDigits(int digits) {
    maxDigits = Math.max(1, digits);
  }

  @Export
  public void setRange(double rangeLow, double rangeHigh) {
    rangeOverriden = true;
    setRangeInternal(rangeLow, rangeHigh);
  }

  /**
   * Set a scale factor for displaying axis tick values
   *
   * @gwt.export
   */
  @Export
  public void setScale(double scale) {
    this.scale = scale;
  }

  public void setShowExponents(boolean showExponents) {
    this.showExponents = showExponents;
  }

  /**
   * Set custom TickLabelNumberFormatter callbacks.
   *
   * @gwt.export
   */
  @Export
  public void setTickLabelNumberFormatter(
      TickLabelNumberFormatter tickLabelNumberFormatter) {
    this.tickLabelNumberFormatter = tickLabelNumberFormatter;
  }

  /**
   * Set the number format used to render ticks
   *
   * @gwt.export
   */
  @Export
  public void setTickNumberFormat(String format) {
    if (format == null) {
      tickLabelNumberFormatter = DEFAULT_TICK_LABEL_Number_FORMATTER;
    } else {
      setTickLabelNumberFormatter(
          new UserTickLabelNumberFormatter(chart.getView(), format));
    }
  }

  /**
   * @gwt.export
   */
  @Export
  public void setVisibleRange(double visRangeMin, double visRangeMax) {

    this.visRangeMin = visRangeMin;
    this.visRangeMax = visRangeMax;
    ticks = null;
    computeTickPositions();
  }

  protected void layout() {
    renderer = new RangeAxisRenderer(this);
    init();
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

  private String getDummyLabel() {
    return "0" + (maxDigits == 1 ? ""
        : "." + "000000000".substring(0, maxDigits - 1));
  }

  public double getUnadjustedRangeHigh() {
    return autoZoom ? visRangeMax : rangeHigh;
  }

  public double getUnadjustedRangeLow() {
    return autoZoom ? visRangeMin : rangeLow;
  }

  private void setRangeInternal(double rangeLow, double rangeHigh) {
    ticks = null;
    this.rangeLow = rangeLow;
    this.rangeHigh = rangeHigh;
  }
}
