package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.MipMap;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.DatasetRenderer;
import org.timepedia.chronoscope.client.render.RangeAxisPanel;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * A RangeAxis is an ValueAxis that represents values, typically on the y-axis.
 */
@ExportPackage("chronoscope")
// TODO - might have to expose a way to change these or otherwise choose the
// most appropriate ones for the locale or units.  E.g. "billion" and "trillion" aren't
// the same magnitude everywhere.

public class RangeAxis extends ValueAxis implements Exportable {

  private static int MAX_TICKS = 8; // the default max number of ticks

  private static final String posExponentLabels[] = {"", "(tens)", "(hundreds)",
        "(thousands)", "(tens of thousands)", "(hundreds of thousands)",
        "(millions)", "(tens of millions)", "(hundreds of millions)",
        "(billions)", "(tens of billions)", "(hundreds of billions)",
        "(trillions)", "(tens of trillions)", "(hundreds of trillions)"};

  private static final String negExponentLabels[] = {"", "(tenths)",
        "(hundredths)", "(thousandths)", "(ten thousandths)",
        "(hundred thousandths)", "(millionths)", "(ten millionths)",
        "(hundred millionths)", "(billionths)", "(ten billionths)",
        "(hundred billionths)", "(trillionths)", "(ten trillionths)",
        "(hundred trillionths)"};

  // by 1000^n: 1000^0, 1000^1, 1000^2, ...
  private static final String SIposSymbol[] = {"", "k", "M", "G", "T", "P", "E", "Z", "Y"};
  private static final String SIposPrefix[] = {"", "kilo", "mega", "giga", "tera", "peta", "exa", "zetta", "yotta"};

  private static final String SInegSymbol[] = {"", "m", "μ", "n", "p", "f", "a", "z", "y"};
  private static final String SInegPrefix[] =  {"", "milli", "micro", "nano", "pico", "femto", "atto", "zepto", "yocto"};

  private class DefaultTickLabelNumberFormatter implements TickLabelNumberFormatter {
    String labelFormat = "###0.####";
    // String labelFormat = "0." + "0#########".substring(MAX_DIGITS);

    public String format(double value) {
      String symbol = ""; // , prefix = "";
      double rscale = getScale();
      int scaleLog10 = (int) Math.log10(Math.abs(rscale));
      int sigdig = getTickLabelSigDig();
      if (isCalcRangeAsPercent()) {
        scientificNotationOn = false;
        labelFormat = zeroish(value) ? "0" : "###.##";
        symbol = "%";
        // value = value/getRangeExtrema.length
      } else if (isForceScientificNotation() ||
            (isAllowScientificNotation() && (Math.abs(scaleLog10) >= MAX_DIGITS))) {
        scientificNotationOn = true;
        // labelFormat = "0." + "##########".substring(MAX_DIGITS-1) + "E0";
        labelFormat = zeroish(value) ? "0" : "0."+"00000000".substring(0, sigdig-1)+"E0";
      } else if (isScaleSI() || (isAllowAutoScale() && (Math.abs(scaleLog10) >= MAX_DIGITS))) {
        scientificNotationOn = false;
        int SIscale = scaleLog10/3;
        // prefix = (rangeAxisScale<0) ? SInegPrefix[-SIscale] : SIposPrefix[SIscale];
        // symbol = (rangeAxisScale<0) ? SInegSymbol[-SIscale] : SIposSymbol[SIscale];
        if (zeroish(value - getRangeInterval().getEnd())) {
          symbol = (scaleLog10<0) ? SInegSymbol [-SIscale] + getAxisId() + negExponentLabels[-scaleLog10] : SIposSymbol[SIscale];
        } else {
          symbol = (scaleLog10<0) ? SInegSymbol [-SIscale] + getAxisId() : SIposSymbol[SIscale];
        }
        value = value/rscale;
        labelFormat = zeroish(value) ? "0" : sigformat(rscale, sigdig);
      }
      return view.numberFormat(labelFormat + symbol, value);
    }
    public String getFormat() {
      return labelFormat;
    }
  }

  private class UserTickLabelNumberFormatter implements TickLabelNumberFormatter {

    private View view;

    private String format;

    public UserTickLabelNumberFormatter(View view, String format) {
      this.view = view;
      this.format = format;
    }

    public String format(double value) {
      return view.numberFormat(format, value);
    }

    public String getFormat() {
      return format;
    }
  }

  public static final int MAX_DIGITS = 5;


