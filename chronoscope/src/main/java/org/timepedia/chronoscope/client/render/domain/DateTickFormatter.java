package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;

/**
 * Provides functionality for rendering the date/time ticks in a context-sensitive
 * way depending on the current domain interval.
 * 
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 * @author Chad Takahashi &lt;chad@timepedia.org&gt;
 */
public abstract class DateTickFormatter extends TickFormatter {
  
  protected DateFormatHelper dateFormat = new DateFormatHelper();
  
  protected ChronoDate currTick = ChronoDate.getSystemDate();
  
  /**
   * Subclasses assign this field upon construction.
   * 
   * @see #getTickInterval()
   */
  protected TimeUnit timeUnitTickInterval;
    
  /**
   * Constructs a new formatter.
   *  
   * @param longestPossibleLabel Represents the the longest possible label that
   * could occur, given the set of all labels for this formatter.  For example,
   * if this formatter formatted days of the week, then "Saturday" should be used,
   * since it is the longest name of the 7 days.
   */
  public DateTickFormatter(String longestPossibleLabel) {
    super(longestPossibleLabel);
  }
  
  @Override
  public double getTickDomainValue() {
    return currTick.getTime();
  }

  /**
   * Returns a positive value corresponding to a single tick for this formatter.
   * For example, if this is a day-of-month formatter, then this method would
   * return {@link TimeUnit#DAY#ms()}.
   */
  public final double getTickInterval() {
    return timeUnitTickInterval.ms();
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
  public int incrementTick(int numTimeUnits) {
    ChronoDate date = currTick;
    date.add(timeUnitTickInterval, numTimeUnits);
    date.getTime();
    return numTimeUnits;
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
  public void resetToQuantizedTick(double timeStamp, int tickStep) {
    currTick.setTime(timeStamp);
    currTick.truncate(this.timeUnitTickInterval);
    int normalizedValue = 
        MathUtil.quantize(currTick.get(this.timeUnitTickInterval), tickStep);
    currTick.set(this.timeUnitTickInterval, normalizedValue);
  }
  
}
