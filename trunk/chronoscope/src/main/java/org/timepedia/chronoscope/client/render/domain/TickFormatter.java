package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

/**
 * Provides functionality for rendering the domain axis ticks in a context-sensitive
 * way depending on the current domain interval.
 * 
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 * @author Chad Takahashi &lt;chad@timepedia.org&gt;
 */
public abstract class TickFormatter {
  
  protected DateFormatHelper dateFormat = new DateFormatHelper();
  
  /**
   * Stores the possible "tick steps" that are relevant for a given formatter.
   * For example, when dealing with minutes, people typically expect tick steps of 
   * 1, 2, 5, 10, 15, 30.  Whereas for months, people would expect tick steps
   * of 1, 2 (bimonthly), 3 (quarters), and 6 (semiannual).
   */
  protected int[] possibleTickSteps;
  
  /**
   * A pointer to the next formatter to use when the domain interval to format is 
   * too small for this formatter to handle.
   */
  protected TickFormatter subFormatter;
  
  /**
   * A pointer to this formatter's parent.
   */
  protected TickFormatter superFormatter;
  
  /**
   * Subclasses assign this field upon construction.
   * 
   * @see #getTickInterval()
   */
  protected TimeUnit tickInterval;
  
  private final String longestPossibleLabel;

  private double maxLabelWidth = -1;
  
  /**
   * Constructs a new formatter.
   *  
   * @param longestPossibleLabel Represents the the longest possible label that
   * could occur, given the set of all labels for this formatter.  For example,
   * if this formatter formatted days of the week, then "Saturday" should be used,
   * since it is the longest name of the 7 days.
   */
  public TickFormatter(String longestPossibleLabel) {
    ArgChecker.isNotNull(longestPossibleLabel, "longestPossibleLabel");
    this.longestPossibleLabel = longestPossibleLabel;
  }

  /**
   * Attempt to find the optimal tick step (taking into accout screen width, 
   * tick label width, and domain-context-dependent quantized tick steps).
   */
  public final int calcIdealTickStep(double domainWidth, int maxTicksForScreen) {
    int[] tickSteps = this.possibleTickSteps;
    final double tickDomainInterval = this.tickInterval.ms();
    
    // This is the smallest domain interval possible before the tick labels will
    // start running into each other
    final double minDomainInterval = domainWidth / (double)maxTicksForScreen;
    
    int idealTickStep = -1;
    for (int i = 0; i < tickSteps.length; i++) {
      int candidateTickStep = tickSteps[i];
      if (((double)candidateTickStep * tickDomainInterval) >= minDomainInterval) {
        idealTickStep = candidateTickStep;
        break;
      }
    }
    // TODO: find sensible fallback value if none of the quantized intervals will work
    if (idealTickStep == -1) {
      //throw new RuntimeException("Unable to find suitable tick interval");
      idealTickStep = tickSteps[tickSteps.length - 1];
    }
    
    return idealTickStep;
  }
  

  /**
   * Return a relative date, which assumes that a nearby fullTick is being
   * rendered to give the user a visual context. For example, if rendering a
   * month, then a relative tick label would be 'Nov', and if day, a relative
   * day would be '13'.
   */
  public abstract String formatRelativeTick(ChronoDate tickDate);

  /**
   * Return the screen width of the largest possible tick label for this
   * formatter.
   */
  public double getMaxTickLabelWidth(Layer layer, GssProperties axisProperties) {
    if (maxLabelWidth == -1) {
      maxLabelWidth = layer.stringWidth(longestPossibleLabel, axisProperties.fontFamily,
          axisProperties.fontWeight, axisProperties.fontSize);
    }
    return maxLabelWidth;
  }
  
  /**
   * Returns a suitable sub-tick step size for the given 'primaryTickStep'
   * (i.e. the tick spacing to be used for the labeled ticks).
   */
  public int getSubTickStep(int primaryTickStep) {
    // Subclasses may want to reduce this value to some smaller multiple to 
    // avoid too many subticks.
    return primaryTickStep;
  }
  
  /**
   * The time unit that corresponds to a single tick for this formatter.
   * For example, if this is a day-of-month formatter, then this method would
   * return {@link TimeUnit#DAY}.
   */
  public final TimeUnit getTickInterval() {
    return tickInterval;
  }

  /**
   * Increments <tt>date</tt> by the specified number of time units (where a 
   * time unit can be an hour, day, second, etc.).  Subclasses may sometimes 
   * need to override this method to modify the actual number of time units in
   * order to ensure that the associated tick labels are stable when scrolling.
   * 
   * @return the number of time units that were *actually* incremented; typically,
   * this value will be the same as the <tt>numTimeUnits</tt> input parameter, but
   * in the aforementioned subclass override case, a different value could 
   * get returned (the typical case for this is a date near the end of a month).
   */
  public int incrementDate(ChronoDate date, int numTimeUnits) {
    date.add(getTickInterval(), numTimeUnits);
    return numTimeUnits;
  }

  /**
   * Returns true if this formatter is capable of rendering the specified domain width 
   * (e.g. '2 years').
   * 
   * @param domainWidth The domain width, specified in milliseconds.
   */
  public boolean inInterval(double domainWidth) {
    if (isRootFormatter()) {
      return domainWidth > getTickInterval().ms();
    }
    else {
      double myTickWidth = getTickInterval().ms();
      double parentTickWidth = superFormatter.getTickInterval().ms();
      return domainWidth > myTickWidth && domainWidth <= parentTickWidth;
    }
  }
  
  /**
   * Returns true only if this formatter has no subformatter.
   */
  public final boolean isLeafFormatter() {
    return this.subFormatter == null;
  }

  /**
   * Returns true only if this formatter has no superformatter.
   */
  public final boolean isRootFormatter() {
    return this.superFormatter == null;
  }
  
  /**
   * Quantizes the specified timeStamp down to the specified tickStep.  For example, 
   * suppose this is a MonthTickFormatter, 
   * <tt>timeStamp = JUN-19-1985:22hrs:36min...</tt>, and 
   * <tt>tickStep = 3</tt> (in this context, '3' refers to 3 months).  This method
   * will return <tt>APR-1-1985:0hrs:0min, ...</tt>.
   * 
   * @param timeStamp -The point in time, specified in milliseconds, to be quantized
   * @param tickStep - The tick step to which the timeStamp will be quantized
   * @return the quantized date
   */
  public ChronoDate quantizeDate(double timeStamp, int tickStep) {
    ChronoDate d = ChronoDate.get(timeStamp).truncate(this.tickInterval);
    int normalizedValue = quantize(d.get(this.tickInterval), tickStep);
    d.set(this.tickInterval, normalizedValue);
    return d;
  }
  
  /**
   * Returns the largest integer that is less than or equal to <tt>value</tt>, 
   * and is also a multiple of <tt>factor</tt>.  For example, 
   * <code>quantize(10, 3)</code> would return the value 9.
   * 
   * @param value a non-negative value
   * @param factor a non-negative factor
   */
  static int quantize(int value, int factor) {
    return value - (value % factor);
    //return (value / factor) * factor;
  }
  
}
