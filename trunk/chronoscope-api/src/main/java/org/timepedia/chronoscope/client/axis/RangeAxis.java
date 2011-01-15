package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.MipMap;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.DatasetRenderer;
import org.timepedia.chronoscope.client.render.RangeAxisPanel;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * A RangeAxis is an ValueAxis that represents values, typically on the y-axis.
 */
@ExportPackage("chronoscope")
public class RangeAxis extends ValueAxis implements Exportable {

  private class DefaultTickLabelNumberFormatter
      implements TickLabelNumberFormatter {

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
          labelFormat += "." + "0#########".substring(10-leftOver);
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

  public static final int MAX_DIGITS = 5;

  private static final String posExponentLabels[] = {"", "(Tens)", "(Hundreds)",
      "(Thousands)", "(Tens of Thousands)", "(Hundreds of Thousands)",
      "(Millions)", "(Tens of Millions)", "(Hundreds of Millions)",
      "(Billions)", "(Tens of Billions)", "(Hundreds of Billions)",
      "(Trillions)", "(Tens of Trillions)", "(Hundreds of Trillions)"};

  private static final String negExponentLabels[] = {"", "(Tenths)",
      "(Hundredths)", "(Thousandths)", "(Ten Thousandths)",
      "(Hundred Thousandths)", "(Millionths)", "(Ten Millionths)",
      "(Hundred Millionths)", "(Billionths)", "(Ten Billionths)",
      "(Hundred Billionths)", "(Trillionths)", "(Ten Trillionths)",
      "(Hundred Trillionths)"};

  /**
   * Calculates the percentage difference from value v1 to value v2.  For
   * example, if v1 = 10 and v2 = 15, then the % difference is 50% ( 15.0/10.0 -
   * 1.0 )
   */
  public static double calcPrctDiff(double v1, double v2) {
    ArgChecker.isNonNegative(v1, "v1");
    ArgChecker.isNonNegative(v2, "v2");
    return (v2 / v1) - 1.0;
  }

  private static double[] computeLinearTickPositions(double lrangeLow,
      double lrangeHigh, double axisHeight, double tickLabelHeight,
      boolean forceFirstTick, boolean forceLastTick) {

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
      } else {
        lrangeHigh = 1.0;
        lrangeLow = -1.0;
      }
    }

    final double range = lrangeHigh - lrangeLow;

    final int maxNumLabels = (int) Math.floor((axisHeight - tickLabelHeight) / (2.5 * tickLabelHeight));

    final double roughInterval = range / maxNumLabels;

    final int logRange = ((int) Math.floor(Math.log10(roughInterval))) - 1;
    final double exponent = Math.pow(10, logRange);
    int smoothSigDigits = (int) (roughInterval / exponent);
    smoothSigDigits = smoothSigDigits + 5;
    smoothSigDigits = smoothSigDigits - (int) MathUtil.mod(smoothSigDigits, 5.0);

    final double smoothInterval = smoothSigDigits * exponent;

    double axisStart = lrangeLow - MathUtil.mod(lrangeLow, smoothInterval);
    int numTicks = (int) (Math.ceil((lrangeHigh - axisStart) / smoothInterval));

    if (axisStart + (smoothInterval * (numTicks - 1)) < lrangeHigh) {
      numTicks++;
    }
    if (forceFirstTick && axisStart < lrangeLow) {
      numTicks--;
      axisStart += smoothInterval;
    }
    if(numTicks<1){
        numTicks=1;
    }
    double tickPositions[] = new double[numTicks];
    double tickValue = axisStart;
    for (int i = 0; i < tickPositions.length; i++) {
      tickPositions[i] = tickValue;
      tickValue += smoothInterval;
    }
    // last tick requires special handling when user manually sets
    // the range interval
    if (forceLastTick) {
      tickPositions[numTicks - 1] = lrangeHigh;
    }

