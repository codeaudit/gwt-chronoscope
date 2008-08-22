/**
 * 
 */
package org.timepedia.chronoscope.client.util;

import java.util.Date;

/**
 * 
 * @author chad takahashi
 */
public final class SimpleDate {
  private double dateTimeStamp;
  private Date d;
  
  /**
   * Analagous to a bitwise-AND mask, this date mask facilitates creation of a
   * date object where time units smaller than a specified interval are ignored.
   */
  public enum Mask {
    Y,
    YM,
    YMD,
    YMDH,
    YMDHM,
    YMDHMS
  }
  
  public static SimpleDate get(SimpleDate d, Mask mask) {
    return get(d.getTime(), mask);
  }
  
  public static SimpleDate get(double dateTimeStamp, Mask mask) {
    Date d = new Date((long)dateTimeStamp);
    Date maskedDate;
    switch (mask) {
      case Y:
        maskedDate = new Date(d.getYear(), 0, 1);
        break;
      case YM:
        maskedDate = new Date(d.getYear(), d.getMonth(), 1);
        break;
      case YMD:
        maskedDate = new Date(d.getYear(), d.getMonth(), d.getDate());
        break;
      case YMDH:
        maskedDate = new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), 0, 0);
        break;
      case YMDHM:
        maskedDate = new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes(), 0);
        break;
      case YMDHMS:
        maskedDate = new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes(), d.getSeconds());
        break;
      default:
        throw new IllegalArgumentException("Unsupported Mask type: " + mask);
    }
    
    return get(maskedDate.getTime());
  }
  
  /**
   * Constructs a new date from the specified year, month, and day.
   * 
   * @param yyyy 4-digit year
   * @param month month of the year, where Jan=0, Feb=1, ..., Dec=11
   * @param dayOfMonth The day of the month (first day = 1, second day = 2, ...)
   */
  public static SimpleDate get(int yyyy, int month, int dayOfMonth) {
    return get(new Date(yyyy - 1900, month, dayOfMonth).getTime());
  }
  
  /**
   * Creates a date object corresponding to the specified date/time stamp.
   */
  public static SimpleDate get(double dateTimeStamp) {
    return new SimpleDate(dateTimeStamp);
  }
  
  /**
   * Creates a date object representing the current system date.
   */
  public static SimpleDate now() {
    return get(new Date().getTime());
  }
  
  private SimpleDate(double dateTimeStamp) {
    this.dateTimeStamp = dateTimeStamp;
    this.d = new Date((long) dateTimeStamp);
  }
  
  /**
   * Sets this date object to the specified dateTimeStamp.
   */
  public void setDate(double dateTimeStamp) {
    this.dateTimeStamp = dateTimeStamp;
    d.setTime((long)dateTimeStamp);
  }
  
  /**
   * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT. 
   */
  public double getTime() {
    return dateTimeStamp;
  }
  
  /**
   * Returns the 4-digit year portion of this date.
   */
  public int getYear() {
    return d.getYear() + 1900;
  }

  /**
   * Returns a value in the range [0..11] representing the month of the year.
   */
  public int getMonth() {
    return d.getMonth();
  }

  public int getHours() {
    return d.getHours();
  }
  
  public int getMinutes() {
    return d.getMinutes();
  }
  
  public int getSeconds() {
    return d.getSeconds();
  }
  
  /**
   * Returns the day of the month, where the first day of the month starts with
   * '1'.
   */
  public int getDayOfMonth() {
    return d.getDate();
  }

  public String toString() {
    return d.toString();
  }
  
}
