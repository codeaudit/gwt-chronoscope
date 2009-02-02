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

  private class DefaultTickLabelNumberFormatter implements TickLabelNumberFormatter {

    String labelFormat = null;

    public String format(double value) {
      computeLabelFormat();
      return view.numberFormat(labelFormat, value);
    }

    private void computeLabelFormat() {
      double scale = getScale();
      scale = Double.isNaN(scale) ? 1 : scale;

      int intDigits = (int) Math.floor(Math.log10(adjustedRangeMax));
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

  private static final String posExponentLabels[] = {"", "(Tens)", "(Hundreds)",
      "(Thousands)", "(Tens of Thousands)", "(Hundreds of Thousands)",
      "(Millions)", "(Tens of Millions)", "(Hundreds of Millions)",
      "(Billions)", "(Tens of Billions)", "(Hundreds of Billions)",
      "(Trillions)", "(Tens of Trillions)", "(Hundreds of Trillions)"};

  private static final String negExponentLabels[] = {"", "(Tenths)", "(Hundredths)",
      "(Thousandths)", "(Ten Thousandths)", "(Hundred Thousandths)",
      "(Millionths)", "(Ten Millionths)", "(Hundred Millionths)",
      "(Billionths)", "(Ten Billionths)", "(Hundred Billionths)",
      "(Trillionths)", "(Ten Trillionths)", "(Hundred Trillionths)"};

  private static double[] computeLinearTickPositions(double lrangeLow,
      double lrangeHigh, double axisHeight, double tickLabelHeight,
      boolean forceLastTick) {

    if (lrangeHigh == lrangeLow) {
      if (lrangeHigh != 0.0) {
        int logRange = ((int) Math.floor(Math.log10(lrangeHigh)));
        if (logRange < 0) {
          logRange += 1;
        }
        double exponent = Math.pow(10, logRange);
        double rounded = Math.floor(lrangeHigh / exponent);
        lrangeHigh = (rounded + 1) * exponent;
        lrangeLow = (rounded - 1) * exponent;
      }
      else {
        lrangeHigh = 1.0;
        lrangeLow = -1.0;
      }
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
      if ((tickPositions.length == (i + 1)) && forceLastTick) {
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

  private final int axisIndex;

  private TickLabelNumberFormatter DEFAULT_TICK_LABEL_Number_FORMATTER;

  private boolean forceScientificNotation = false;

  private XYPlot plot;

  private double absRangeMin, absRangeMax, visRangeMin, visRangeMax,
      adjustedRangeMin, adjustedRangeMax;

  private RangeAxisPanel axisPanel;

  private boolean rangeOverridden;

  private double scale = Double.NaN;

  private boolean scientificNotationOn;

  private boolean showExponents = false;

  private double[] ticks;

  private TickLabelNumberFormatter tickLabelNumberFormatter;

  private View view;

  public RangeAxis(XYPlot plot, View view, String rangeLabel, String axisId,
      int axisIndex, Interval rangeInterval) {
    super(rangeLabel, axisId);
    this.axisIndex = axisIndex;
    tickLabelNumberFormatter = DEFAULT_TICK_LABEL_Number_FORMATTER
        = new DefaultTickLabelNumberFormatter();
    this.plot = plot;
    this.view = view;
    this.absRangeMin = rangeInterval.getStart();
    this.absRangeMax = rangeInterval.getEnd();
    this.adjustedRangeMin = absRangeMin;
    this.adjustedRangeMax = absRangeMax;
  }

  /**
   * Increases the interval of this object's current range extrema 
   * so that it's at least as wide as the specified <tt>rangeExtrema</tt>.
   */
  public void adjustRangeExtrema(Interval rangeExtrema) {
    if (!rangeOverridden) {
      setRangeInternal(
          Math.min(absRangeMin, rangeExtrema.getStart()),
          Math.max(absRangeMax, rangeExtrema.getEnd()));
    }
  }
  
  public double[] computeTickPositions() {

    if (ticks != null) {
      return ticks;
    }
    
    final double rangeMin = autoZoom ? this.visRangeMin : this.absRangeMin;
    final double rangeMax = autoZoom ? this.visRangeMax : this.absRangeMax;
    
    ticks = computeLinearTickPositions(rangeMin, rangeMax, 
        axisPanel.getBounds().height, axisPanel.getMaxLabelHeight(), 
        rangeOverridden);

    if (rangeOverridden) {
      adjustedRangeMin = rangeMin;
      adjustedRangeMax = rangeMax;
    }
    else {
      adjustedRangeMin = ticks[0];
      final double largestComputedTick = ticks[ticks.length - 1];
      adjustedRangeMax = Math.max(rangeMax, largestComputedTick);
    }
    
    return ticks;
  }

  public double dataToUser(double dataValue) {
    return (dataValue - adjustedRangeMin) / (adjustedRangeMax
        - adjustedRangeMin);
  }

  public int getAxisIndex() {
    return axisIndex;
  }

  public RangeAxisPanel getAxisPanel() {
    return this.axisPanel;
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
      int intDigits = (int) Math.floor(Math.log10(getExtrema().length() + 1));
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

  public Interval getExtrema() {
    return new Interval(adjustedRangeMin, adjustedRangeMax);
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
    visRangeMin = absRangeMin;
    visRangeMax = absRangeMax;
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
    rangeOverridden = true;
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

  public double userToData(double userValue) {
    return adjustedRangeMin + 
        ((adjustedRangeMax - adjustedRangeMin) * userValue);
  }

  private void setRangeInternal(double rangeMin, double rangeMax) {
    ticks = null;
    this.absRangeMin = rangeMin;
    this.absRangeMax = rangeMax;
  }
}