    return tickPositions;
  }

  private boolean allowAutoScale = true;

  private boolean allowScientificNotation = false;

  private boolean autoZoom = false;

  private int axisIndex;

  private boolean calcRangeAsPercent = false;

  private TickLabelNumberFormatter defaultTickLabelNumberFormatter;

  private boolean forceScientificNotation = false;

  private XYPlot plot;

  private double absRangeMin = Double.POSITIVE_INFINITY, absRangeMax
      = Double.NEGATIVE_INFINITY;

  private double visRangeMin = Double.POSITIVE_INFINITY, visRangeMax
      = Double.NEGATIVE_INFINITY;

  private double adjustedRangeMin = Double.POSITIVE_INFINITY, adjustedRangeMax
      = Double.NEGATIVE_INFINITY;

  private RangeAxisPanel axisPanel;

  private boolean rangeOveriddenLow, rangeOveriddenHigh;

  private double scale = Double.NaN;

  private boolean scientificNotationOn;

  private boolean showExponents = false;

  private double[] ticks;

  private TickLabelNumberFormatter tickLabelNumberFormatter;

  private View view;

  public RangeAxis(String rangeLabel, String axisId) {
    super(rangeLabel, axisId);
    tickLabelNumberFormatter = defaultTickLabelNumberFormatter
        = new DefaultTickLabelNumberFormatter();
  }

  /**
   * Increases the interval of this object's current range extrema so that it's
   * at least as wide as <tt>rangeExtrema(0)</tt> of the specified dataset
   * object.
   */
  public void adjustAbsRange(Dataset ds) {
    if (!rangeOveriddenLow || !rangeOveriddenHigh) {
      DatasetRenderer dr = plot.getDatasetRenderer(plot.getDatasets().indexOf(ds));
      MipMap m = ds.getMipMapChain().getMipMap(0);

      while (m != null) {
        if (m.getLevel() > 1 && m.size() < plot.getMaxDrawableDataPoints()) {
          break;
        }
        Interval rangeExtrema = dr.getRangeExtrema(m);
        double rangeMin = rangeExtrema.getStart();
        double rangeMax = rangeExtrema.getEnd();
        if (calcRangeAsPercent) {
          final double refY = dr.getRange(ds.getFlyweightTuple(0));
          rangeMin = calcPrctDiff(refY, rangeMin);
          rangeMax = calcPrctDiff(refY, rangeMax);
        }
        setAbsRange(
            Math.min(absRangeMin, rangeOveriddenLow ? absRangeMin : rangeMin),
            Math.max(absRangeMax, rangeOveriddenHigh ? absRangeMax : rangeMax));
        m = m.next();
      }
    }
  }

  /**
   * Increases the interval of this object's current visible range extrema so
   * that it's at least as wide as the specified <tt>visRange</tt> object.
   */
  public void adjustVisibleRange(Interval visRange) {
    setVisibleRange(Math.min(visRangeMin, visRange.getStart()),
        Math.max(visRangeMax, visRange.getEnd()));
  }

  public double[] calcTickPositions() {

    if (ticks != null) {
      return ticks;
    }

    final double rangeMin = autoZoom ? this.visRangeMin : this.absRangeMin;
    final double rangeMax = autoZoom ? this.visRangeMax : this.absRangeMax;

    double labelLineHeight = Math.min(axisPanel.getMaxLabelHeight(), 14); // TODO - use axisLabelHeight
    double rangeAxisHeight = axisPanel.getBounds().height - labelLineHeight;

    ticks = computeLinearTickPositions(rangeMin, rangeMax,
            rangeAxisHeight, labelLineHeight,
            rangeOveriddenLow, rangeOveriddenHigh);
    
    if (ticks.length == 0) {
      return ticks;
    }

    if (rangeOveriddenLow) {
      adjustedRangeMin = rangeMin;
    } else {
      adjustedRangeMin = ticks[0];
      
    }
    if(rangeOveriddenHigh) {
      adjustedRangeMax = rangeMax;
    }
    else {
      final double largestComputedTick = ticks[ticks.length - 1];
      adjustedRangeMax = Math.max(rangeMax, largestComputedTick);
    }

    return ticks;
  }

  public TickLabelNumberFormatter createDefaultTickLabelNumberFormatter() {
    return new DefaultTickLabelNumberFormatter();
  }

  public double dataToUser(double dataValue) {
    return (dataValue - adjustedRangeMin) / (adjustedRangeMax - adjustedRangeMin);
  }

  /**
   * Make this range axis the current focus. Has not effect the plot has
   * multi-axis set to true. Otherwise, this axis becomes the primary left axis
   * displayed.
   */
  @Export
  public void focus() {
    getAxisPanel().setValueAxis(this);
    plot.damageAxes();
    getAxisPanel().getParent().layout();
    ((DefaultXYPlot) plot).redraw(true);
  }

  public int getAxisIndex() {
    return axisIndex;
  }

  public RangeAxisPanel getAxisPanel() {
    return this.axisPanel;
  }

  public Interval getExtrema() {
    return new Interval(adjustedRangeMin, adjustedRangeMax);
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
  
  public Interval getRangeInterval() {
    return new Interval(absRangeMin, absRangeMax);
  }

  @Export
  public double getScale() {
    return scale;
  }

  public TickLabelNumberFormatter getTickLabelFormatter() {
    return tickLabelNumberFormatter;
  }

  @Export
  public boolean isAllowAutoScale() {
    return allowAutoScale;
  }

  @Export
  public boolean isAllowScientificNotation() {
    return allowScientificNotation;
  }

  @Export
  public boolean isAutoZoomVisibleRange() {
    return autoZoom;
  }

  /**
   * See {@link #setCalcRangeAsPercent(boolean)}
   */
  @Export
  public boolean isCalcRangeAsPercent() {
    return calcRangeAsPercent;
  }

  @Export
  public boolean isForceScientificNotation() {
    return forceScientificNotation;
  }

  @Export
  public boolean isScientificNotationOn() {
    return scientificNotationOn;
  }

  public boolean isShowScale() {
    return showExponents;
  }

  public void resetVisibleRange() {
    visRangeMin = Double.POSITIVE_INFINITY;
    visRangeMax = Double.NEGATIVE_INFINITY;
  }

  /**
   * If set to true, allow axis ticks to be scaled automatically by powers of
   * thousand if they exceed maxDigits settings. For example, if max digits is
   * 4, then the number 25000 will be rendered as 25, and the axis label will be
   * modified to include the word "Thousands". setAllowScientificNotation() will
   * override this and take priority, as well as setScale().
   */
  @Export
  public void setAllowAutoScale(boolean allowAutoScale) {
    this.allowAutoScale = allowAutoScale;
  }

  /**
   * If enabled (true by default), when maxTickLabelDigits is exceeded, labels
   * will be rendered in scientific notation.
   */
  @Export
  public void setAllowScientificNotation(boolean enable) {
    allowScientificNotation = enable;
  }

  /**
   * Setting to <tt>true</tt> will cause the range-Y to be between min and max
   * values of the visible domain, making this range calculate each time the zoom
   * or the domain is changed, otherwise the range-Y is fixed to the min and max
   * values of all the domain.
   * 
   * @param autoZoom
   */
  @Export
  public void setAutoZoomVisibleRange(boolean autoZoom) {
    if (this.autoZoom != autoZoom) {
      this.autoZoom = autoZoom;
      if (plot != null) {
        plot.damageAxes();
        adjustAbsRanges();
        plot.reloadStyles();
      }
    }
  }

  public void setAxisIndex(int axisIndex) {
    this.axisIndex = axisIndex;
  }

  public void setAxisPanel(RangeAxisPanel r) {
    this.axisPanel = r;
  }

  /**
   * Setting to <tt>true</tt> will cause all of the range-Y values to be
   * converted to a percentage increase/decrease from some reference range-Y
   * value (either the first datapoint of the dataset or the leftmost datapoint
   * that's visible in the plot).
   */
  @Export
  public void setCalcRangeAsPercent(boolean b) {
    this.calcRangeAsPercent = b;
    this.plot.damageAxes();
    adjustAbsRanges();
  }

  /**
   * Force tick labels to always be rendered in scientific notation. (Default
   * false);
   */
  @Export
  public void setForceScientificNotation(boolean force) {
    forceScientificNotation = force;
  }

  @Export
  public void setLabel(String label) {
    super.setLabel(label);
    plot.damageAxes();
    axisPanel.computeLabelWidths(view);
  }

  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }

  /**
   * Set the minimum and maximum visible axis range.
   */
  @Export
  public void setRange(double rangeLow, double rangeHigh) {
    rangeOveriddenLow = rangeOveriddenHigh = true;
    setAbsRange(rangeLow, rangeHigh);
    this.plot.reloadStyles();
  }

  /**
   * Set the maximum visible axis range.
   */
  @Export
  public void setRangeMax(double rangeHigh) {
    rangeOveriddenHigh = true;
    setAbsRange(absRangeMin, rangeHigh);
    this.plot.reloadStyles();
  }

  /**
   * Set the minimum visible axis range.
   */
  @Export
  public void setRangeMin(double rangeLow) {
    rangeOveriddenLow = true;
    setAbsRange(rangeLow, absRangeMax);
    this.plot.reloadStyles();
  }

  /**
   * Set a scale factor for displaying axis tick values
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
   */
  @Export
  public void setTickLabelNumberFormatter(
      TickLabelNumberFormatter tickLabelNumberFormatter) {
    this.tickLabelNumberFormatter = tickLabelNumberFormatter;
  }

  /**
   * Set the number format used to render ticks
   */
  @Export
  public void setTickNumberFormat(String format) {
    if (format == null) {
      tickLabelNumberFormatter = defaultTickLabelNumberFormatter;
    } else {
      setTickLabelNumberFormatter(
          new UserTickLabelNumberFormatter(view, format));
    }
  }

  public void setView(View view) {
    this.view = view;
  }

  public void setVisibleRange(double visRangeMin, double visRangeMax) {
    this.visRangeMin = visRangeMin;
    this.visRangeMax = visRangeMax;
    ticks = null;
    calcTickPositions();
  }

  public void setVisibleRangeMax(double visRangeMax) {
    this.visRangeMax = visRangeMax;
    ticks = null;
  }

  public void setVisibleRangeMin(double visRangeMin) {
    this.visRangeMin = visRangeMin;
    ticks = null;
  }

  public double userToData(double userValue) {
    return adjustedRangeMin + ((adjustedRangeMax - adjustedRangeMin) * userValue);
  }

  /**
   * Adjusts {@link #absRangeMin} and {@link #absRangeMax} to the smallest
   * interval that fully encompasses
   */
  private void adjustAbsRanges() {
    this.setAbsRange(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    Datasets datasets = plot.getDatasets();
    for (int i = 0; i < datasets.size(); i++) {
      Dataset dataset = datasets.get(i);
      if (dataset.getAxisId(0).equals(this.getAxisId())) {
        this.adjustAbsRange(dataset);
      }
    }
  }

  /**
   * Sets {@link #absRangeMin} and {@link #absRangeMax} to the specified
   * values.
   */
  private void setAbsRange(double absRangeMin, double absRangeMax) {
    ticks = null;
    this.absRangeMin = absRangeMin;
    this.absRangeMax = absRangeMax;
  }
}