  /**
   * Calculates the percentage difference from value v1 to value v2.  For
   * example, if v1 = 10 and v2 = 15, then the % difference is 50% ( 15.0/10.0 )
   */
  public static double calcRatio(double ref, double delta) {
    return Math.abs(delta-ref)/Math.abs(ref);
  }

   /**
   * Calculates the percentage difference from value v1 to value v2 in a given interval
   * example, if v1 = 10 and v2 = 15 on an interval from 0,100, then percent difference
   * is 5% ( 5.0/100.0 )
   */
  public static double calcPercentDifference(Interval length, double ref, double delta) {
    return Math.abs(delta-ref)/length.length();
  }
  // private static double[] computeLinearTickPositions(double lrangeLow,
  private double[] computeLinearTickPositions(double lrangeLow,
      double lrangeHigh, double axisHeight, double tickLabelHeight,
      boolean forceLowestTick, boolean forceHighestTick) {

    // TESTING
    forceHighestTick = true;
    forceLowestTick = true; // isCalcRangeAsPercent() ? true : forceLowestTick;

    if (zeroish(lrangeHigh - lrangeLow)) {
      if (lrangeHigh != 0.0) {
        int logRange = ((int) Math.floor(Math.log10(lrangeHigh)));
        if (logRange < 0) {
          logRange += 1;
        }
        double exponent = Math.pow(10, logRange);
        double rounded = Math.floor(lrangeHigh / exponent);
        lrangeHigh = forceHighestTick ? lrangeHigh : (rounded + 1) * exponent;
        lrangeLow = forceLowestTick ? lrangeLow : (rounded - 1) * exponent;
      } else {
        lrangeHigh = 1.0;
        lrangeLow = -1.0;
      }
    }


    int numTicks = (int) (axisHeight/(2.0 * tickLabelHeight));
    numTicks = MAX_TICKS < numTicks ? MAX_TICKS : numTicks;
    // if values straddle zero, try to make zero one of the ticks
    boolean spansZero = !zeroish(lrangeHigh) && !zeroish(lrangeLow) && (lrangeHigh * lrangeLow < 0);
    double range = Math.abs(lrangeHigh - lrangeLow);
    double larger = Math.max(lrangeLow, lrangeHigh);
    // int ticksInSmaller = (int)(numTicks * (range-larger)/range);
    // double roughInterval = spansZero ? range/(numTicks - 2): range/(numTicks - 2);
    // double roughInterval = spansZero? larger/(numTicks - 3) : range/(numTicks - 2);

    double roughInterval = range / (1.0 * (numTicks - 1));

    int logRange = ((int) Math.floor(Math.log10(roughInterval))) - 1;
    double exponent = Math.pow(10, logRange);
    int smoothSigDigits = (int) (roughInterval / exponent);
    smoothSigDigits = smoothSigDigits % 5 == 0 ? smoothSigDigits : smoothSigDigits - (int) MathUtil.mod(smoothSigDigits, 5.0);
    double smoothInterval = smoothSigDigits * exponent;
    double epsilon = smoothInterval/2.0;
    double edge = larger % smoothInterval;
      /*
    if (forceHighestTick) {
      if (spansZero) {
        // back off if crowded
        edge = larger % smoothInterval;
        if (!zeroish(edge) && (epsilon > edge)) {
            // numTicks = numTicks > 3 ? numTicks - 1 : numTicks + 1;
            numTicks = numTicks > 5 ? numTicks - 2 : numTicks++;
            roughInterval = range/(numTicks-1);
            numTicks = (int) (range / roughInterval) + 1;

            logRange = ((int) Math.floor(Math.log10(roughInterval))) - 1;
            exponent = Math.pow(10, logRange);
            roughInterval = range/(numTicks -1);
            smoothSigDigits = (int) (roughInterval / exponent);
            smoothInterval = smoothSigDigits * exponent;
        }
      } else {
        // back off if the top is crowded
        edge = lrangeHigh % smoothInterval;
        if (!zeroish(edge) && (epsilon > edge)) {
            // numTicks = numTicks > 2 ? numTicks - 1 : numTicks + 1;
            // numTicks--;
            roughInterval = range/(numTicks - 1);
            numTicks = (int) (range/roughInterval) + 1;

            logRange = ((int) Math.floor(Math.log10(roughInterval))) - 1;
            exponent = Math.pow(10, logRange);
            roughInterval = range/(numTicks-1);
            smoothSigDigits = (int) (roughInterval / exponent);
            smoothInterval = smoothSigDigits * exponent;
        }
      }
    } */

    tickLabelSigDig = String.valueOf(smoothSigDigits*numTicks).length();
    // double roundedStart = lrangeLow - MathUtil.mod(lrangeLow, 5.0 * exponent);
    double roundedStart = lrangeLow < 0 ? Math.ceil(-lrangeLow/smoothInterval) * smoothInterval :
            Math.floor(lrangeLow/smoothInterval) * smoothInterval;
    double tickValue = forceLowestTick ? lrangeLow : roundedStart;

    if (spansZero) {
      int posTicks = numTicks - (int)Math.floor(-tickValue/smoothInterval);
      double delta = lrangeHigh - posTicks*smoothInterval;
      if (delta > epsilon) {
        numTicks += (int) Math.ceil((delta-epsilon)/smoothInterval);
      }
    } else {
      double delta = lrangeHigh - numTicks*smoothInterval - tickValue;
      if (delta > epsilon) {
        numTicks += (int) Math.ceil((delta-epsilon)/smoothInterval);
      }
    }

    double tickPositions[] = new double[numTicks];

    tickPositions[0] = tickValue;
    for (int i = 1; i < tickPositions.length; i++) {
      if (tickValue < 0) {
         int negIntervals = (int) ((Math.abs(tickValue)-epsilon)/smoothInterval);
         if (negIntervals > 0) {
           tickValue = - smoothInterval * negIntervals;
         } else {
           double testtick = tickValue + smoothInterval;
           if (Math.abs(testtick) < Math.abs(epsilon)) { // if close to zero but still negative, use zero
               tickValue = 0;
           } else if ((testtick * tickValue) < 0) { // if crossed from neg to pos, use zero
               tickValue = 0;
           }
         }
      } else {
          int posIntervals = (int) (Math.abs(lrangeHigh - epsilon - tickValue)/(smoothInterval));
          if ((tickPositions.length-1)== i) {
            tickValue = forceHighestTick ? lrangeHigh : smoothInterval * posIntervals;
          } else {
            tickValue += smoothInterval;
          }
      }
      tickPositions[i] = tickValue;
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

  private double absRangeMin = Double.POSITIVE_INFINITY, absRangeMax = Double.NEGATIVE_INFINITY;

  private double visRangeMin = Double.POSITIVE_INFINITY, visRangeMax = Double.NEGATIVE_INFINITY;

  private double adjustedRangeMin = Double.POSITIVE_INFINITY, adjustedRangeMax = Double.NEGATIVE_INFINITY;

  private RangeAxisPanel axisPanel;

  private boolean rangeOveriddenLow, rangeOveriddenHigh;

  private double absScale = Double.NaN, visScale = Double.NaN;

  private boolean scaleSI = false;

  private boolean scientificNotationOn;

  private boolean showExponents = false;

  private double[] ticks;

  private TickLabelNumberFormatter tickLabelNumberFormatter;

  private int tickLabelSigDig;

  private double absAxisLength, visAxisLength;

  private double tickLabelHeight;

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
        // rangeLength = rangeExtrema.length();
        setAbsLength(rangeMin, rangeMax);

        if (calcRangeAsPercent) {
          final double refY = dr.getRange(ds.getFlyweightTuple(0));
          double rmin = rangeOveriddenLow ? absRangeMin : rangeMin;
          double rmax = rangeOveriddenHigh ? absRangeMax : rangeMax;
          double normalizedMin = rangeExtrema.getPercentChange(refY, rmin);
          double normalizedMax = rangeExtrema.getPercentChange(refY, rmax);
          setAbsRange( normalizedMin, normalizedMax);
        } else {
          setAbsRange(
            Math.min(absRangeMin, rangeOveriddenLow ? absRangeMin : rangeMin),
            Math.max(absRangeMax, rangeOveriddenHigh ? absRangeMax : rangeMax));
        }
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

    tickLabelHeight = Math.min(axisPanel.getMaxLabelHeight(), 12);
    double rangeAxisHeight = axisPanel.getBounds().height - tickLabelHeight;

    ticks = computeLinearTickPositions(rangeMin, rangeMax,
            rangeAxisHeight, tickLabelHeight,
            rangeOveriddenLow, rangeOveriddenHigh);

    if (ticks.length == 0) {
      return ticks;
    }

    adjustedRangeMin = rangeOveriddenLow ? rangeMin : Math.min(ticks[0], rangeMin);
    adjustedRangeMax = rangeOveriddenHigh ? rangeMax : Math.max(ticks[ticks.length-1], rangeMax);

    return ticks;
  }

  public TickLabelNumberFormatter createDefaultTickLabelNumberFormatter() {
    return new DefaultTickLabelNumberFormatter();
  }

  public double dataToUser(double dataValue) {
    return (dataValue - adjustedRangeMin) / (adjustedRangeMax - adjustedRangeMin);
    // return (dataValue - getRangeInterval().getStart()) / getAxisLength();
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

  // TODO - export as axis.index rather than axis.getIndex() ?
  @Export
  public int getAxisIndex() {
    return axisIndex;
  }

  public RangeAxisPanel getAxisPanel() {
    return this.axisPanel;
  }

  public Interval getExtrema() {
    return new Interval(adjustedRangeMin, adjustedRangeMax);
  }

  public String getFormattedLabel(double value) {
    return tickLabelNumberFormatter.format(value);
  }

  // TODO - export as axis.label rather than axis.getLabel() ?
  @Export
  public String getLabel() {
//    double s = Double.isNaN(getScale()) ? 1.0 : getScale();
//    return super.so wgetLabel() + getLabelSuffix(getRange());
    String axisId = getAxisId();
    if (axisId == null || "".equals(axisId)) {
      return getLabelPrefix() + super.getLabel() + getLabelSuffix();
    } else {
      return getLabelPrefix() + axisId + getLabelSuffix();
      // String symbol = (intDigits<0) ? SInegSymbol[SIscale] : SIposSymbol[SIscale];
      // return axisId;
    }
  }

  public String getLabelPrefix() {
      return "";
      /*
      if (isCalcRangeAsPercent()) {
          // double percent = Math.abs(getAxisLength()/100.0);
          // String fmt = sigformat(percent, getTickLabelSigDig());
          return ""; // return "1% = "+ view.numberFormat(fmt, percent) + " ";
      } else if (getScale()<1) {  //getScale isn't safe to call during axis label rangepanel setup
          return ""; // return "1/"+ view.numberFormat("###,###,###,###", (int) (1/getScale())) + " ";
      } else {
          return ""; // return view.numberFormat("###,###,###,###", (int) getScale()) + " ";
      } */
  }
  public String getLabelSuffix() {
    if (isCalcRangeAsPercent()) {
        return " %";
        // TODO - maybe describe the real range value span or what each percent means
        // eg: 1% ~ 1,000,000 barrels of oil

    }
    return "";
  }

    /**
     *
     * @return range interval of tick labels, to get the underlying real range length when isCalcPercent use getAxisLength
     */
  public Interval getRangeInterval() {
    return new Interval(absRangeMin, absRangeMax);
  }

    /**
     *
     * @return range extrema interval length of real underlying values (not percentages)
     */
  public double getAxisLength() {
    if (isCalcRangeAsPercent()) {
        // TODO - should use the right dimension/tupleCoord if >0
        plot.getDatasets().get(this.getAxisIndex()).getRangeExtrema(0).length();
    }
    return isAutoZoomVisibleRange() ?  visAxisLength : absAxisLength;
  }

  @Export
  public double getScale() {
    double rscale = isAutoZoomVisibleRange() ? visScale : absScale;
    if (Double.isNaN(rscale)) {
        // Interval realExtrema = plot.getRangeAxis(this.getAxisIndex()).getExtrema();
        // this.setAbsRange(realExtrema.getStart(), realExtrema.getEnd());
        // this.setVisibleRange(realExtrema.getStart(), realExtrema.getEnd());
        adjustAbsRanges();
    }
    return isAutoZoomVisibleRange() ? visScale : absScale;
  }

  public double getTickLabelHeight() {
    return tickLabelHeight;
  }

  public int getTickLabelSigDig() {
    return tickLabelSigDig;
  }
  public TickLabelNumberFormatter getTickLabelFormatter() {
    return tickLabelNumberFormatter;
  }

  @Export
  public String getTickNumberFormat() {
    return tickLabelNumberFormatter.getFormat();
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
  public boolean isScaleSI() {
    return scaleSI;
  }

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
    if (enable) {
      allowAutoScale = false;
      scaleSI = false;
    }
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
        ((DefaultXYPlot) plot).redraw(true);
        // plot.reloadStyles();
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
  public void setCalcRangeAsPercent(boolean calcRangeAsPercent) {
    if (calcRangeAsPercent != this.calcRangeAsPercent) {
      if (calcRangeAsPercent) {
        this.calcRangeAsPercent = calcRangeAsPercent;
        this.plot.damageAxes();
        adjustAbsRanges();
      } else {
        this.rangeOveriddenHigh = false;
        this.rangeOveriddenLow = false;
        this.calcRangeAsPercent = calcRangeAsPercent;
        adjustAbsRanges();
        // Interval realExtrema = plot.getDatasets().get(this.getAxisIndex()).getRangeExtrema(0);
        // this.setAbsRange(realExtrema.getStart(), realExtrema.getEnd());
        // this.setVisibleRange(realExtrema.getStart(), realExtrema.getEnd());
        this.plot.damageAxes();
      }
    }
  }

  /**
   * Force tick labels to always be rendered in scientific notation. (Default
   * false);
   */
  @Export
  public void setForceScientificNotation(boolean force) {
    if (forceScientificNotation != force) {
      if (force) {
        allowAutoScale = false;
        calcRangeAsPercent = false;
        scaleSI = false;
      } // else { }
      forceScientificNotation = force;
    }
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
    setVisibleRange(rangeLow, rangeHigh);
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
    if (isAutoZoomVisibleRange()) {
      this.visScale = scale;
    } else {
      this.absScale = scale;
    }
  }

  /**
   * Use SI scale prefixes and symbol abbreviations (kilo,k), (milli,m), (giga, G), etc.
   * @param scaleSI
   */
  @Export
  public void setScaleSI(boolean scaleSI) {
    if (this.scaleSI != scaleSI) {
      if(scaleSI){
        forceScientificNotation = false;
        calcRangeAsPercent = false;
      } // else { }
      this.scaleSI = scaleSI;
    }
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
    ticks = null;

    this.visRangeMin = visRangeMin;
    this.visRangeMax = visRangeMax;

    ticks = calcTickPositions();

    setVisScale(calcScale(visRangeMin, visRangeMax));
    setVisLength(visRangeMin, visRangeMax);

    this.visRangeMin = visRangeMin;
    this.visRangeMax = visRangeMax;
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
    setAbsScale(calcScale(absRangeMin, absRangeMax));
    setAbsLength(absRangeMin, absRangeMax);
  }

  static double calcScale(double min, double max) {
    if (Double.isNaN(min) || Double.isInfinite(min) || Double.isNaN(max) || Double.isInfinite(max)) {
        return 1;
    }
    int minDigits = zeroish(min) ? 1 : (int) Math.floor(Math.log10(Math.abs(min)));
    int maxDigits = zeroish(max) ? 1 : (int) Math.floor(Math.log10(Math.abs(max)));
    double rscale = 1;
    int SIscale = Math.max(maxDigits, minDigits)/3;

    rscale = Math.pow(1000, SIscale);

    /* if ((minDigits * maxDigits) < 0) {
      rscale = (int)Math.pow(1000,((Math.max(maxDigits, minDigits))/3));
    } else {
      rscale = (int)Math.pow(1000,((maxDigits + minDigits)/6));
    }  */

    return rscale;
  }

  private void setAbsScale(double absScale) {
    this.absScale = absScale;
  }

  private void setVisScale(double visScale) {
    this.visScale = visScale;
  }

  private void setAbsLength(double min, double max) {
    if (Double.isInfinite(min) || Double.isInfinite(max) || Double.isNaN(min) || Double.isNaN(max)) {
      return;
    }
    this.absAxisLength = Math.abs(max-min);
  }

  private void setVisLength(double min, double max) {
    if (Double.isInfinite(min) || Double.isInfinite(max) || Double.isNaN(min) || Double.isNaN(max)) {
      return;
    }
    this.visAxisLength = Math.abs(max-min);
  }
  // TODO - move to util?
  private static boolean zeroish(double val) {
    return (Double.MIN_VALUE * 10 > Math.abs(val));
  }
  private static String sigformat(double rscale, int sigDigits) {
    String sigfmt = "##0.##";
    int scaleLog10 = (int) Math.log10(Math.abs(rscale));

    if (scaleLog10 >= 0) {
      String digStr = "#########0";
      sigfmt = digStr.substring(Math.max(digStr.length() - scaleLog10, 1));
      int leftOver = Math.max(sigDigits - scaleLog10, 0);
      if (leftOver > 0) {
        String decStr = "0000000000";
          sigfmt += "." + decStr.substring(decStr.length()-leftOver);
      }
    } else { // 1 > val > -1
      String decStr = "0000000000";
      sigfmt = "0." + decStr.substring(decStr.length()-sigDigits);
    }
    return sigfmt;
  }
}
