/**
 * 
 */
package org.timepedia.chronoscope.client.util.date;

import org.timepedia.chronoscope.client.util.TimeUnit;

import java.util.Date;

/**
 * A specialized date class for use with the Chronoscope framework.
 * 
 * @author chad takahashi
 */
public abstract class ChronoDate {

  /**
   * Factor method that creates a new date object for the specified timeStamp.
   */
  public static final ChronoDate get(double timeStamp) {
    // NOTE: this can be replaced with a homegrown high-performance
    // implementation
    // at some point.
    return new DefaultChronoDate(timeStamp);
  }

  /**
   * Returns a date representing the current time/date.
   */
  public static final ChronoDate getSystemDate() {
    return get(new Date().getTime());
  }

  /**
   * Adds the specified number of time units to this date. Implementations of
   * this method are expected to handle "rollover" scenarios, where adding
   * <tt>numUnits</tt> to this date would cause the next largest time unit to
   * increment.
   */
  public abstract void add(TimeUnit timeUnit, int numUnits);

  /**
   * Returns the year, month, day, etc. portion of this date object.
   * 
   * @param timeUnit - The portion of this date whose value is to be returned
   */
  public abstract int get(TimeUnit timeUnit);

  public abstract int getDay();

  public abstract int getHour();

  public abstract int getMinute();

  public abstract int getMonth();

  public abstract int getSecond();

  public abstract double getTime();

  public abstract int getYear();

  public abstract boolean isFirstOfMonth();

  /**
   * Sets the specified time unit to the specified value.
   */
  public abstract void set(TimeUnit timeUnit, int value);

  /**
   * Analagous to <tt>java.util.Date.setTime()</tt>.
   */
  public abstract void setTime(double ms);

  /**
   * Truncates this date down to the specified time unit. For example, if
   * <tt>myDate = '1987-Mar-12 14:30:59'</tt>, then
   * <tt>truncate(myDate, TimeUnit.MONTH)</tt> will truncate this date up to
   * the month resulting in the date <tt>'1987-Mar-12 00:00:00'</tt>.
   * 
   * @throws UnsupportedOperationException if the specified timeUnit is not
   *           supported by a particular subclass.
   */
  public abstract ChronoDate truncate(TimeUnit timeUnit);
}
