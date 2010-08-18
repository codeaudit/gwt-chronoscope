package org.timepedia.chronoscope.client.util.date;

import com.google.gwt.i18n.client.TimeZone;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.TimeUnit;

import java.util.Date;
import org.timepedia.chronoscope.client.util.DateFormatter;

/**
 * A specialized date class for use with the Chronoscope framework.
 *
 * @author chad takahashi
 */
public abstract class ChronoDate {

  public static boolean isTimeZoneOffset=false;

  private static double timeZoneOffsetInMilliseconds=0;

  /**
   * Factory method that creates a new date object for the specified timeStamp.
   */
  public static final ChronoDate get(double timeStamp) {
//    return new DefaultChronoDate(timeStamp);
    return new FastChronoDate(timeStamp);
  }

  public static final ChronoDate get(int year, int month, int day) {
    return new FastChronoDate(year, month, day);
  }

  /**
   * Returns a date representing the current time/date.
   */
  public static final ChronoDate getSystemDate() {
    return get(new Date().getTime());
  }

  public abstract boolean isLeapYear();

  public static int isoWeekday(DayOfWeek weekday) { 
    int dow = 0;
    switch(weekday) {
      case MONDAY: dow = 1; break;
      case TUESDAY: dow = 2; break;
      case WEDNESDAY: dow = 3; break;
      case THURSDAY: dow = 4; break;
      case FRIDAY: dow = 5; break;
      case SATURDAY: dow = 6; break;
      case SUNDAY: dow = 7; break;
    }

    return dow;
  }

  /**
   * Adds the specified number of time units to this date. Implementations of
   * this method are expected to handle "rollover" scenarios, where adding
   * <tt>numUnits</tt> to this date would cause the next largest time unit to
   * increment.
   */
  public abstract void add(TimeUnit timeUnit, int numUnits);

  /**
   * Returns a copy of this date.
   */
  public ChronoDate copy() {
    return ChronoDate.get(this.getTime());
  }

  /**
   * Copies the state of this date over to the target date.
   */
  public void copyTo(ChronoDate target) {
    ArgChecker.isNotNull(target, "target");
    // TODO: Subclasses can potentially speed up this calc if we check if 
    // 'target' is also an instance of this class and, if so, manually transfer 
    // over the fields.
    target.setTime(this.getTime());
  }

  /**
   * Returns the year, month, day, etc. portion of this date object.
   *
   * @param timeUnit - The portion of this date whose value is to be returned
   */
  public final int get(TimeUnit timeUnit) {
    switch (timeUnit) {
      case CENTURY:
        return getYear() / 100;
      case DECADE:
        return getYear() / 10;
      case YEAR:
        return getYear();
      case MONTH:
        return getMonth();
      case WEEK:
        return getWeekOfYear();
      case DAY:
        return getDay();
      case HOUR:
        return getHour();
      case MIN:
        return getMinute();
      case SEC:
        return getSecond();
      case MILLENIUM: // define this near/at the bottom, as it's used less frequently
        return (getYear() / 1000);
      default:
        throw new UnsupportedOperationException(
            "TimeUnit " + timeUnit + " not supported at this time");
    }
  }

  /**
   * Returns the number of days in this date's month.
   */
  public abstract int getDaysInMonth();

  /**
   * Returns the day of the week that this date falls on.
   */
  public abstract DayOfWeek getDayOfWeek();

  public abstract int getDay();

  public abstract int getHour();

  public abstract int getMinute();

  public abstract int getMonth();

  public abstract int getSecond();

  /**
   * Analogous to <tt>java.util.Date.getTime()</tt>.
   */
  public abstract double getTime();

  public abstract int getYear();

  /**
   *  @return ordinal day (1-366)
   */
  public abstract int getDayOfYear();

  public abstract int getWeekOfYear();

  /**
   * Sets the constituent components of this date via method chaining. For
   * example, to set the date to 'January 19th, 1956':
   * <blockquote><pre>
   * myDate.set().year(1956).month(0).day(19).done();
   * </pre></blockquote>
   *
   * Don't forget to call {@link DateFieldSetter#done()}! Otherwise the date
   * modifications will not take effect.
   */
  public abstract DateFieldSetter set();

  /**
   * Sets the specified date field to the specified value. <p> NOTE: If you need
   * to set more than 1 date field, use {@link #set()} instead.  This method
   * defers date field validation until the {@link DateFieldSetter#done()}
   * method is called.
   */
  public abstract void set(TimeUnit dateField, int value);

  /**
   * Analagous to <tt>java.util.Date.setTime()</tt>.
   */
  public abstract void setTime(double ms);

  /**
   * Truncates this date up to the specified time unit. For example, if
   * <tt>myDate = '1987-Mar-12 14:30:59'</tt>, then <tt>truncate(myDate,
   * TimeUnit.MONTH)</tt> will truncate this date up to the month resulting in
   * the date <tt>'1987-Mar-01 00:00:00'</tt>.
   *
   * @throws UnsupportedOperationException if the specified timeUnit is not
   *                                       supported by a particular subclass.
   */
  public abstract ChronoDate truncate(TimeUnit timeUnit);

  /**
   * If you have to consider time zone,convert the current time zone to set time zone
   * @return Local time zone and set the time zone difference between the value
   */
  public static double timeZoneConverter() {
      Date date = new Date();
      int localTimeZoneOffsetInMilliseconds = date.getTimezoneOffset() * 60 * 1000;
      if (isTimeZoneOffset) {
          return timeZoneOffsetInMilliseconds + localTimeZoneOffsetInMilliseconds;
      }
      return 0;
  }

  /**
   * If you need a String date, and consider the set time zone or the local time zone 
   * @param dateFormatter
   * @param d
   * @return String date about timezone
   */
  public static String formatDateByTimeZone(DateFormatter dateFormatter,double d){
    if(isTimeZoneOffset){
     TimeZone timeZone=TimeZone.createTimeZone((int)-timeZoneOffsetInMilliseconds/(60*1000));
     return dateFormatter.format(d, timeZone);
    }
    return dateFormatter.format(d);
  }

  public static double getTimeZoneOffsetInMilliseconds() {
      return timeZoneOffsetInMilliseconds;
  }

  public static void setTimeZoneOffsetInMilliseconds(double timeZoneOffsetInMilliseconds) {
      ChronoDate.timeZoneOffsetInMilliseconds = timeZoneOffsetInMilliseconds;
  }
}
