package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.RangeAxisPanel;
import org.timepedia.chronoscope.client.util.Interval;
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

  public static final int MAX_DIGITS = 5;

  class DefaultTickLabelNumberFormatter implements TickLabelNumberFormatter {

    String labelFormat = null;

    public String format(double value) {
      computeLabelFormat(value);
      return view.numberFormat(labelFormat, value);
    }

    private void computeLabelFormat(double label) {
      double scale = getScale();
      scale = Double.isNaN(scale) ? 1 : scale;

      int intDigits = (int) Math.floor(Math.log10(getRangeHigh()));
      if (isAllowAutoScale() && Double.isNaN(getScale())
          && intDigits + 1 > MAX_DIGITS) {
        scale = Math.pow(1000, intDigits / 3);
      }
      if (isForceScientificNotation() || (isAllowScientificNotation() && (
          intDigits + 1 > MAX_DIGITS || Math.abs(intDigits) > MAX_DIGITS))) {
        labelFormat = "0." + "0#########".substring(MAX_DIGITS) + "E0";
        scientificNotationOn = true;
      } else if (intDigits > 0) {
        String digStr = "#########0";
        labelFormat = digStr
            .substring(Math.max(digStr.length() - intDigits, 0));
        int leftOver = Math.max(MAX_DIGITS - intDigits, 0);
        if (leftOver > 0) {
          labelFormat += "." + "0#########".substring(leftOver);
        }
        scientificNotationOn = false;
      } else {
        labelFormat = "0." + "0#########".substring(MAX_DIGITS);
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

  private static double[] computeLinearTickPositions(double lrangeLow,
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

    final double range = lrangeHigh - lrangeLow;

    final int maxNumLabels = (int) Math
        .floor(axisHeight / (2 * tickLabelHeight));

    final double roughInterval = range / maxNumLabels;

    final int logRange = ((int) Math.floor(Math.log10(roughInterval))) - 1;
    final double exponent = Math.pow(10, logRange);
    int smoothSigDigits = (int) (roughInterval / exponent);
    smoothSigDigits = smoothSigDigits + 5;
    smoothSigDigits = smoothSigDigits - (int) MathUtil
        .mod(smoothSigDigits, 5.0);

    final double smoothInterval = smoothSigDigits * exponent;

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

  private boolean allowAutoScale = true;

  private boolean allowScientificNotation = false;

  private boolean autoZoom = false;

  private double adjustedRangeLow, adjustedRangeHigh;

  private final int axisIndex;

  private TickLabelNumberFormatter DEFAULT_TICK_LABEL_Number_FORMATTER;

  private boolean forceScientificNotation = false;

  private XYPlot plot;

  private double rangeLow, rangeHigh;

  private RangeAxisPanel axisPanel;

  private boolean rangeOverriden;

  private double scale = Double.NaN;

  private boolean scientificNotationOn;

  private boolean showExponents = false;

  private double[] ticks;

  private TickLabelNumberFormatter tickLabelNumberFormatter;

  private View view;

  private double visRangeMin, visRangeMax;

  public RangeAxis(XYPlot plot, View view, String rangeLabel, String axisId,
      int axisIndex, Interval rangeInterval) {
    super(rangeLabel, axisId);
    this.axisIndex = axisIndex;
    tickLabelNumberFormatter = DEFAULT_TICK_LABEL_Number_FORMATTER
        = new DefaultTickLabelNumberFormatter();
    this.plot = plot;
    this.view = view;
    this.rangeLow = rangeInterval.getStart();
    this.rangeHigh = rangeInterval.getEnd();
    this.adjustedRangeLow = rangeLow;
    this.adjustedRangeHigh = rangeHigh;
  }

  public double[] computeTickPositions() {
    
    if (ticks != null) {
      return ticks;
    }

    ticks = computeLinearTickPositions(getUnadjustedRangeLow(),
        getUnadjustedRangeHigh(),
        axisPanel.getHeight(),
        axisPanel.getMaxLabelHeight(), 
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

  public int getAxisIndex() {
    return axisIndex;
  }

  public String getFormattedLabel(double label) {
    if (!Double.isNaN(getScale())) {
      label /= getScale();
    }

    return tickLabelNumberFormatter.format(label);
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

  protected double getRangeHigh() {
    return adjustedRangeHigh;
  }

  protected double getRangeLow() {
    return adjustedRangeLow;
  }

  public double getScale() {
    return scale;
  }

  public TickLabelNumberFormatter getTickLabelFormatter() {
    return tickLabelNumberFormatter;
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

  public void setAxisPanel(RangeAxisPanel r) {
    this.axisPanel = r;
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
    plot.damageAxes(this);
    axisPanel.computeLabelWidths(view);
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
          new UserTickLabelNumberFormatter(view, format));
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
